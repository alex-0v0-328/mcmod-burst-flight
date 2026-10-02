package net.alex.burstflight.permission;

import net.alex.burstflight.BurstFlight;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * The server config, {@code serverconfig/burst_flight-server.toml} in each world; a modpack ships its defaults in
 * {@code defaultconfigs/}.
 *
 * <p>{@link #MULTIPLIER} is the flying speed during a burst as a multiple of vanilla creative flight, default
 * {@link Multipliers#DEFAULT}, capped at {@link Multipliers#MAX}. {@link #EVERYONE} decides whether players no mod has
 * allowed or denied may burst-fly. Both are read every tick of a burst, so an edit applies at once, mid-flight
 * included. The config screen labels them under {@link #LANG} in {@code en_us.json}.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public final class ServerConfig {

    private static final String LANG = BurstFlight.MOD_ID + ".config.";
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.DoubleValue MULTIPLIER = BUILDER
            .comment("Flying speed during a burst, as a multiple of vanilla creative flight; sprinting doubles it "
                    + "as in creative. A player another mod granted its own multiple keeps that one")
            .translation(LANG + "multiplier")
            .defineInRange("multiplier", Multipliers.DEFAULT, Multipliers.MIN, Multipliers.MAX);
    public static final ModConfigSpec.BooleanValue EVERYONE = BUILDER
            .comment("Whether every player may burst-fly; when false, only players another mod allows can")
            .translation(LANG + "everyone")
            .define("everyone", true);
    public static final ModConfigSpec SPEC = BUILDER.build();

    private ServerConfig() {}
}
