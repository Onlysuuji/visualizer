package org.better.visualizer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;

public final class EnchantmentBadgeUtil {
    private EnchantmentBadgeUtil() {
    }

    public static @Nullable ClientLevel resolveLevel(@Nullable ClientLevel level) {
        return level != null ? level : Minecraft.getInstance().level;
    }

    public static boolean isSupportedSword(ItemStack stack) {
        return stack.is(Items.DIAMOND_SWORD)
                || stack.is(Items.IRON_SWORD)
                || stack.is(Items.GOLDEN_SWORD);
    }

    public static boolean isSupportedTool(ItemStack stack) {
        return stack.is(Items.DIAMOND_PICKAXE)
                || stack.is(Items.IRON_PICKAXE)
                || stack.is(Items.GOLDEN_PICKAXE)
                || stack.is(Items.DIAMOND_SHOVEL)
                || stack.is(Items.IRON_SHOVEL)
                || stack.is(Items.GOLDEN_SHOVEL);
    }

    public static int getLeftBadgeLevel(ItemStack stack, @Nullable ClientLevel level) {
        ClientLevel actual = resolveLevel(level);
        if (actual == null) return 0;

        var lookup = actual.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        if (isSupportedSword(stack)) {
            var sharpness = lookup.getOrThrow(Enchantments.SHARPNESS);
            return Math.max(0, Math.min(stack.getEnchantmentLevel(sharpness), 5));
        }

        if (isSupportedTool(stack)) {
            var efficiency = lookup.getOrThrow(Enchantments.EFFICIENCY);
            return Math.max(0, Math.min(stack.getEnchantmentLevel(efficiency), 5));
        }

        return 0;
    }

    public static int getRightBadgeLevel(ItemStack stack, @Nullable ClientLevel level) {
        ClientLevel actual = resolveLevel(level);
        if (actual == null) return 0;

        var lookup = actual.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        if (isSupportedSword(stack)) {
            var fireAspect = lookup.getOrThrow(Enchantments.FIRE_ASPECT);
            return Math.max(0, Math.min(stack.getEnchantmentLevel(fireAspect), 2));
        }

        if (isSupportedTool(stack)) {
            var fortune = lookup.getOrThrow(Enchantments.FORTUNE);
            return Math.max(0, Math.min(stack.getEnchantmentLevel(fortune), 3));
        }

        return 0;
    }
}