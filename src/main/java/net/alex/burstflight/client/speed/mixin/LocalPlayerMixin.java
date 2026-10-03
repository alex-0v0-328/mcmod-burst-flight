package net.alex.burstflight.client.speed.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import net.alex.burstflight.client.speed.ClientBurstState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Scales the local player's flight during a burst; client only.
 *
 * <p>Two readings of the flying speed move a flying player: the horizontal one, {@code Player#getFlyingSpeed}
 * (doubled while sprinting), and the climb and descent one inside {@code LocalPlayer#aiStep}. {@link #getFlyingSpeed}
 * overrides the first for {@code LocalPlayer} alone and {@link #burst_flight$scaleClimb} scales the second, both by
 * {@link ClientBurstState#getMultiplier}. Only the local player runs this physics, so the multiple never reaches other
 * players, the integrated server's players or the save. The constructor only satisfies the compiler; Mixin discards
 * it.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {

    private LocalPlayerMixin(ClientLevel level, GameProfile profile) {
        super(level, profile);
    }

    @Override
    protected float getFlyingSpeed() {
        float speed = super.getFlyingSpeed();
        return getAbilities().flying ? speed * ClientBurstState.getMultiplier() : speed;
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Abilities;getFlyingSpeed()F"))
    private float burst_flight$scaleClimb(float speed) {
        return speed * ClientBurstState.getMultiplier();
    }
}
