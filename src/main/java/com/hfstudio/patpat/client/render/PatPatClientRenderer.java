package com.hfstudio.patpat.client.render;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import com.hfstudio.patpat.client.config.resourcepack.CustomAnimationSettingsConfig;
import com.hfstudio.patpat.client.config.resourcepack.FrameConfig;
import com.hfstudio.patpat.client.manager.PatPatClientManager;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class PatPatClientRenderer {

    private static final PatPatClientRenderer INSTANCE = new PatPatClientRenderer();
    private static final Set<Integer> SCALED_ENTITIES = new HashSet<>();

    private PatPatClientRenderer() {}

    public static void register() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance()
            .bus()
            .register(INSTANCE);
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre event) {
        PatAnimationState state = PatPatClientManager.getAnimationState(event.entity);
        if (state == null) {
            return;
        }

        float tickDelta = Minecraft.getMinecraft().timer.renderPartialTicks;
        float normalizedProgress = state.getNormalizedProgress(tickDelta);
        float easedProgress = (float) (1.0 - Math.pow(1.0 - normalizedProgress, 2.0));

        // Upstream-equivalent squish: scaleY = (1 - range) + range * (1 - sin(progress * PI))
        float patWeight = 0.5F;
        float range = patWeight / Math.max(event.entity.height, 0.001F);
        float scaleY = (1.0F - range) + range * (1.0F - (float) Math.sin(easedProgress * Math.PI));

        GL11.glPushMatrix();
        GL11.glTranslated(event.x, event.y, event.z);
        GL11.glScalef(1.0F, scaleY, 1.0F);
        GL11.glTranslated(-event.x, -event.y, -event.z);
        SCALED_ENTITIES.add(event.entity.getEntityId());
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post event) {
        if (SCALED_ENTITIES.remove(event.entity.getEntityId())) {
            GL11.glPopMatrix();
        }

        PatAnimationState state = PatPatClientManager.getAnimationState(event.entity);
        if (state == null) {
            return;
        }

        float tickDelta = Minecraft.getMinecraft().timer.renderPartialTicks;

        CustomAnimationSettingsConfig settings = state.getSettings();
        FrameConfig frame = settings.getFrameConfig();
        ResourceLocation texture = settings.getTexture();

        // ease-out quadratic curve, matching upstream: 1 - (1 - t)^2
        float normalizedProgress = state.getNormalizedProgress(tickDelta);
        float easedProgress = (float) (1.0 - Math.pow(1.0 - normalizedProgress, 2.0));

        int totalFrames = Math.max(frame.getTotalFrames(), 1);
        int currentFrame = Math.min((int) Math.floor(easedProgress * totalFrames), totalFrames - 1);

        float minU = (1.0F / totalFrames) * currentFrame;
        float maxU = minU + (1.0F / totalFrames);
        float minV = 0.0F;
        float maxV = 1.0F;
        float width = 0.425F * frame.getScaleX();
        float height = 0.425F * frame.getScaleY();

        // Y oscillation matching upstream: y = entityHeight * ((1 - range) + range * (1 - sin(eased * PI)))
        // patWeight = 0.5F is the default hand weight used by upstream
        float patWeight = 0.5F;
        float range = patWeight / Math.max(event.entity.height, 0.001F);
        float yProgress = (1.0F - range) + range * (1.0F - (float) Math.sin(easedProgress * Math.PI));
        float yOffset = event.entity.height * yProgress + 0.11F + frame.getOffsetY();

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(texture);

        GL11.glPushMatrix();
        GL11.glTranslated(event.x, event.y + yOffset, event.z + frame.getOffsetZ());
        GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(frame.getOffsetX(), 0.0F, 0.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator.addVertexWithUV(-width, 0.0F, 0.0F, minU, maxV);
        tessellator.addVertexWithUV(width, 0.0F, 0.0F, maxU, maxV);
        tessellator.addVertexWithUV(width, height * 2.0F, 0.0F, maxU, minV);
        tessellator.addVertexWithUV(-width, height * 2.0F, 0.0F, minU, minV);
        tessellator.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
