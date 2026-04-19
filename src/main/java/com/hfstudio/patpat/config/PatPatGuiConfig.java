package com.hfstudio.patpat.config;

import net.minecraft.client.gui.GuiScreen;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.SimpleGuiConfig;
import com.hfstudio.patpat.PatPat;

public class PatPatGuiConfig extends SimpleGuiConfig {

    public PatPatGuiConfig(GuiScreen parentScreen) throws ConfigException {
        super(parentScreen, PatPat.MODID, PatPat.MODNAME, true, PatPatConfig.class);
    }
}
