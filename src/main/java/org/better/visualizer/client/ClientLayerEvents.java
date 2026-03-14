package org.better.visualizer.client;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.better.visualizer.Visualizer;

@EventBusSubscriber(modid = Visualizer.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientLayerEvents {

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        EntityModelSet modelSet = event.getEntityModels();

        for (PlayerSkin.Model skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);
            if (renderer != null) {
                renderer.addLayer(new ProtectionArmorLayer(renderer, modelSet));
            }
        }
    }
}