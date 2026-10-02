package net.alex.burstflight.api;

import java.util.Optional;
import net.alex.burstflight.BurstAttachments;
import net.alex.burstflight.BurstFlightConfig;
import net.alex.burstflight.BurstGrant;
import net.alex.burstflight.Multipliers;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * The public API for other mods: who may burst-fly, and how fast. Server side only; every call takes a
 * {@link ServerPlayer}.
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

public final class BurstFlightApi {

    public static final double DEFAULT_MULTIPLIER = 3.0;
    public static final double MIN_MULTIPLIER = 1.0;
    public static final double MAX_MULTIPLIER = 8.0;

    private BurstFlightApi() {}

    public static void allow(@NotNull ServerPlayer player) {
        player.setData(BurstAttachments.GRANT, BurstGrant.ALLOWED);
    }

    public static void allow(@NotNull ServerPlayer player, double multiplier) {
        if (!Double.isFinite(multiplier)) {
            throw new IllegalArgumentException("burst flight multiplier must be finite, got " + multiplier);
        }
        player.setData(BurstAttachments.GRANT, new BurstGrant(true, Optional.of(Multipliers.clamp(multiplier))));
    }

    public static void deny(@NotNull ServerPlayer player) {
        player.setData(BurstAttachments.GRANT, BurstGrant.DENIED);
    }

    public static void reset(@NotNull ServerPlayer player) {
        player.removeData(BurstAttachments.GRANT);
    }

    public static double getMultiplier(@NotNull ServerPlayer player) {
        BurstGrant grant = player.getExistingDataOrNull(BurstAttachments.GRANT);
        return Multipliers.resolve(grant == null ? null : grant.allowed(),
                grant == null ? null : grant.multiplier().orElse(null), BurstFlightConfig.EVERYONE.get(),
                BurstFlightConfig.MULTIPLIER.get());
    }

    public static boolean isBursting(@NotNull ServerPlayer player) {
        return player.hasData(BurstAttachments.BURSTING);
    }
}
