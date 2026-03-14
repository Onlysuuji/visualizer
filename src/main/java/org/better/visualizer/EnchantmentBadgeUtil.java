package org.better.visualizer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

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

    public static boolean isSupportedBow(ItemStack stack) {
        return stack.is(Items.BOW);
    }

    public static boolean isSupportedBook(ItemStack stack) {
        return stack.is(Items.ENCHANTED_BOOK);
    }

    public static int getLeftBadgeLevel(ItemStack stack, @Nullable ClientLevel level) {
        ClientLevel actual = resolveLevel(level);
        if (actual == null) return 0;

        var lookup = actual.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        if (isSupportedSword(stack)) {
            return clamp(getNormalEnchantLevel(stack, lookup.getOrThrow(Enchantments.SHARPNESS)), 0, 5);
        }

        if (isSupportedTool(stack)) {
            return clamp(getNormalEnchantLevel(stack, lookup.getOrThrow(Enchantments.EFFICIENCY)), 0, 5);
        }

        if (isSupportedBow(stack)) {
            return clamp(getNormalEnchantLevel(stack, lookup.getOrThrow(Enchantments.POWER)), 0, 5);
        }

        if (isSupportedBook(stack)) {
            return clamp(getStoredBookEnchantLevel(stack, lookup.getOrThrow(Enchantments.SHARPNESS)), 0, 5);
        }

        return 0;
    }

    public static int getRightBadgeLevel(ItemStack stack, @Nullable ClientLevel level) {
        ClientLevel actual = resolveLevel(level);
        if (actual == null) return 0;

        var lookup = actual.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        if (isSupportedSword(stack)) {
            return clamp(getNormalEnchantLevel(stack, lookup.getOrThrow(Enchantments.FIRE_ASPECT)), 0, 2);
        }

        if (isSupportedTool(stack)) {
            return clamp(getNormalEnchantLevel(stack, lookup.getOrThrow(Enchantments.FORTUNE)), 0, 3);
        }

        if (isSupportedBow(stack)) {
            return clamp(getNormalEnchantLevel(stack, lookup.getOrThrow(Enchantments.FLAME)), 0, 1);
        }

        // 本では右下は使わない
        if (isSupportedBook(stack)) {
            return 0;
        }

        return 0;
    }

    public static int getTopLeftBadgeLevel(ItemStack stack, @Nullable ClientLevel level) {
        ClientLevel actual = resolveLevel(level);
        if (actual == null) return 0;

        var lookup = actual.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        if (isSupportedBook(stack)) {
            return clamp(getStoredBookEnchantLevel(stack, lookup.getOrThrow(Enchantments.EFFICIENCY)), 0, 5);
        }

        return 0;
    }

    public static int getTopRightBadgeLevel(ItemStack stack, @Nullable ClientLevel level) {
        ClientLevel actual = resolveLevel(level);
        if (actual == null) return 0;

        var lookup = actual.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        if (isSupportedBook(stack)) {
            return clamp(getStoredBookEnchantLevel(stack, lookup.getOrThrow(Enchantments.FIRE_ASPECT)), 0, 2);
        }

        return 0;
    }

    private static int getNormalEnchantLevel(ItemStack stack, Holder<Enchantment> target) {
        AtomicInteger result = new AtomicInteger(0);

        EnchantmentHelper.runIterationOnItem(stack, (holder, level) -> {
            if (holder.equals(target)) {
                result.set(Math.max(result.get(), level));
            }
        });

        return result.get();
    }

    private static int getStoredBookEnchantLevel(ItemStack stack, Holder<Enchantment> target) {
        ItemEnchantments stored = stack.getOrDefault(
                DataComponents.STORED_ENCHANTMENTS,
                ItemEnchantments.EMPTY
        );
        return stored.getLevel(target);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
}