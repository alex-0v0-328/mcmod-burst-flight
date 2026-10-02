package net.alex.burstflight;

import net.alex.burstflight.api.BurstFlightApi;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

/**
 * The server half of a burst: starts it on a double tap, keeps it alive, ends it.
 *
 * <p>{@link #start} needs a multiple above 0 ({@link BurstFlightApi#getMultiplier}) and a player who could fly at
 * all ({@link #canFly}: alive, not a spectator, not riding, not asleep). It lets the player fly through a transient
 * modifier on NeoForge's {@code creative_flight} attribute ({@link #FLIGHT}) rather than {@code Abilities#mayfly},
 * so it never takes away flight another mod or the game mode granted, and turns flying on. The client is told the
 * multiple before the abilities packet, so a player standing on the ground jumps first and the client's landing check
 * does not end the flight on its first tick ({@link net.alex.burstflight.client.ClientBurstState#receive}).
 *
 * <p>The speed itself is never written to {@code Abilities}, which the game saves: only the client scales its own
 * flight, by the multiple from {@link #send}. The server needs no speed, since it does not simulate player movement.
 *
 * <p>{@link #tick} runs every server tick of a burst and ends it once the player stops flying (the client lands, or
 * vanilla's double jump turns flying off), the multiple drops to 0 (a mod denied it, or the config stopped allowing
 * everyone) or the player can no longer fly. Otherwise it reasserts the modifier, which a load from disk or a respawn
 * through the End portal leaves behind, and resends a changed multiple. {@link #stop} removes the modifier and, when
 * nothing else lets the player fly, turns flying off, so a survival player falls from where the burst ended; a
 * creative player keeps flying at normal speed. It always resends the abilities: a client that turned flying back on
 * with vanilla's double jump in the tick before the modifier's removal reached it would otherwise keep flying
 * unauthorized. A player logging out mid-burst keeps it ({@link BurstAttachments}).
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

@EventBusSubscriber(modid = BurstFlight.MOD_ID)
public final class BurstController {

    private static final ResourceLocation FLIGHT_ID = ResourceLocation.fromNamespaceAndPath(BurstFlight.MOD_ID,
            "burst");
    private static final AttributeModifier FLIGHT = new AttributeModifier(FLIGHT_ID, 1.0,
            AttributeModifier.Operation.ADD_VALUE);

    private BurstController() {}

    public static void toggle(@NotNull ServerPlayer player) {
        if (BurstFlightApi.isBursting(player)) {
            stop(player);
        } else {
            start(player);
        }
    }

    public static boolean start(@NotNull ServerPlayer player) {
        double multiplier = BurstFlightApi.getMultiplier(player);
        if (multiplier <= 0.0 || !canFly(player)) {
            return false;
        }
        player.setData(BurstAttachments.BURSTING, true);
        grantFlight(player);
        send(player, (float) multiplier);
        player.getAbilities().flying = true;
        player.onUpdateAbilities();
        return true;
    }

    public static void stop(@NotNull ServerPlayer player) {
        player.removeData(BurstAttachments.BURSTING);
        AttributeInstance flight = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
        if (flight != null) {
            flight.removeModifier(FLIGHT_ID);
        }
        send(player, 0.0F);
        if (!player.mayFly()) {
            player.getAbilities().flying = false;
        }
        player.onUpdateAbilities();
    }

    public static void tick(@NotNull ServerPlayer player) {
        if (!BurstFlightApi.isBursting(player)) {
            return;
        }
        float multiplier = (float) BurstFlightApi.getMultiplier(player);
        if (multiplier <= 0.0F || !canFly(player) || !player.getAbilities().flying) {
            stop(player);
            return;
        }
        grantFlight(player);
        if (player.getData(BurstAttachments.SENT_MULTIPLIER) != multiplier) {
            send(player, multiplier);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            tick(player);
        }
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.removeData(BurstAttachments.SENT_MULTIPLIER);
        }
    }

    private static boolean canFly(ServerPlayer player) {
        return player.isAlive() && !player.isSpectator() && !player.isPassenger() && !player.isSleeping();
    }

    private static void grantFlight(ServerPlayer player) {
        AttributeInstance flight = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
        if (flight != null && !flight.hasModifier(FLIGHT_ID)) {
            flight.addTransientModifier(FLIGHT);
        }
    }

    private static void send(ServerPlayer player, float multiplier) {
        player.setData(BurstAttachments.SENT_MULTIPLIER, multiplier);
        PacketDistributor.sendToPlayer(player, new BurstNetwork.State(multiplier));
    }
}
