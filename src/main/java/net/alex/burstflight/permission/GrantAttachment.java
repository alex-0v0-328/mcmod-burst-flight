package net.alex.burstflight.permission;

import java.util.Optional;
import java.util.function.Supplier;
import net.alex.burstflight.BurstFlight;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

/**
 * Where a player's {@link Grant} lives, and the only code that reads or writes it.
 *
 * <p>{@link #GRANT} is saved with the player and survives death, so a mod decides once. {@link #allow},
 * {@link #deny} and {@link #reset} write it; {@link #multiplier} resolves it with the {@link ServerConfig} into the
 * multiple in force, 0 meaning the player may not burst-fly. An own multiple is clamped when written and must be
 * finite.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public final class GrantAttachment {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, BurstFlight.MOD_ID);
    public static final Supplier<AttachmentType<Grant>> GRANT = ATTACHMENT_TYPES.register("grant",
            () -> AttachmentType.builder(() -> Grant.ALLOWED).serialize(Grant.CODEC).copyOnDeath().build());

    private GrantAttachment() {}

    public static void allow(@NotNull ServerPlayer player) {
        player.setData(GRANT, Grant.ALLOWED);
    }

    public static void allow(@NotNull ServerPlayer player, double multiplier) {
        if (!Double.isFinite(multiplier)) {
            throw new IllegalArgumentException("burst flight multiplier must be finite, got " + multiplier);
        }
        player.setData(GRANT, new Grant(true, Optional.of(Multipliers.clamp(multiplier))));
    }

    public static void deny(@NotNull ServerPlayer player) {
        player.setData(GRANT, Grant.DENIED);
    }

    public static void reset(@NotNull ServerPlayer player) {
        player.removeData(GRANT);
    }

    public static double multiplier(@NotNull ServerPlayer player) {
        Grant grant = player.getExistingDataOrNull(GRANT);
        return Multipliers.resolve(grant == null ? null : grant.allowed(),
                grant == null ? null : grant.multiplier().orElse(null), ServerConfig.EVERYONE.get(),
                ServerConfig.MULTIPLIER.get());
    }
}
