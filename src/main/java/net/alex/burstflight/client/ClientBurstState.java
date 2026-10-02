package net.alex.burstflight.client;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The local player's burst as the server last described it; plain fields with no client-only types, since the
 * common payload registration refers to it.
 *
 * <p>{@link #receive} takes the multiple from the server, 0 meaning no burst. When a burst starts on the ground it
 * jumps the way vanilla's creative double jump does, before the abilities packet that turns flying on arrives, so
 * the client's landing check does not end the flight at once. {@link #multiplier} is what
 * {@link net.alex.burstflight.mixin.AbilitiesMixin} scales the local flying speed by: 1 outside a burst.
 * {@link #isActive} tells {@link BurstFlightClient} when to widen the view.
 * {@link #reset} runs when the local player object is replaced or the client leaves the world; the server resends
 * the multiple after a dimension change.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

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

    public static void reset() {
        active = false;
        multiplier = 1.0F;
    }
}
