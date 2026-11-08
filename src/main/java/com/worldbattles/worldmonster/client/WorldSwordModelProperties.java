package com.worldbattles.worldmonster.client;

import com.worldbattles.worldmonster.WorldMonsterMod;
import com.worldbattles.worldmonster.item.WorldSwordItem;
import com.worldbattles.worldmonster.registry.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = WorldMonsterMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class WorldSwordModelProperties {
    private WorldSwordModelProperties() {}
    @SubscribeEvent public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(ModItems.WORLD_SWORD.get(),
                new ResourceLocation(WorldMonsterMod.MOD_ID, "level"),
                (stack, level, entity, seed) -> WorldSwordItem.getLevel(stack)));
    }
}