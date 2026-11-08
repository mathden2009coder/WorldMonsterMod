package com.worldbattles.worldmonster.event;

import com.worldbattles.worldmonster.entity.WorldMonsterEntity;
import com.worldbattles.worldmonster.item.WorldSwordItem;
import com.worldbattles.worldmonster.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "worldmonster", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WorldSwordEvents {
    private WorldSwordEvents() {}
    private static ItemStack getSword(Player player) {
        if (player.getMainHandItem().is(ModItems.WORLD_SWORD.get())) return player.getMainHandItem();
        if (player.getOffhandItem().is(ModItems.WORLD_SWORD.get())) return player.getOffhandItem();
        return ItemStack.EMPTY;
    }
    private static boolean directHit(DamageSource source, Player player) {
        return source.getEntity() == player && source.getDirectEntity() == player;
    }
    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (event.getEntity() instanceof WorldMonsterEntity monster && !monster.level().isClientSide) {
            monster.spawnAtLocation(new ItemStack(ModItems.WORLD_SWORD.get()));
            if (monster.level() instanceof ServerLevel server)
                server.getPlayerList().broadcastSystemMessage(Component.translatable("message.worldmonster.defeated")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
        }
        if (event.getEntity() instanceof Player || !(event.getSource().getEntity() instanceof Player player)
                || !directHit(event.getSource(), player)) return;
        ItemStack sword = getSword(player);
        if (sword.isEmpty()) return;
        int oldLevel = WorldSwordItem.getLevel(sword);
        int kills = WorldSwordItem.getKills(sword) + 1;
        int newLevel = oldLevel;
        while (newLevel < 5 && kills >= WorldSwordItem.getNextThreshold(newLevel)) newLevel++;
        WorldSwordItem.setProgress(sword, newLevel, kills);
        if (newLevel > oldLevel) player.sendSystemMessage(Component.translatable("message.worldmonster.world_sword.level_up",
                Component.translatable(WorldSwordItem.getLevelTranslationKey(newLevel))).withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
    }
    @SubscribeEvent
    public static void onHit(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player) || !directHit(event.getSource(), player)) return;
        ItemStack sword = getSword(player);
        if (sword.isEmpty()) return;
        int level = WorldSwordItem.getLevel(sword);
        event.setAmount(event.getAmount() + WorldSwordItem.getDamageBonus(level));
        float chance = WorldSwordItem.getBleedChance(level);
        LivingEntity target = event.getEntity();
        if (!target.level().isClientSide && chance > 0 && target.getRandom().nextFloat() < chance) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 0));
            if (target.level() instanceof ServerLevel server)
                server.sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY() + target.getBbHeight() * 0.55D,
                        target.getZ(), 7, 0.25D, 0.35D, 0.25D, 0.02D);
        }
    }
    @SubscribeEvent
    public static void onLifesteal(LivingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player) || !directHit(event.getSource(), player)) return;
        ItemStack sword = getSword(player);
        if (sword.isEmpty()) return;
        float ratio = WorldSwordItem.getLifestealRatio(WorldSwordItem.getLevel(sword));
        if (ratio > 0 && !player.level().isClientSide) player.heal(event.getAmount() * ratio);
    }
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof WorldSwordItem)) return;
        int level = WorldSwordItem.getLevel(stack), kills = WorldSwordItem.getKills(stack);
        event.getToolTip().add(Component.translatable("tooltip.worldmonster.world_sword.level",
                Component.translatable(WorldSwordItem.getLevelTranslationKey(level))).withStyle(ChatFormatting.DARK_RED));
        if (level >= 5) event.getToolTip().add(Component.translatable("tooltip.worldmonster.world_sword.max").withStyle(ChatFormatting.GOLD));
        else {
            int current = Math.max(0, kills - WorldSwordItem.getThreshold(level));
            int required = WorldSwordItem.getNextThreshold(level) - WorldSwordItem.getThreshold(level);
            event.getToolTip().add(Component.translatable("tooltip.worldmonster.world_sword.progress", current, required).withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable("tooltip.worldmonster.world_sword.remaining",
                    WorldSwordItem.getKillsRemaining(stack)).withStyle(ChatFormatting.RED));
        }
    }
}