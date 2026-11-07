package com.worldbattles.worldmonster;
import com.worldbattles.worldmonster.entity.WorldMonsterEntity;
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
 public static final String MOD_ID="worldmonster";
 public WorldMonsterMod(){
  IEventBus bus=FMLJavaModLoadingContext.get().getModEventBus();
  ModEntities.ENTITY_TYPES.register(bus); ModItems.ITEMS.register(bus); ModSounds.SOUND_EVENTS.register(bus);
  bus.addListener(this::createAttributes); bus.addListener(this::addCreativeItems);
 }
 private void createAttributes(EntityAttributeCreationEvent e){e.put(ModEntities.WORLD_MONSTER.get(),WorldMonsterEntity.createAttributes().build());}
 private void addCreativeItems(BuildCreativeModeTabContentsEvent e){if(e.getTabKey()==CreativeModeTabs.SPAWN_EGGS)e.accept(ModItems.WORLD_MONSTER_SPAWN_EGG);}
}