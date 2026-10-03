package net.alex.burstflight.api;

import net.alex.burstflight.permission.GrantAttachment;
import net.alex.burstflight.permission.Multipliers;
import net.alex.burstflight.session.SessionController;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * The public API for other mods: who may burst-fly, and how fast. Server side only; every call takes a
 * {@link ServerPlayer}. A thin facade: the rules live in {@code permission}, the burst in {@code session}, and this
 * class is the only one other mods should touch.
 *
 * <p>Without any call, the server config decides: everyone may burst-fly at {@link #DEFAULT_MULTIPLIER} times
 * vanilla creative flight unless a modpack changed it. {@link #allow(ServerPlayer)} lets a player burst-fly at the
 * configured multiple, {@link #allow(ServerPlayer, double)} at its own multiple, which is clamped to
 * {@link #MIN_MULTIPLIER}..{@link #MAX_MULTIPLIER} and must be finite. {@link #deny} forbids it, and {@link #reset}
 * hands the player back to the config. A decision is saved with the player and survives death, so a mod calls once,
 * not every tick; a later call replaces it. A burst in progress follows a change within one tick: a denial ends it,
 * a new multiple applies mid-flight.
 *
 * <p>{@link #getMultiplier} gives the multiple in force for a player, 0 when the player may not burst-fly.
 * {@link #isBursting} tells whether a burst is in progress.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public final class BFApi {

    public static final double DEFAULT_MULTIPLIER = Multipliers.DEFAULT;
    public static final double MIN_MULTIPLIER = Multipliers.MIN;
    public static final double MAX_MULTIPLIER = Multipliers.MAX;

    private BFApi() {}

    public static void allow(@NotNull ServerPlayer player) {
        GrantAttachment.allow(player);
    }

    public static void allow(@NotNull ServerPlayer player, double multiplier) {
        GrantAttachment.allow(player, multiplier);
    }

    public static void deny(@NotNull ServerPlayer player) {
        GrantAttachment.deny(player);
    }

    public static void reset(@NotNull ServerPlayer player) {
        GrantAttachment.reset(player);
    }

    public static double getMultiplier(@NotNull ServerPlayer player) {
        return GrantAttachment.getMultiplier(player);
    }

    public static boolean isBursting(@NotNull ServerPlayer player) {
        return SessionController.isBursting(player);
    }
}
