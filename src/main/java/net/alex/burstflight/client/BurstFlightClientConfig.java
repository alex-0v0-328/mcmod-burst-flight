package net.alex.burstflight.client;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * The client config, {@code config/burst_flight-client.toml}: each player's own view during a burst.
 *
 * <p>{@link #FOV_EFFECT} turns the wider view on or off; {@link #FOV_INCREASE} is how much wider, in percent
 * ({@link FovWidening}). Both are read every frame, so an edit applies at once. Labels and tooltips for the in-game
 * config screen come from {@code en_us.json} only (Alex, 2026-10-02).
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public final class BurstFlightClientConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue FOV_EFFECT = BUILDER
            .comment("Whether the field of view widens during a burst")
            .define("fovEffect", true);
    public static final ModConfigSpec.IntValue FOV_INCREASE = BUILDER
            .comment("How much wider the field of view gets during a burst, in percent; the FOV Effects slider "
                    + "scales it, and Minecraft caps the total widening, sprint and flight included, at 50 percent")
            .defineInRange("fovIncrease", 15, 0, 50);
    public static final ModConfigSpec SPEC = BUILDER.build();

    private BurstFlightClientConfig() {}
}
