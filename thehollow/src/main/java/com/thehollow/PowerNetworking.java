package com.thehollow;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final class PowerNetworking {
    private PowerNetworking() {}

    /** Client -> server: "I picked power N on my relic". The server re-checks everything. */
    public record SelectPowerPayload(int power) implements CustomPayload {
        public static final CustomPayload.Id<SelectPowerPayload> ID =
                new CustomPayload.Id<>(Identifier.of(TheHollow.MOD_ID, "select_power"));
        public static final PacketCodec<RegistryByteBuf, SelectPowerPayload> CODEC =
                PacketCodec.tuple(PacketCodecs.INTEGER, SelectPowerPayload::power, SelectPowerPayload::new);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(SelectPowerPayload.ID, SelectPowerPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SelectPowerPayload.ID,
                (payload, context) -> Powers.activate(context.player(), payload.power()));
    }
}
