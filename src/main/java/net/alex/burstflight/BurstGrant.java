package net.alex.burstflight;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

/**
 * What another mod decided for one player through {@link net.alex.burstflight.api.BurstFlightApi}: allowed or
 * denied, and for an allowance optionally its own multiple. Without a multiple an allowance follows the server
 * config's, so a later config edit still reaches it. Stored in {@link BurstAttachments#GRANT}; {@link Multipliers}
 * resolves it.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public record BurstGrant(boolean allowed, Optional<Double> multiplier) {

    public static final BurstGrant ALLOWED = new BurstGrant(true, Optional.empty());
    public static final BurstGrant DENIED = new BurstGrant(false, Optional.empty());
    public static final Codec<BurstGrant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("allowed").forGetter(BurstGrant::allowed),
            Codec.DOUBLE.optionalFieldOf("multiplier").forGetter(BurstGrant::multiplier)
    ).apply(instance, BurstGrant::new));
}
