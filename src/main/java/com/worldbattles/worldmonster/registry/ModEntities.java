package com.worldbattles.worldmonster.registry;
import com.worldbattles.worldmonster.WorldMonsterMod;
import com.worldbattles.worldmonster.entity.WorldMonsterEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
public final class ModEntities {
 public static final DeferredRegister<EntityType<?>> ENTITY_TYPES=DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,WorldMonsterMod.MOD_ID);
 public static final RegistryObject<EntityType<WorldMonsterEntity>> WORLD_MONSTER=ENTITY_TYPES.register("worldmonster",()->EntityType.Builder.of(WorldMonsterEntity::new,MobCategory.MONSTER).sized(1.25F,2.85F).clientTrackingRange(12).fireImmune().build(WorldMonsterMod.MOD_ID+":worldmonster"));
 private ModEntities(){}
}