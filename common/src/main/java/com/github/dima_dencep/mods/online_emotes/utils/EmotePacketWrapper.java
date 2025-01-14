package com.github.dima_dencep.mods.online_emotes.utils;

import com.github.dima_dencep.mods.online_emotes.EmotesExpectPlatform;
import com.github.dima_dencep.mods.online_emotes.OnlineEmotes;
import io.netty.buffer.Unpooled;
import io.netty.channel.local.LocalAddress;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import net.minecraft.client.network.ClientPlayerEntity;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class EmotePacketWrapper {
    public byte[] emotePacket;
    public String playerName;
    public UUID playerUUID;
    public String serverAddress;
    public String modVersion;

    public EmotePacketWrapper(byte[] emotePacket) {
        this.modVersion = EmotesExpectPlatform.getModVersion();
        this.emotePacket = emotePacket;

        ClientPlayerEntity player = OnlineEmotes.client.player;
        if (player != null) {
            this.playerName = player.getEntityName();
            this.playerUUID = player.getUuid();

            if (player.networkHandler != null) {
                this.serverAddress = getIP(player.networkHandler.connection.getAddress());
            }
        }
    }

    public BinaryWebSocketFrame toWebSocketFrame() {
        return new BinaryWebSocketFrame(Unpooled.wrappedBuffer(OnlineEmotes.gson.toJson(this).getBytes(StandardCharsets.UTF_8)));
    }

    private static String getIP(SocketAddress address) {
        if (address instanceof LocalAddress) {
            return null;

        } else if (address instanceof InetSocketAddress inetSocketAddress) {
            return inetSocketAddress.getAddress().getHostAddress();
        }

        return address.toString();
    }
}
