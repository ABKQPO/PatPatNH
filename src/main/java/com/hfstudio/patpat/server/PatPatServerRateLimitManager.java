package com.hfstudio.patpat.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;

import com.hfstudio.patpat.config.PatPatConfig;

/**
 * Server-side rate limiting for pat requests.
 * Tracks the last pat tick per player UUID and enforces cooldown.
 * Mirrors the role of PatPatServerRateLimitManager in upstream PatPat.
 */
public final class PatPatServerRateLimitManager {

    private static final Map<UUID, Long> LAST_PAT_TICKS = new HashMap<>();

    private PatPatServerRateLimitManager() {}

    /**
     * Returns true if the player is allowed to pat right now,
     * and records the current tick as the new last-pat tick.
     */
    public static boolean tryPat(EntityPlayerMP player) {
        UUID id = player.getUniqueID();
        long nowTick = player.worldObj.getTotalWorldTime();
        Long lastTick = LAST_PAT_TICKS.get(id);
        if (lastTick != null && nowTick - lastTick < PatPatConfig.main.patCooldownTicks) {
            return false;
        }
        LAST_PAT_TICKS.put(id, nowTick);
        return true;
    }

    /** Call on player disconnect to free map memory. */
    public static void removePlayer(UUID playerId) {
        LAST_PAT_TICKS.remove(playerId);
    }

    /** Clear all rate limit state (e.g. on server stop). */
    public static void clear() {
        LAST_PAT_TICKS.clear();
    }
}
