package com.hfstudio.patpat.client.config.resourcepack;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;

import com.google.gson.JsonObject;

public class CustomAnimationConfig implements Comparable<CustomAnimationConfig> {

    private final String version;
    private final int priority;
    private final CustomAnimationSettingsConfig animation;
    private final boolean blacklist;
    private final List<EntityConfig> entities;
    private String configPath;

    public CustomAnimationConfig(String version, int priority, CustomAnimationSettingsConfig animation,
        boolean blacklist, List<EntityConfig> entities) {
        this.version = version;
        this.priority = priority;
        this.animation = animation;
        this.blacklist = blacklist;
        this.entities = entities;
    }

    public String getVersion() {
        return version;
    }

    public int getPriority() {
        return priority;
    }

    public CustomAnimationSettingsConfig getAnimation() {
        return animation;
    }

    public boolean isBlacklist() {
        return blacklist;
    }

    public List<EntityConfig> getEntities() {
        return entities;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public boolean canUseFor(EntityLivingBase entity, PlayerConfig whoPatted) {
        if (entities == null || entities.isEmpty()) {
            return !blacklist;
        }
        for (EntityConfig entityConfig : entities) {
            if (entityConfig.matches(entity, whoPatted)) {
                return !blacklist;
            }
        }
        return blacklist;
    }

    @Override
    public int compareTo(CustomAnimationConfig other) {
        int value = Integer.compare(priority, other.priority);
        if (value == 0 && configPath != null && other.configPath != null) {
            return configPath.compareTo(other.configPath);
        }
        return value;
    }

    public static CustomAnimationConfig fromJson(JsonObject object) {
        if (object == null || !object.has("animation")
            || !object.get("animation")
                .isJsonObject()) {
            return null;
        }
        String version = object.has("version") ? object.get("version")
            .getAsString() : "1.0.0";
        int priority = object.has("priority") ? object.get("priority")
            .getAsInt() : 0;
        boolean blacklist = object.has("blacklist") && object.get("blacklist")
            .getAsBoolean();
        CustomAnimationSettingsConfig animation = CustomAnimationSettingsConfig
            .fromJson(object.getAsJsonObject("animation"));
        List<EntityConfig> entities = object.has("entities") ? EntityConfig.fromJson(object.get("entities"))
            : Collections.singletonList(EntityConfig.of("all"));
        // If no entity filters are provided, treat it as "all".
        if (entities.isEmpty()) {
            entities = Collections.singletonList(EntityConfig.of("all"));
        }
        return new CustomAnimationConfig(version, priority, animation, blacklist, entities);
    }
}
