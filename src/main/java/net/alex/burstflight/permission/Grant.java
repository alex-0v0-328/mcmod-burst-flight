package net.alex.burstflight.permission;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

/**
 * What another mod decided for one player through the public API: allowed or denied, and for an allowance optionally
 * its own multiple. Without a multiple an allowance follows the server config's, so a later
 * config edit still reaches it. Stored by {@link GrantAttachment}; {@link Multipliers} resolves it.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public record Grant(boolean allowed, Optional<Double> multiplier) {

    public static final Grant ALLOWED = new Grant(true, Optional.empty());
    public static final Grant DENIED = new Grant(false, Optional.empty());
    public static final Codec<Grant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("allowed").forGetter(Grant::allowed),
            Codec.DOUBLE.optionalFieldOf("multiplier").forGetter(Grant::multiplier)
    ).apply(instance, Grant::new));
}
