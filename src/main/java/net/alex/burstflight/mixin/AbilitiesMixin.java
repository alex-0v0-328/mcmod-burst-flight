package net.alex.burstflight.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.alex.burstflight.client.ClientBurstState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Abilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Scales the local player's flying speed during a burst; client only.
 *
 * <p>Vanilla reads {@code Abilities#getFlyingSpeed} for both the horizontal flying speed
 * ({@code Player#getFlyingSpeed}, doubled while sprinting) and the climb and descent speed
 * ({@code LocalPlayer#aiStep}), so one hook scales the whole flight. The class is shared with the integrated server, whose players have their own
 * {@code Abilities}, so only the object belonging to the local player is scaled. The stored speed is never touched,
 * which keeps the multiple out of the save and composes with any mod that changes the base speed.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

@Mixin(Abilities.class)
public abstract class AbilitiesMixin {

    @ModifyReturnValue(method = "getFlyingSpeed", at = @At("RETURN"))
    private float burst_flight$scaleLocalFlight(float speed) {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && player.getAbilities() == (Object) this ? speed * ClientBurstState.multiplier() : speed;
    }
}
