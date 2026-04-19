package com.hfstudio.patpat.server.packet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;

import com.hfstudio.patpat.PatPat;
import com.hfstudio.patpat.common.network.message.HelloS2CPacket;
import com.hfstudio.patpat.common.network.message.PatS2CPacket;
import com.hfstudio.patpat.common.network.message.PatToServerC2SPacket;
import com.hfstudio.patpat.config.PatPatConfig;
import com.hfstudio.patpat.server.PatPatServerRateLimitManager;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public final class PatPatServerPacketManager {

    private static final ServerEvents EVENTS = new ServerEvents();

    private PatPatServerPacketManager() {}

    public static void register() {
        PatPat.network.registerMessage(PatHandler.class, PatToServerC2SPacket.class, 2, Side.SERVER);
        FMLCommonHandler.instance()
            .bus()
            .register(EVENTS);
    }

    private static boolean canPat(EntityPlayerMP sender, EntityLivingBase target) {
        if (!PatPatConfig.main.modEnabled) {
            return false;
        }
        if (PatPatConfig.main.requireSneaking && !sender.isSneaking()) {
            return false;
        }
        if (PatPatConfig.main.requireEmptyMainHand && sender.getHeldItem() != null) {
            return false;
        }
        if (target.isInvisible() || target.isDead) {
            return false;
        }
        if (sender.getDistanceSqToEntity(target) > 36.0D) {
            return false;
        }
        return PatPatServerRateLimitManager.tryPat(sender);
    }

    public static class ServerEvents {

        @SubscribeEvent
        public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
            if (event.player instanceof EntityPlayerMP) {
                PatPat.network.sendTo(new HelloS2CPacket(), (EntityPlayerMP) event.player);
            }
        }

        @SubscribeEvent
        public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
            PatPatServerRateLimitManager.removePlayer(event.player.getUniqueID());
        }
    }

    public static class PatHandler implements IMessageHandler<PatToServerC2SPacket, IMessage> {

        @Override
        public IMessage onMessage(PatToServerC2SPacket message, MessageContext ctx) {
            EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
            Entity entity = sender.worldObj.getEntityByID(message.getTargetEntityId());
            if (!(entity instanceof EntityLivingBase living)) {
                return null;
            }

            if (!canPat(sender, living)) {
                return null;
            }

            PatS2CPacket broadcast = new PatS2CPacket(message.getTargetEntityId(), sender.getEntityId());
            for (Object playerObj : sender.worldObj.playerEntities) {
                if (!(playerObj instanceof EntityPlayerMP otherPlayer)) {
                    continue;
                }
                if (otherPlayer == sender) {
                    continue;
                }
                if (otherPlayer.dimension != sender.dimension) {
                    continue;
                }
                if (otherPlayer.getDistanceSqToEntity(living) > 4096.0D) {
                    continue;
                }
                PatPat.network.sendTo(broadcast, otherPlayer);
            }
            return null;
        }
    }
}
