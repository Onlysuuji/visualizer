package org.better.visualizer;

import org.better.visualizer.Visualizer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;

@EventBusSubscriber(modid = Visualizer.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public final class ClientModelProperties {
    private ClientModelProperties() {
    }

    @SubscribeEvent
    public static void registerRangeProperties(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(
                ResourceLocation.fromNamespaceAndPath(EnchTexMod.MODID, "left_badge_level"),
                LeftBadgeLevelProperty.MAP_CODEC
        );

        event.register(
                ResourceLocation.fromNamespaceAndPath(EnchTexMod.MODID, "right_badge_level"),
                RightBadgeLevelProperty.MAP_CODEC
        );
    }
}