package net.alex.burstflight;

import io.netty.buffer.ByteBuf;
import net.alex.burstflight.client.ClientBurstState;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;

/**
 * The two payloads, both handled on the main thread. {@link Toggle} goes to the server when the client sees a
 * double tap, and the server decides ({@link BurstController#toggle}). {@link State} tells the client the multiple
 * now in force, 0 once a burst ends ({@link ClientBurstState#receive}).
 *
 * <p>Both are required channels, so a client without the mod cannot join a server that has it, and the reverse; the
 * client half alone could not fly, and the server half alone would never hear a double tap.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public final class BurstNetwork {

    private BurstNetwork() {}

    static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(Toggle.TYPE, Toggle.STREAM_CODEC, (payload, context) -> {
            if (context.player() instanceof ServerPlayer player) {
                BurstController.toggle(player);
            }
        });
        registrar.playToClient(State.TYPE, State.STREAM_CODEC,
                (payload, context) -> ClientBurstState.receive(context.player(), payload.multiplier()));
    }

    public record Toggle() implements CustomPacketPayload {

        public static final Toggle INSTANCE = new Toggle();
        public static final Type<Toggle> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(BurstFlight.MOD_ID, "toggle"));
        public static final StreamCodec<ByteBuf, Toggle> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public @NotNull Type<Toggle> type() {
            return TYPE;
        }
    }

    public record State(float multiplier) implements CustomPacketPayload {

        public static final Type<State> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(BurstFlight.MOD_ID, "state"));
        public static final StreamCodec<ByteBuf, State> STREAM_CODEC =
                ByteBufCodecs.FLOAT.map(State::new, State::multiplier);

        @Override
        public @NotNull Type<State> type() {
            return TYPE;
        }
    }
}
