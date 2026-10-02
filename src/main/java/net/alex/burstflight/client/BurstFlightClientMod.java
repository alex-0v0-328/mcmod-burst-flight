package net.alex.burstflight.client;

import net.alex.burstflight.BurstFlight;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * The client-only entry point beside {@link BurstFlight}: registers the client config
 * {@link BurstFlightClientConfig} and NeoForge's generated config screen, which the mod list's Config button opens.
 * The screen shows the client settings and, in singleplayer, the server settings too.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

@Mod(value = BurstFlight.MOD_ID, dist = Dist.CLIENT)
public final class BurstFlightClientMod {

    public BurstFlightClientMod(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, BurstFlightClientConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
