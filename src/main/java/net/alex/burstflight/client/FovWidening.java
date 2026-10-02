package net.alex.burstflight.client;

/**
 * Pure field-of-view math, kept free of game state so plain JUnit covers it.
 *
 * <p>{@link #factor} is what the FOV modifier is multiplied by during a burst: 1 plus the configured percent, eased
 * toward 1 by the vanilla FOV Effects slider ({@code effectScale}, 0..1) the same way NeoForge eases the vanilla
 * modifier, so a player who turned FOV effects off for comfort sees no widening here either.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public final class FovWidening {

    private FovWidening() {}

    public static float factor(int percent, double effectScale) {
        return (float) (1.0 + percent / 100.0 * effectScale);
    }
}
