package org.better.visualizer;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record RightBadgeLevelProperty() implements RangeSelectItemModelProperty {
    public static final MapCodec<RightBadgeLevelProperty> MAP_CODEC =
            MapCodec.unit(new RightBadgeLevelProperty());

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        return EnchantmentBadgeUtil.getRightBadgeLevel(stack, level);
    }

    @Override
    public MapCodec<RightBadgeLevelProperty> type() {
        return MAP_CODEC;
    }
}