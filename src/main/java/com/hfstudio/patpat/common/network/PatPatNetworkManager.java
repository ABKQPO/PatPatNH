package com.hfstudio.patpat.common.network;

import com.hfstudio.patpat.PatPat;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public final class PatPatNetworkManager {

    private PatPatNetworkManager() {}

    public static SimpleNetworkWrapper createChannel() {
        return NetworkRegistry.INSTANCE.newSimpleChannel(PatPat.MODID);
    }
}
