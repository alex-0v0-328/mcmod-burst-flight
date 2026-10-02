package net.alex.burstflight;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

/**
 * Entry point of Burst Flight: double-tapping the sprint key starts creative-style flight at a multiple of the
 * vanilla flying speed, and double-tapping it again or touching the ground ends it ({@link BurstController}).
 *
 * <p>Both sides need the mod: the client detects the double tap and scales its own flight
 * ({@link net.alex.burstflight.client.BurstFlightClient}), the server decides who may fly and grants the flight
 * ({@link BurstController}). Registers the server config {@link BurstFlightConfig}, the player attachments
 * {@link BurstAttachments} and the two payloads {@link BurstNetwork}; the client config and the config screen belong
 * to the client-only entry point {@link net.alex.burstflight.client.BurstFlightClientMod}. Other mods use
 * {@link net.alex.burstflight.api.BurstFlightApi}.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

@Mod(BurstFlight.MOD_ID)
public class BurstFlight {

    public static final String MOD_ID = "burst_flight";

    public BurstFlight(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, BurstFlightConfig.SPEC);
        BurstAttachments.ATTACHMENT_TYPES.register(modEventBus);
        modEventBus.addListener(BurstNetwork::register);
    }
}
