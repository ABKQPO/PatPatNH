package com.hfstudio.patpat.client.config.resourcepack;

import com.google.gson.JsonObject;

public class FrameConfig {

    public static final FrameConfig DEFAULT = new FrameConfig(5, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);

    private final int totalFrames;
    private final float scaleX;
    private final float scaleY;
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;

    public FrameConfig(int totalFrames, float scaleX, float scaleY, float offsetX, float offsetY, float offsetZ) {
        this.totalFrames = totalFrames;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    public int getTotalFrames() {
        return totalFrames;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public float getOffsetZ() {
        return offsetZ;
    }

    public static FrameConfig fromJson(JsonObject object) {
        if (object == null) {
            return DEFAULT;
        }
        return new FrameConfig(
            getInt(object, "totalFrames", DEFAULT.totalFrames),
            getFloat(object, "scaleX", DEFAULT.scaleX),
            getFloat(object, "scaleY", DEFAULT.scaleY),
            getFloat(object, "offsetX", DEFAULT.offsetX),
            getFloat(object, "offsetY", DEFAULT.offsetY),
            getFloat(object, "offsetZ", DEFAULT.offsetZ));
    }

    private static int getInt(JsonObject object, String key, int defaultValue) {
        return object.has(key) ? object.get(key)
            .getAsInt() : defaultValue;
    }

    private static float getFloat(JsonObject object, String key, float defaultValue) {
        return object.has(key) ? object.get(key)
            .getAsFloat() : defaultValue;
    }
}
