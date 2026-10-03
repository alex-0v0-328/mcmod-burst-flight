package net.alex.burstflight.client.view;

import net.alex.burstflight.BurstFlight;
import net.alex.burstflight.client.speed.ClientBurstState;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;

/**
 * Widens the view while the local player flies a burst.
 *
 * <p>{@link #onComputeFovModifier} multiplies the vanilla FOV modifier (already widened by flying and sprinting) by
 * {@link FovWidening#getFactor} while {@link ClientBurstState#isBursting} and the player is flying, if
 * {@link ClientConfig} allows it. Vanilla eases the FOV toward each new modifier over a few ticks, so the view widens
 * and narrows smoothly when a burst starts and ends.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

@EventBusSubscriber(modid = BurstFlight.MOD_ID, value = Dist.CLIENT)
public final class FovListener {

    private FovListener() {}

    @SubscribeEvent
    public static void onComputeFovModifier(ComputeFovModifierEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (ClientBurstState.isBursting() && ClientConfig.FOV_EFFECT.get()
                && event.getPlayer() == minecraft.player && event.getPlayer().getAbilities().flying) {
            event.setNewFovModifier(event.getNewFovModifier() * FovWidening.getFactor(
                    ClientConfig.FOV_INCREASE.get(), minecraft.options.fovEffectScale().get()));
        }
    }
}
