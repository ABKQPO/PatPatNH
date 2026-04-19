package com.hfstudio.patpat.client.config.resourcepack;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonObject;
import com.hfstudio.patpat.PatPat;

public class CustomAnimationSettingsConfig {

    public static final CustomAnimationSettingsConfig DEFAULT = new CustomAnimationSettingsConfig(
        new ResourceLocation(PatPat.MODID, "textures/default/patpat.png"),
        240,
        FrameConfig.DEFAULT,
        SoundConfig.DEFAULT);

    private final ResourceLocation texture;
    private final int duration;
    private final FrameConfig frameConfig;
    private final SoundConfig soundConfig;

    public CustomAnimationSettingsConfig(ResourceLocation texture, int duration, FrameConfig frameConfig,
        SoundConfig soundConfig) {
        this.texture = texture;
        this.duration = duration;
        this.frameConfig = frameConfig;
        this.soundConfig = soundConfig;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public int getDuration() {
        return duration;
    }

    public FrameConfig getFrameConfig() {
        return frameConfig;
    }

    public SoundConfig getSoundConfig() {
        return soundConfig;
    }

    public static CustomAnimationSettingsConfig fromJson(JsonObject object) {
        if (object == null) {
            return DEFAULT;
        }
        String textureId = object.has("texture") ? object.get("texture")
            .getAsString() : DEFAULT.texture.toString();
        int duration = object.has("duration") ? object.get("duration")
            .getAsInt() : DEFAULT.duration;
        FrameConfig frame = object.has("frame") && object.get("frame")
            .isJsonObject() ? FrameConfig.fromJson(object.getAsJsonObject("frame")) : DEFAULT.frameConfig;
        SoundConfig sound = object.has("sound") ? SoundConfig.fromJson(object.get("sound")) : DEFAULT.soundConfig;
        return new CustomAnimationSettingsConfig(new ResourceLocation(textureId), duration, frame, sound);
    }
}
