package com.hfstudio.patpat.common.interaction;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.hfstudio.patpat.config.PatPatConfig;

public class PatPatInteractionPolicy {

    private PatPatInteractionPolicy() {}

    public static boolean canPatClient(EntityPlayer player, EntityLivingBase target, int patCooldown,
        boolean serverAvailable, boolean allowClientOnly) {
        if (patCooldown > 0) {
            return false;
        }
        if (!serverAvailable && !allowClientOnly) {
            return false;
        }
        return canPat(player, target);
    }

    public static boolean canPatServer(EntityPlayer player, EntityLivingBase target, boolean withinRange) {
        if (!withinRange) {
            return false;
        }
        return canPat(player, target);
    }

    public static boolean isMainHandEmpty(EntityPlayer player) {
        if (player == null) {
            return true;
        }
        return isMainHandEmpty(player.getCurrentEquippedItem());
    }

    public static boolean isMainHandEmpty(ItemStack heldItem) {
        return heldItem == null || heldItem.getItem() == null || heldItem.stackSize <= 0;
    }

    private static boolean canPat(EntityPlayer player, EntityLivingBase target) {
        if (!PatPatConfig.main.modEnabled) {
            return false;
        }
        if (player == null || target == null) {
            return false;
        }
        if (player.isDead || target.isDead) {
            return false;
        }
        if (target.isInvisible()) {
            return false;
        }
        if (PatPatConfig.main.requireSneaking && !player.isSneaking()) {
            return false;
        }
        if (PatPatConfig.main.requireEmptyMainHand && !isMainHandEmpty(player)) {
            return false;
        }
        return true;
    }
}
