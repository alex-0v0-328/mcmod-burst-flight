package net.alex.burstflight.client;

import net.alex.burstflight.BurstFlight;
import net.alex.burstflight.client.speed.ClientBurstState;
import net.alex.burstflight.client.view.ClientConfig;
import net.alex.burstflight.session.SessionPayloads;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * The client-only entry point beside {@link BurstFlight}: registers the client config {@link ClientConfig} and
 * NeoForge's generated config screen, which the mod list's Config button opens (client settings and, in
 * singleplayer, the server settings too), and plugs {@link ClientBurstState#receive} into the server's state payload.
 * The client features themselves ({@code input}, {@code speed}, {@code view}) subscribe to their own events.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

@Mod(value = BurstFlight.MOD_ID, dist = Dist.CLIENT)
public final class BFClientMod {

    public BFClientMod(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        SessionPayloads.onState(ClientBurstState::receive);
    }
}
