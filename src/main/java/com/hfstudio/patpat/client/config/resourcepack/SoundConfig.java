package com.hfstudio.patpat.client.config.resourcepack;

import java.util.Random;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SoundConfig {

    public static final SoundConfig DEFAULT = new SoundConfig("patpat:patpat", 1.0F, 1.0F, 1.0F);

    private final String soundId;
    private final float minPitch;
    private final float maxPitch;
    private final float volume;

    public SoundConfig(String soundId, float minPitch, float maxPitch, float volume) {
        this.soundId = soundId;
        this.minPitch = minPitch;
        this.maxPitch = maxPitch;
        this.volume = volume;
    }

    public String getSoundId() {
        return soundId;
    }

    public float getMinPitch() {
        return minPitch;
    }

    public float getMaxPitch() {
        return maxPitch;
    }

    public float getVolume() {
        return volume;
    }

    public float pickPitch(Random random) {
        if (minPitch == maxPitch) {
            return minPitch;
        }
        return minPitch + (maxPitch - minPitch) * random.nextFloat();
    }

    public static SoundConfig fromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return DEFAULT;
        }
        if (element.isJsonPrimitive()) {
            return new SoundConfig(element.getAsString(), 1.0F, 1.0F, 1.0F);
        }
        JsonObject object = element.getAsJsonObject();
        return new SoundConfig(
            object.has("id") ? object.get("id")
                .getAsString() : DEFAULT.soundId,
            object.has("min_pitch") ? object.get("min_pitch")
                .getAsFloat()
                : (object.has("minPitch") ? object.get("minPitch")
                    .getAsFloat() : DEFAULT.minPitch),
            object.has("max_pitch") ? object.get("max_pitch")
                .getAsFloat()
                : (object.has("maxPitch") ? object.get("maxPitch")
                    .getAsFloat() : DEFAULT.maxPitch),
            object.has("volume") ? object.get("volume")
                .getAsFloat() : DEFAULT.volume);
    }
}
