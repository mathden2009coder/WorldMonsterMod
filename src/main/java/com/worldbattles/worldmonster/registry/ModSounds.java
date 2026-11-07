package com.worldbattles.worldmonster.registry;
import com.worldbattles.worldmonster.WorldMonsterMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
public final class ModSounds {
 public static final DeferredRegister<SoundEvent> SOUND_EVENTS=DeferredRegister.create(Registries.SOUND_EVENT,WorldMonsterMod.MOD_ID);
 public static final RegistryObject<SoundEvent> HURT_1=register("entity.worldmonster.hurt_1");
 public static final RegistryObject<SoundEvent> HURT_2=register("entity.worldmonster.hurt_2");
 public static final RegistryObject<SoundEvent> HURT_3=register("entity.worldmonster.hurt_3");
 private static RegistryObject<SoundEvent> register(String name){ResourceLocation id=new ResourceLocation(WorldMonsterMod.MOD_ID,name);return SOUND_EVENTS.register(name,()->SoundEvent.createVariableRangeEvent(id));}
 private ModSounds(){}
}