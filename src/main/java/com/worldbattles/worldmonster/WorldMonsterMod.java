package com.worldbattles.worldmonster;

import com.worldbattles.worldmonster.entity.WorldMonsterEntity;
import com.worldbattles.worldmonster.network.ModNetwork;
import com.worldbattles.worldmonster.registry.ModEntities;
import com.worldbattles.worldmonster.registry.ModItems;
import com.worldbattles.worldmonster.registry.ModSounds;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WorldMonsterMod.MOD_ID)
public class WorldMonsterMod {
    public static final String MOD_ID = "worldmonster";
    public WorldMonsterMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITY_TYPES.register(bus);
        ModItems.ITEMS.register(bus);
        ModSounds.SOUND_EVENTS.register(bus);
        ModNetwork.register();
        bus.addListener(this::createAttributes);
        bus.addListener(this::addCreativeItems);
    }
    private void createAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.WORLD_MONSTER.get(), WorldMonsterEntity.createAttributes().build());
    }
    private void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) event.accept(ModItems.WORLD_MONSTER_SPAWN_EGG);
        if (event.getTabKey() == CreativeModeTabs.COMBAT) event.accept(ModItems.WORLD_SWORD);
    }
}