package com.worldbattles.worldmonster.registry;
import com.worldbattles.worldmonster.WorldMonsterMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
public final class ModItems {
 public static final DeferredRegister<Item> ITEMS=DeferredRegister.create(ForgeRegistries.ITEMS,WorldMonsterMod.MOD_ID);
 public static final RegistryObject<Item> WORLD_MONSTER_SPAWN_EGG=ITEMS.register("worldmonster_spawn_egg",()->new SpawnEggItem(ModEntities.WORLD_MONSTER,0x120D12,0xB90020,new Item.Properties()));
 private ModItems(){}
}