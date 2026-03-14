package org.better.visualizer;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.util.Map;

public final class ClientModelProperties {
    private ClientModelProperties() {}

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void register() {
        try {
            Field field = RangeSelectItemModelProperties.class.getDeclaredField("ID_MAPPER");
            field.setAccessible(true);

            Map map = (Map) field.get(null);

            map.put(
                    ResourceLocation.fromNamespaceAndPath(Visualizer.MODID, "left_badge_level"),
                    LeftBadgeLevelProperty.MAP_CODEC
            );
            map.put(
                    ResourceLocation.fromNamespaceAndPath(Visualizer.MODID, "right_badge_level"),
                    RightBadgeLevelProperty.MAP_CODEC
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to register item model properties", e);
        }
    }
}