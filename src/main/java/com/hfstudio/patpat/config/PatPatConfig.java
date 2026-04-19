package com.hfstudio.patpat.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultInt;
import com.gtnewhorizon.gtnhlib.config.Config.LangKey;
import com.gtnewhorizon.gtnhlib.config.Config.RequiresMcRestart;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.hfstudio.patpat.PatPat;

@Config(modid = PatPat.MODID, filename = "patpat", configSubDirectory = "PatPat")
@Config.LangKeyPattern(pattern = "patpat.gui.config.%cat.%field", fullyQualified = true)
@Comment("PatPat configuration")
public class PatPatConfig {

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(PatPatConfig.class);
    }

    @LangKey("patpat.gui.config.main")
    public static final Main main = new Main();
    @LangKey("patpat.gui.config.multiplayer")
    public static final Multiplayer multiplayer = new Multiplayer();
    @LangKey("patpat.gui.config.resourcepacks")
    public static final ResourcePacks resourcePacks = new ResourcePacks();
    @LangKey("patpat.gui.config.sounds")
    public static final Sounds sounds = new Sounds();
    @LangKey("patpat.gui.config.debug")
    public static final Debug debug = new Debug();

    public static class Main {

        @Comment("Master on/off switch for PatPat.")
        @LangKey("patpat.gui.config.main.modenabled")
        @DefaultBoolean(true)
        public boolean modEnabled = true;

        @Comment("Whether the player must be sneaking to trigger a pat.")
        @LangKey("patpat.gui.config.main.requiresneaking")
        @DefaultBoolean(true)
        public boolean requireSneaking = true;

        @Comment("Whether the player's main hand must be empty to trigger a pat.")
        @LangKey("patpat.gui.config.main.requireemptymainhand")
        @DefaultBoolean(true)
        public boolean requireEmptyMainHand = true;

        @Comment("Cooldown between pats, in ticks.")
        @LangKey("patpat.gui.config.main.patcooldownticks")
        @DefaultInt(4)
        public int patCooldownTicks = 4;
    }

    public static class Multiplayer {

        @Comment("Send pat sync packets to the server when connected.")
        @LangKey("patpat.gui.config.multiplayer.enableserversync")
        @DefaultBoolean(true)
        public boolean enableServerSync = true;

        @Comment("Allow local pat rendering when only the client has the mod installed.")
        @LangKey("patpat.gui.config.multiplayer.allowclientonly")
        @DefaultBoolean(true)
        public boolean allowClientOnly = true;
    }

    public static class ResourcePacks {

        @Comment("Load animation configs from resource packs.")
        @LangKey("patpat.gui.config.resourcepacks.enablecustomanimations")
        @DefaultBoolean(true)
        public boolean enableCustomAnimations = true;

        @Comment("Enable compatibility mode for upstream (Fabric) resource pack format.")
        @LangKey("patpat.gui.config.resourcepacks.enableupstreampackcompatibility")
        @DefaultBoolean(true)
        public boolean enableUpstreamPackCompatibility = true;
    }

    public static class Sounds {

        @Comment("Enable custom sounds during pat animation.")
        @LangKey("patpat.gui.config.sounds.enabled")
        @DefaultBoolean(true)
        public boolean enabled = true;
    }

    public static class Debug {

        @Comment("Enable debug logging.")
        @LangKey("patpat.gui.config.debug.enabledebugmode")
        @DefaultBoolean(false)
        @RequiresMcRestart
        public boolean enableDebugMode = false;
    }
}
