package com.hfstudio.patpat.client.resourcepack;

import java.util.Random;

import net.minecraft.entity.Entity;

import com.hfstudio.patpat.client.config.resourcepack.SoundConfig;
import com.hfstudio.patpat.config.PatPatConfig;

public final class PatPatClientSoundManager {

    private static final Random RANDOM = new Random();

    private PatPatClientSoundManager() {}

    public static void preInit() {}

    public static void play(Entity entity, SoundConfig soundConfig) {
        if (entity == null || entity.worldObj == null || soundConfig == null) {
            return;
        }
        if (!PatPatConfig.sounds.enabled) {
            return;
        }
        entity.worldObj.playSound(
            entity.posX,
            entity.posY,
            entity.posZ,
            soundConfig.getSoundId(),
            soundConfig.getVolume(),
            soundConfig.pickPitch(RANDOM),
            false);
    }
}
