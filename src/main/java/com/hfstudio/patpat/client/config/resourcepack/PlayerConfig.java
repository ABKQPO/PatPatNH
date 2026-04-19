package com.hfstudio.patpat.client.config.resourcepack;

import java.util.UUID;

import com.google.gson.JsonObject;

public class PlayerConfig {

    private final String name;
    private final UUID uuid;

    public PlayerConfig(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public static PlayerConfig of(String name, UUID uuid) {
        return new PlayerConfig(name, uuid);
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean matches(PlayerConfig other) {
        if (other == null) {
            return false;
        }
        boolean nameMatches = other.name == null || (name != null && name.equals(other.name));
        boolean uuidMatches = other.uuid == null || (uuid != null && uuid.equals(other.uuid));
        return nameMatches && uuidMatches;
    }

    public static PlayerConfig fromJson(JsonObject object) {
        String name = object.has("name") ? object.get("name")
            .getAsString() : null;
        UUID uuid = object.has("uuid") ? UUID.fromString(
            object.get("uuid")
                .getAsString())
            : null;
        return new PlayerConfig(name, uuid);
    }
}
