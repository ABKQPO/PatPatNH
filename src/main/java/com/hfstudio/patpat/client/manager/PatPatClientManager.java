package com.hfstudio.patpat.client.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

import com.hfstudio.patpat.client.config.resourcepack.CustomAnimationSettingsConfig;
import com.hfstudio.patpat.client.config.resourcepack.PlayerConfig;
import com.hfstudio.patpat.client.packet.PatPatClientPacketManager;
import com.hfstudio.patpat.client.render.PatAnimationState;
import com.hfstudio.patpat.client.resourcepack.PatPatClientResourcePackManager;
import com.hfstudio.patpat.client.resourcepack.PatPatClientSoundManager;
import com.hfstudio.patpat.common.interaction.PatPatInteractionPolicy;
import com.hfstudio.patpat.config.PatPatConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class PatPatClientManager {

    private static final PatPatClientManager INSTANCE = new PatPatClientManager();
    private static final Map<Integer, PatAnimationState> ACTIVE_ANIMATIONS = new HashMap<>();

    private static int patCooldown;

    private PatPatClientManager() {}

    public static void register() {
        patCooldown = 0;
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance()
            .bus()
            .register(INSTANCE);
    }

    public static boolean isEnabled() {
        return PatPatConfig.main.modEnabled;
    }

    public static int getPatCooldown() {
        return patCooldown;
    }

    public static void setPatCooldown(int cooldown) {
        patCooldown = Math.max(cooldown, 0);
    }

    public static PatAnimationState getAnimationState(EntityLivingBase entity) {
        return ACTIVE_ANIMATIONS.get(entity.getEntityId());
    }

    public static void clearAnimations() {
        ACTIVE_ANIMATIONS.clear();
    }

    public static void playPat(EntityLivingBase entity, PlayerConfig whoPatted, boolean playSound) {
        CustomAnimationSettingsConfig settings = PatPatClientResourcePackManager.INSTANCE
            .getAnimation(entity, whoPatted);
        Integer entityId = entity.getEntityId();
        PatAnimationState animationState = ACTIVE_ANIMATIONS.get(entityId);
        if (animationState == null) {
            animationState = new PatAnimationState(entity.getUniqueID(), settings, whoPatted);
            ACTIVE_ANIMATIONS.put(entityId, animationState);
        } else {
            animationState.reset(settings, whoPatted);
        }
        if (playSound) {
            PatPatClientSoundManager.play(entity, settings.getSoundConfig());
        }
    }

    public static void handleRemotePat(int targetEntityId, int sourceEntityId) {
        Minecraft minecraft = Minecraft.getMinecraft();
        World world = minecraft.theWorld;
        if (world == null) {
            return;
        }

        Entity targetEntity = world.getEntityByID(targetEntityId);
        Entity sourceEntity = world.getEntityByID(sourceEntityId);
        if (!(targetEntity instanceof EntityLivingBase) || !(sourceEntity instanceof EntityPlayer sourcePlayer)) {
            return;
        }

        if (minecraft.thePlayer != null && sourceEntityId == minecraft.thePlayer.getEntityId()) {
            setPatCooldown(PatPatConfig.main.patCooldownTicks);
        }

        playPat(
            (EntityLivingBase) targetEntity,
            PlayerConfig.of(sourcePlayer.getCommandSenderName(), sourcePlayer.getUniqueID()),
            true);
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        if (!(event.entityPlayer instanceof EntityClientPlayerMP)) {
            return;
        }
        if (!(event.target instanceof EntityLivingBase target)) {
            return;
        }
        if (!event.entityPlayer.worldObj.isRemote) {
            return;
        }

        EntityPlayer player = event.entityPlayer;
        if (!canPat(player, target)) {
            return;
        }

        boolean serverUp = PatPatClientPacketManager.isServerAvailable();
        if (shouldUseServerAuthority(serverUp)) {
            setPatCooldown(PatPatConfig.main.patCooldownTicks);
            PatPatClientPacketManager.sendPatToServer(target.getEntityId());
            return;
        }

        PlayerConfig self = PlayerConfig.of(player.getCommandSenderName(), player.getUniqueID());
        playPat(target, self, true);
        setPatCooldown(PatPatConfig.main.patCooldownTicks);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (event.phase != Phase.END) {
            return;
        }
        if (patCooldown > 0) {
            patCooldown--;
        }

        Iterator<Map.Entry<Integer, PatAnimationState>> iterator = ACTIVE_ANIMATIONS.entrySet()
            .iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, PatAnimationState> entry = iterator.next();
            PatAnimationState state = entry.getValue();
            state.tick();
            if (state.isExpired(0F)) {
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    public void onDisconnect(ClientDisconnectionFromServerEvent event) {
        clearAnimations();
        PatPatClientPacketManager.setServerAvailable(false);
        patCooldown = 0;
    }

    private static boolean canPat(EntityPlayer player, EntityLivingBase target) {
        return PatPatInteractionPolicy.canPatClient(
            player,
            target,
            getPatCooldown(),
            PatPatClientPacketManager.isServerAvailable(),
            PatPatConfig.multiplayer.allowClientOnly);
    }

    private static boolean shouldUseServerAuthority(boolean serverAvailable) {
        return serverAvailable && PatPatConfig.multiplayer.enableServerSync;
    }
}
