package net.alex.burstflight.session;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.alex.burstflight.BurstFlight;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * The per-player state of a burst, all server side.
 *
 * <p>{@link #BURSTING} marks a burst in progress; it is saved, so a player who logs out midair is still flying on
 * return, but not kept through death. {@link #SENT_MULTIPLIER} is the multiple the client was last told; it is never
 * saved or copied, so a fresh player object (login, respawn) starts at 0 and gets the multiple resent.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public final class SessionAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, BurstFlight.MOD_ID);
    public static final Supplier<AttachmentType<Boolean>> BURSTING = ATTACHMENT_TYPES.register("bursting",
            () -> AttachmentType.builder(() -> Boolean.TRUE).serialize(Codec.BOOL).build());
    public static final Supplier<AttachmentType<Float>> SENT_MULTIPLIER = ATTACHMENT_TYPES.register(
            "sent_multiplier", () -> AttachmentType.builder(() -> 0.0F).build());

    private SessionAttachments() {}
}
