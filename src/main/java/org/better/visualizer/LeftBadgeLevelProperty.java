// src/main/java/org/better/visualizer/LeftBadgeLevelProperty.java
package org.better.visualizer;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record LeftBadgeLevelProperty() implements RangeSelectItemModelProperty {
    public static final MapCodec<LeftBadgeLevelProperty> MAP_CODEC =
            MapCodec.unit(new LeftBadgeLevelProperty());

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        return EnchantmentBadgeUtil.getLeftBadgeLevel(stack, level);
    }

    @Override
    public MapCodec<LeftBadgeLevelProperty> type() {
        return MAP_CODEC;
    }
}