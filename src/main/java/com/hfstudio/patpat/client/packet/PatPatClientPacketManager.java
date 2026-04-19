package com.hfstudio.patpat.client.packet;

import com.hfstudio.patpat.PatPat;
import com.hfstudio.patpat.client.manager.PatPatClientManager;
import com.hfstudio.patpat.common.network.message.HelloS2CPacket;
import com.hfstudio.patpat.common.network.message.PatS2CPacket;
import com.hfstudio.patpat.common.network.message.PatToServerC2SPacket;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public final class PatPatClientPacketManager {

    private static boolean serverAvailable;

    private PatPatClientPacketManager() {}

    public static void register() {
        PatPat.network.registerMessage(HelloHandler.class, HelloS2CPacket.class, 0, Side.CLIENT);
        PatPat.network.registerMessage(PatHandler.class, PatS2CPacket.class, 1, Side.CLIENT);
    }

    public static boolean isServerAvailable() {
        return serverAvailable;
    }

    public static void setServerAvailable(boolean available) {
        serverAvailable = available;
    }

    public static void sendPatToServer(int entityId) {
        PatPat.network.sendToServer(new PatToServerC2SPacket(entityId));
    }

    public static class HelloHandler implements IMessageHandler<HelloS2CPacket, IMessage> {

        @Override
        public IMessage onMessage(HelloS2CPacket message, MessageContext ctx) {
            PatPat.LOG.info(
                "[PatPat] Server has PatPat installed (version: {}). Enabling server sync.",
                message.getServerVersion());
            setServerAvailable(true);
            return null;
        }
    }

    public static class PatHandler implements IMessageHandler<PatS2CPacket, IMessage> {

        @Override
        public IMessage onMessage(PatS2CPacket message, MessageContext ctx) {
            PatPatClientManager.handleRemotePat(message.getTargetEntityId(), message.getSourceEntityId());
            return null;
        }
    }
}
