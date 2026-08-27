package com.example.addon.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;

public class ResistanceVignetteClient implements ClientModInitializer {

    private static final Identifier VIGNETTE_TEXTURE = Identifier.of("minecraft", "textures/misc/vignette.png");

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;

            StatusEffectInstance resistance = client.player.getStatusEffect(StatusEffects.RESISTANCE);

            if (resistance != null) {
                int duration = resistance.getDuration();
                int width = client.getWindow().getScaledWidth();
                int height = client.getWindow().getScaledHeight();

                float r = 0.0f, g = 0.0f, b = 0.0f, alpha = 0.0f;

                if (duration > 100) { 
                    // Mor renk (Efekt aktifken)
                    r = 0.5f; g = 0.0f; b = 0.5f; alpha = 0.7f;
                } else if (duration > 0) {
                    // Kırmızı yanıp sönme (Son 5 saniye)
                    if ((duration / 5) % 2 == 0) {
                        r = 1.0f; g = 0.0f; b = 0.0f; alpha = 0.9f;
                    } else {
                        r = 0.3f; g = 0.0f; b = 0.0f; alpha = 0.1f;
                    }
                }

                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.setShader(GameRenderer::getPositionTexProgram);
                RenderSystem.setShaderColor(r, g, b, alpha);

                drawContext.drawTexture(VIGNETTE_TEXTURE, 0, 0, -90, 0.0F, 0.0F, width, height, width, height);

                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableBlend();
                RenderSystem.depthMask(true);
                RenderSystem.enableDepthTest();
            }
        });
    }
}
