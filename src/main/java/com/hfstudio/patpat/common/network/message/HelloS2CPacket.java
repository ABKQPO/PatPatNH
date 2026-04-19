package com.hfstudio.patpat.common.network.message;

import java.nio.charset.StandardCharsets;

import com.hfstudio.patpat.PatPat;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

/**
 * Sent by the server to the client on login to signal that PatPat is installed
 * server-side. Carries the server's mod version string so the client can log it.
 */
public class HelloS2CPacket implements IMessage {

    private String serverVersion;

    /** No-arg constructor required by SimpleNetworkWrapper. */
    public HelloS2CPacket() {
        this.serverVersion = PatPat.VERSION;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] bytes = serverVersion.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int len = buf.readInt();
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        this.serverVersion = new String(bytes, StandardCharsets.UTF_8);
    }
}
