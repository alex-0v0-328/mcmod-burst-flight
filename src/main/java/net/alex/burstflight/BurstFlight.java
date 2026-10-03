package net.alex.burstflight;

import net.alex.burstflight.permission.GrantAttachment;
import net.alex.burstflight.permission.ServerConfig;
import net.alex.burstflight.session.SessionAttachments;
import net.alex.burstflight.session.SessionPayloads;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

/**
 * Entry point of Burst Flight: double-tapping the sprint key starts creative-style flight at a multiple of the
 * vanilla flying speed, and double-tapping it again or touching the ground ends it. It only registers; each feature
 * lives in its own package, and dependencies point one way: {@code client} → {@code session} → {@code permission},
 * with {@code api} on top of both.
 *
 * <ul>
 *   <li>{@code permission}: who may fly and how fast; the {@link ServerConfig} and each player's grant
 *   ({@link GrantAttachment}).</li>
 *   <li>{@code session}: the server half of one burst ({@link net.alex.burstflight.session.SessionController}), its
 *   per-player state ({@link SessionAttachments}) and the two payloads ({@link SessionPayloads}).</li>
 *   <li>{@code client}: its own entry point {@link net.alex.burstflight.client.BFClientMod}, the sprint double tap
 *   ({@code input}), the scaled flight ({@code speed}) and the wider view ({@code view}).</li>
 *   <li>{@code api}: {@link net.alex.burstflight.api.BFApi}, the one class other mods use.</li>
 * </ul>
 *
 * <p>Both sides need the mod: the client detects the double tap and scales its own flight, the server decides who may
 * fly and grants the flight.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

@Mod(BurstFlight.MOD_ID)
public class BurstFlight {

    public static final String MOD_ID = "burst_flight";

    public BurstFlight(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        GrantAttachment.ATTACHMENT_TYPES.register(modEventBus);
        SessionAttachments.ATTACHMENT_TYPES.register(modEventBus);
        modEventBus.addListener(SessionPayloads::onRegisterPayloadHandlers);
    }
}
