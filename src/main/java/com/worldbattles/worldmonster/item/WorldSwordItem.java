package com.worldbattles.worldmonster.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

public class WorldSwordItem extends SwordItem {
    public static final String TAG_LEVEL = "WorldSwordLevel";
    public static final String TAG_KILLS = "WorldSwordKills";
    private static final int[] THRESHOLDS = {0, 100, 400, 1300, 4000};

    public WorldSwordItem() {
        super(Tiers.DIAMOND, 3, -2.4F, new Properties().stacksTo(1).fireResistant());
    }
    public static int getLevel(ItemStack stack) {
        int level = stack.getOrCreateTag().getInt(TAG_LEVEL);
        return Math.max(1, Math.min(5, level == 0 ? 1 : level));
    }
    public static int getKills(ItemStack stack) { return Math.max(0, stack.getOrCreateTag().getInt(TAG_KILLS)); }
    public static void setProgress(ItemStack stack, int level, int kills) {
        stack.getOrCreateTag().putInt(TAG_LEVEL, Math.max(1, Math.min(5, level)));
        stack.getOrCreateTag().putInt(TAG_KILLS, Math.max(0, kills));
    }
    public static int getThreshold(int level) { return THRESHOLDS[Math.max(0, Math.min(4, level - 1))]; }
    public static int getNextThreshold(int level) { return THRESHOLDS[Math.max(0, Math.min(4, level))]; }
    public static int getKillsRemaining(ItemStack stack) {
        int level = getLevel(stack);
        return level >= 5 ? 0 : Math.max(0, getNextThreshold(level) - getKills(stack));
    }
    public static String getLevelTranslationKey(int level) {
        return switch (Math.max(1, Math.min(5, level))) {
            case 1 -> "level.worldmonster.awakened";
            case 2 -> "level.worldmonster.reawakened";
            case 3 -> "level.worldmonster.corrupted";
            case 4 -> "level.worldmonster.absolute_void";
            default -> "level.worldmonster.max";
        };
    }
    public static float getDamageBonus(int level) {
        return switch (level) { case 2 -> 1.0F; case 3 -> 2.0F; case 4 -> 4.0F; case 5 -> 6.0F; default -> 0.0F; };
    }
    public static float getLifestealRatio(int level) {
        return switch (level) { case 3 -> 0.10F; case 4 -> 0.20F; case 5 -> 0.30F; default -> 0.0F; };
    }
    public static float getBleedChance(int level) {
        return switch (level) { case 2 -> 0.20F; case 3 -> 0.28F; case 4 -> 0.38F; case 5 -> 0.50F; default -> 0.0F; };
    }
}