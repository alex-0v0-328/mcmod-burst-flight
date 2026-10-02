package net.alex.burstflight.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.alex.burstflight.BurstFlight;
import net.alex.burstflight.BurstNetwork;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/**
 * The client half: watches the sprint key and asks the server to toggle a burst on a double tap, and widens the view
 * while a burst flies.
 *
 * <p>Presses come from the raw key and mouse events, matched against whatever the sprint key is bound to, because
 * the key mapping itself only tells whether the key is down and, with Sprint set to Toggle, not even that.
 * {@link #DOUBLE_TAP_TICKS} is vanilla's window for its own double taps (forward to sprint, jump to fly), counted in
 * client ticks ({@link #ticks}). Presses with a screen open do not count. The server decides the rest, so a refused
 * request (no permission, riding, spectating) simply does nothing.
 *
 * <p>{@link #onComputeFovModifier} multiplies the vanilla FOV modifier (already widened by flying and sprinting) by
 * {@link FovWidening#factor} while the local player is in a burst and flying, if {@link BurstFlightClientConfig}
 * allows it. Vanilla eases the FOV toward each new modifier over a few ticks, so the view widens and narrows smoothly
 * when a burst starts and ends.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

@EventBusSubscriber(modid = BurstFlight.MOD_ID, value = Dist.CLIENT)
public final class BurstFlightClient {

    private static final int DOUBLE_TAP_TICKS = 7;
    private static final DoubleTap SPRINT_TAPS = new DoubleTap(DOUBLE_TAP_TICKS);
    private static long ticks;

    private BurstFlightClient() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        ticks++;
    }

    @SubscribeEvent
    public static void onKey(InputEvent.Key event) {
        if (event.getAction() == GLFW.GLFW_PRESS) {
            onPress(InputConstants.getKey(event.getKey(), event.getScanCode()));
        }
    }

    @SubscribeEvent
    public static void onMouseButton(InputEvent.MouseButton.Post event) {
        if (event.getAction() == GLFW.GLFW_PRESS) {
            onPress(InputConstants.Type.MOUSE.getOrCreate(event.getButton()));
        }
    }

    @SubscribeEvent
    public static void onComputeFovModifier(ComputeFovModifierEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (ClientBurstState.isActive() && BurstFlightClientConfig.FOV_EFFECT.get()
                && event.getPlayer() == minecraft.player && event.getPlayer().getAbilities().flying) {
            event.setNewFovModifier(event.getNewFovModifier() * FovWidening.factor(
                    BurstFlightClientConfig.FOV_INCREASE.get(), minecraft.options.fovEffectScale().get()));
        }
    }

    @SubscribeEvent
    public static void onClone(ClientPlayerNetworkEvent.Clone event) {
        ClientBurstState.reset();
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientBurstState.reset();
    }

    private static void onPress(InputConstants.Key key) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.screen != null
                || !minecraft.options.keySprint.isActiveAndMatches(key)) {
            return;
        }
        if (SPRINT_TAPS.press(ticks)) {
            PacketDistributor.sendToServer(BurstNetwork.Toggle.INSTANCE);
        }
    }
}
