package com.hfstudio.patpat.client.resourcepack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.EntityLivingBase;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hfstudio.patpat.PatPat;
import com.hfstudio.patpat.client.config.resourcepack.CustomAnimationConfig;
import com.hfstudio.patpat.client.config.resourcepack.CustomAnimationSettingsConfig;
import com.hfstudio.patpat.client.config.resourcepack.PlayerConfig;
import com.hfstudio.patpat.config.PatPatConfig;

public final class PatPatClientResourcePackManager implements IResourceManagerReloadListener {

    public static final PatPatClientResourcePackManager INSTANCE = new PatPatClientResourcePackManager();
    private static final String CONFIG_ROOT = "assets/patpat/textures";

    private final List<CustomAnimationConfig> loadedAnimations = new ArrayList<>();

    private PatPatClientResourcePackManager() {}

    public static void preInit() {
        if (Minecraft.getMinecraft()
            .getResourceManager() instanceof IReloadableResourceManager resourceManager) {
            resourceManager.registerReloadListener(INSTANCE);
        }
    }

    public CustomAnimationSettingsConfig getAnimation(EntityLivingBase entity, PlayerConfig whoPatted) {
        if (!PatPatConfig.resourcePacks.enableCustomAnimations) {
            return CustomAnimationSettingsConfig.DEFAULT;
        }
        for (CustomAnimationConfig config : loadedAnimations) {
            if (config.canUseFor(entity, whoPatted)) {
                return config.getAnimation();
            }
        }
        return CustomAnimationSettingsConfig.DEFAULT;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        reload();
    }

    private void reload() {
        loadedAnimations.clear();

        Minecraft minecraft = Minecraft.getMinecraft();
        File resourcePackDir = new File(minecraft.mcDataDir, "resourcepacks");
        List<String> selectedPacks = minecraft.gameSettings.resourcePacks;
        if (selectedPacks == null || selectedPacks.isEmpty() || !resourcePackDir.isDirectory()) {
            return;
        }

        List<String> packOrder = new ArrayList<>(selectedPacks);
        Collections.reverse(packOrder);
        for (String packName : packOrder) {
            File packFile = new File(resourcePackDir, packName);
            if (packFile.isDirectory()) {
                loadDirectoryPack(packName, new File(packFile, CONFIG_ROOT));
            } else if (packFile.isFile() && packName.endsWith(".zip")) {
                loadZipPack(packName, packFile);
            }
        }
        Collections.sort(loadedAnimations);
        Collections.reverse(loadedAnimations);
    }

    private void loadDirectoryPack(String packName, File current) {
        if (!current.exists()) {
            return;
        }
        File[] files = current.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                loadDirectoryPack(packName, file);
            } else if (isConfigFile(file.getName())) {
                try (InputStream stream = new FileInputStream(file)) {
                    parseConfig(packName, stream);
                } catch (IOException e) {
                    PatPat.LOG.warn("Failed to read animation config from {}", file.getAbsolutePath(), e);
                }
            }
        }
    }

    private void loadZipPack(String packName, File packFile) {
        try {
            ZipFile zipFile = new ZipFile(packFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                String entryName = entry.getName();
                if (!entryName.startsWith(CONFIG_ROOT) || !isConfigFile(entryName)) {
                    continue;
                }
                parseConfig(packName, zipFile.getInputStream(entry));
            }
            zipFile.close();
        } catch (IOException e) {
            PatPat.LOG.warn("Failed to read resource pack {}", packName, e);
        }
    }

    private void parseConfig(String packName, InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        JsonElement element = new JsonParser().parse(reader);
        if (!element.isJsonObject()) {
            return;
        }
        JsonObject object = element.getAsJsonObject();

        // Version compatibility check (mirrors upstream behavior)
        if (object.has("version")) {
            String configVersion = object.get("version")
                .getAsString();
            if (!isVersionSupported(configVersion)) {
                PatPat.LOG.warn(
                    "[PatPat] Resource pack '{}': config version [{}] may not be supported by this mod version [{}], proceeding anyway",
                    packName,
                    configVersion,
                    PatPat.VERSION);
            }
        } else {
            PatPat.LOG.warn(
                "[PatPat] Resource pack '{}': config is missing 'version' field, proceeding with defaults",
                packName);
        }

        CustomAnimationConfig config = CustomAnimationConfig.fromJson(object);
        if (config != null) {
            config.setConfigPath(packName);
            loadedAnimations.add(config);
        }
    }

    /**
     * Returns false if the config version string appears to be for a newer release
     * than this mod (simple major.minor comparison).
     */
    private boolean isVersionSupported(String configVersion) {
        try {
            String[] configParts = configVersion.split("[.\\-]");
            String[] modParts = PatPat.VERSION.split("[.\\-]");
            int configMajor = Integer.parseInt(configParts[0]);
            int modMajor = Integer.parseInt(modParts[0]);
            if (configMajor != modMajor) {
                return configMajor <= modMajor;
            }
            if (configParts.length > 1 && modParts.length > 1) {
                return Integer.parseInt(configParts[1]) <= Integer.parseInt(modParts[1]);
            }
        } catch (Exception ignored) {}
        return true;
    }

    private boolean isConfigFile(String name) {
        return name.endsWith(".json") || name.endsWith(".json5");
    }
}
