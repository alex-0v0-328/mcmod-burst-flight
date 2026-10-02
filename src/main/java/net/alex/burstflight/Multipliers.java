package net.alex.burstflight;

import net.alex.burstflight.api.BurstFlightApi;
import org.jetbrains.annotations.Nullable;

/**
 * Pure multiplier rules, kept free of game state so plain JUnit covers them.
 *
 * <p>{@link #resolve} turns a player's grant into the multiplier in force: no grant follows the server config
 * ({@code everyone}, then its multiplier), a denial gives 0, and an allowance uses its own multiple or, without one,
 * the configured multiple. 0 means the player may not burst-fly. Every multiple in force passes {@link #clamp}, which
 * keeps it inside {@link BurstFlightApi#MIN_MULTIPLIER}..{@link BurstFlightApi#MAX_MULTIPLIER}: above about 9.9 a
 * sprinting burst outruns the vanilla server's "moved too quickly" check and rubber-bands.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public final class Multipliers {

    private Multipliers() {}

    public static double clamp(double multiplier) {
        return Math.clamp(multiplier, BurstFlightApi.MIN_MULTIPLIER, BurstFlightApi.MAX_MULTIPLIER);
    }

    public static double resolve(@Nullable Boolean allowed, @Nullable Double granted, boolean everyone,
                                 double configured) {
        if (allowed == null) {
            return everyone ? clamp(configured) : 0.0;
        }
        if (!allowed) {
            return 0.0;
        }
        return clamp(granted == null ? configured : granted);
    }
}
