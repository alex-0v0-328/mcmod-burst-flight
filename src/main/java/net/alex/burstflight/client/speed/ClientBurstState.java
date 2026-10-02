package net.alex.burstflight.client.speed;

import net.alex.burstflight.BurstFlight;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The local player's burst as the server last described it.
 *
 * <p>{@link #receive} takes the multiple from the server's state payload, 0 meaning no burst; the client entry point
 * plugs it into {@link net.alex.burstflight.session.SessionPayloads#onState}. When a burst starts on the ground it
 * jumps the way vanilla's creative double jump does, before the abilities packet that turns flying on arrives, so the
 * client's landing check does not end the flight at once. {@link #multiplier} is what
 * {@link net.alex.burstflight.client.speed.mixin.LocalPlayerMixin} scales the local flying speed by: 1 outside a
 * burst. {@link #isActive} tells {@link net.alex.burstflight.client.view.FovListener} when to widen the view. The
 * state resets when the local player object is replaced ({@link #onClone}) or the client leaves the world
 * ({@link #onLoggingOut}); the server resends the multiple after a dimension change.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

@EventBusSubscriber(modid = BurstFlight.MOD_ID, value = Dist.CLIENT)
public final class ClientBurstState {

    private static float multiplier = 1.0F;
    private static boolean active;

    private ClientBurstState() {}

    public static boolean isActive() {
        return active;
    }

    public static float multiplier() {
        return multiplier;
    }

    public static void receive(@NotNull Player player, float newMultiplier) {
        boolean starting = !active && newMultiplier > 0.0F;
        active = newMultiplier > 0.0F;
        multiplier = active ? newMultiplier : 1.0F;
        if (starting && player.onGround()) {
            player.jumpFromGround();
        }
    }

    @SubscribeEvent
    public static void onClone(ClientPlayerNetworkEvent.Clone event) {
        reset();
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        reset();
    }

    private static void reset() {
        active = false;
        multiplier = 1.0F;
    }
}
