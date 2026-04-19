package com.hfstudio.patpat;

import com.hfstudio.patpat.client.PatPatClientBootstrap;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        PatPatClientBootstrap.preInit();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        PatPatClientBootstrap.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        PatPatClientBootstrap.postInit();
    }

    @Override
    public void completeInit(FMLLoadCompleteEvent event) {
        super.completeInit(event);
        PatPatClientBootstrap.completeInit();
    }
}
