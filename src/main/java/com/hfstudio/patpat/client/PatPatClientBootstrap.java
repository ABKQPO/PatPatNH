package com.hfstudio.patpat.client;

import com.hfstudio.patpat.client.manager.PatPatClientManager;
import com.hfstudio.patpat.client.packet.PatPatClientPacketManager;
import com.hfstudio.patpat.client.render.PatPatClientRenderer;
import com.hfstudio.patpat.client.resourcepack.PatPatClientResourcePackManager;
import com.hfstudio.patpat.client.resourcepack.PatPatClientSoundManager;

public final class PatPatClientBootstrap {

    private PatPatClientBootstrap() {}

    public static void preInit() {
        PatPatClientResourcePackManager.preInit();
        PatPatClientSoundManager.preInit();
    }

    public static void init() {
        PatPatClientPacketManager.register();
        PatPatClientManager.register();
        PatPatClientRenderer.register();
    }

    public static void postInit() {}

    public static void completeInit() {}
}
