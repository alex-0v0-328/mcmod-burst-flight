package net.alex.burstflight.session;

import io.netty.buffer.ByteBuf;
import java.util.function.BiConsumer;
import net.alex.burstflight.BurstFlight;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;

/**
 * The two payloads of a burst, both handled on the main thread. {@link Toggle} goes to the server when the client
 * sees a double tap, and {@link SessionController#toggle} decides. {@link State} tells the client the multiple now in
 * force, 0 once a burst ends.
 *
 * <p>NeoForge 21.1 registers both directions in common code, so the client half plugs its {@link State} handler in
 * through {@link #onState} from its own entry point; this package never refers to client code, and on a dedicated
 * server the handler stays a no-op.
 *
 * <p>Both are required channels, so a client without the mod cannot join a server that has it, and the reverse; the
 * client half alone could not fly, and the server half alone would never hear a double tap.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public final class SessionPayloads {

    private static BiConsumer<Player, Float> stateHandler = (player, multiplier) -> {};

    private SessionPayloads() {}

    public static void onState(@NotNull BiConsumer<Player, Float> handler) {
        stateHandler = handler;
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(Toggle.TYPE, Toggle.STREAM_CODEC, (payload, context) -> {
            if (context.player() instanceof ServerPlayer player) {
                SessionController.toggle(player);
            }
        });
        registrar.playToClient(State.TYPE, State.STREAM_CODEC,
                (payload, context) -> stateHandler.accept(context.player(), payload.multiplier()));
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
