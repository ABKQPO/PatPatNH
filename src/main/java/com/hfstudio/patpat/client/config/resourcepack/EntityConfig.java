package com.hfstudio.patpat.client.config.resourcepack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hfstudio.patpat.config.PatPatConfig;

public class EntityConfig {

    private final String entityId;
    private final String entityName;
    private final UUID entityUuid;
    private final List<PlayerConfig> fromPlayers;

    public EntityConfig(String entityId, String entityName, UUID entityUuid, List<PlayerConfig> fromPlayers) {
        this.entityId = entityId;
        this.entityName = entityName;
        this.entityUuid = entityUuid;
        this.fromPlayers = fromPlayers;
    }

    public static EntityConfig of(String entityId) {
        return new EntityConfig(entityId, null, null, null);
    }

    public boolean matches(Entity entity, PlayerConfig whoPatted) {
        String currentEntityId = EntityList.getEntityString(entity);
        if (currentEntityId == null) {
            currentEntityId = entity.getClass()
                .getSimpleName();
        }
        if (!entityIdMatches(entityId, currentEntityId)) {
            return false;
        }
        if (entityName != null && !entityName.equals(entity.getCommandSenderName())) {
            return false;
        }
        if (entityUuid != null && !entityUuid.equals(entity.getUniqueID())) {
            return false;
        }
        if (fromPlayers != null && !fromPlayers.isEmpty()) {
            for (PlayerConfig playerConfig : fromPlayers) {
                if (whoPatted.matches(playerConfig)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Matches an entity ID from a resource pack config against the 1.7.10
     * entity string from EntityList.
     *
     * Supports:
     * "all" → matches any entity
     * "Pig" → exact case-insensitive match
     * "minecraft:pig" → strip namespace, case-insensitive match (upstream format)
     * "patpat:pig" → any namespace stripped the same way
     */
    private static boolean entityIdMatches(String configId, String currentId) {
        if (configId.equalsIgnoreCase("all")) {
            return true;
        }
        if (configId.equalsIgnoreCase(currentId)) {
            return true;
        }

        String normalizedCurrentId = normalizeEntityId(currentId);
        String normalizedConfigId = normalizeEntityId(configId);
        if (!normalizedConfigId.isEmpty() && normalizedConfigId.equals(normalizedCurrentId)) {
            return true;
        }

        // Strip "namespace:" prefix from upstream-format IDs (e.g. "minecraft:pig" -> "pig")
        // Only when upstream pack compatibility is enabled.
        if (PatPatConfig.resourcePacks.enableUpstreamPackCompatibility) {
            int colonIdx = configId.indexOf(':');
            if (colonIdx >= 0) {
                String bare = configId.substring(colonIdx + 1);
                if (bare.equalsIgnoreCase(currentId)) {
                    return true;
                }
                return normalizeEntityId(bare).equals(normalizedCurrentId);
            }
        }
        return false;
    }

    /**
     * Normalizes entity IDs for legacy-vs-modern format compatibility.
     * Example: "minecraft:ender_dragon" -> "enderdragon", "EnderDragon" -> "enderdragon".
     */
    private static String normalizeEntityId(String id) {
        if (id == null) {
            return "";
        }
        String bare = id;
        int colonIdx = bare.indexOf(':');
        if (colonIdx >= 0 && colonIdx + 1 < bare.length()) {
            bare = bare.substring(colonIdx + 1);
        }
        StringBuilder builder = new StringBuilder(bare.length());
        for (int i = 0; i < bare.length(); i++) {
            char ch = bare.charAt(i);
            if (Character.isLetterOrDigit(ch)) {
                builder.append(Character.toLowerCase(ch));
            }
        }
        return builder.toString();
    }

    public static List<EntityConfig> fromJson(JsonElement element) {
        List<EntityConfig> configs = new ArrayList<>();
        if (element == null || element.isJsonNull()) {
            configs.add(EntityConfig.of("all"));
            return configs;
        }
        if (element.isJsonPrimitive()) {
            String id = element.getAsString();
            configs.add(EntityConfig.of(isBlank(id) ? "all" : id));
            return configs;
        }
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement entry : array) {
                configs.addAll(fromJson(entry));
            }
            if (configs.isEmpty()) {
                configs.add(EntityConfig.of("all"));
            }
            return configs;
        }

        JsonObject object = element.getAsJsonObject();
        String id = object.has("id") ? object.get("id")
            .getAsString() : "all";
        if (isBlank(id)) {
            id = "all";
        }
        String name = object.has("name") ? object.get("name")
            .getAsString() : null;
        UUID uuid = object.has("uuid") ? UUID.fromString(
            object.get("uuid")
                .getAsString())
            : null;
        List<PlayerConfig> from = null;
        if (object.has("from") && object.get("from")
            .isJsonArray()) {
            from = new ArrayList<>();
            JsonArray fromArray = object.getAsJsonArray("from");
            for (JsonElement playerElement : fromArray) {
                if (playerElement.isJsonObject()) {
                    from.add(PlayerConfig.fromJson(playerElement.getAsJsonObject()));
                }
            }
        }
        configs.add(new EntityConfig(id, name, uuid, from));
        return configs;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim()
            .isEmpty();
    }
}
