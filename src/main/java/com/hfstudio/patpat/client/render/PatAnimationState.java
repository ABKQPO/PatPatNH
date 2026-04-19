package com.hfstudio.patpat.client.render;

import java.util.UUID;

import com.hfstudio.patpat.client.config.resourcepack.CustomAnimationSettingsConfig;
import com.hfstudio.patpat.client.config.resourcepack.PlayerConfig;

public class PatAnimationState {

    private final UUID entityUuid;
    private CustomAnimationSettingsConfig settings;
    private PlayerConfig whoPatted;
    private int tickProgress;

    public PatAnimationState(UUID entityUuid, CustomAnimationSettingsConfig settings, PlayerConfig whoPatted) {
        this.entityUuid = entityUuid;
        this.settings = settings;
        this.whoPatted = whoPatted;
        this.tickProgress = -1;
    }

    public UUID getEntityUuid() {
        return entityUuid;
    }

    public CustomAnimationSettingsConfig getSettings() {
        return settings;
    }

    public PlayerConfig getWhoPatted() {
        return whoPatted;
    }

    public void reset(CustomAnimationSettingsConfig newSettings, PlayerConfig newWhoPatted) {
        this.settings = newSettings;
        this.whoPatted = newWhoPatted;
        this.tickProgress = -1;
    }

    public void tick() {
        tickProgress++;
    }

    /**
     * Returns animation progress in milliseconds (1 tick = 50 ms),
     * matching upstream PatPat semantics.
     */
    public float getProgress(float tickDelta) {
        return Math.max(tickProgress, 0) * 50F + tickDelta * 50F;
    }

    /**
     * Progress as a 0.0-1.0 fraction of total duration.
     * Clamped to [0, 1].
     */
    public float getNormalizedProgress(float tickDelta) {
        if (settings.getDuration() <= 0) {
            return 1.0F;
        }
        float p = getProgress(tickDelta) / settings.getDuration();
        return Math.min(Math.max(p, 0.0F), 1.0F);
    }

    public boolean isExpired(float tickDelta) {
        return getProgress(tickDelta) > settings.getDuration();
    }
}
