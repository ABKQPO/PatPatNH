package com.hfstudio.patpat.common.config;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.hfstudio.patpat.PatPat;
import com.hfstudio.patpat.config.PatPatConfig;

public final class PatPatConfigManager {

    private static boolean initialized;

    private PatPatConfigManager() {}

    public static void preInit() {
        if (initialized) {
            return;
        }
        try {
            ConfigurationManager.registerConfig(PatPatConfig.class);
            initialized = true;
        } catch (ConfigException e) {
            throw new RuntimeException("Failed to register PatPat config", e);
        }
    }

    public static boolean isDebugEnabled() {
        return PatPatConfig.debug.enableDebugMode;
    }

    public static void logDebug(String message) {
        if (isDebugEnabled()) {
            PatPat.LOG.info("[debug] {}", message);
        }
    }
}
