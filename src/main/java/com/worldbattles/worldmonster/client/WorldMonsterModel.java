package com.worldbattles.worldmonster.client;
import com.worldbattles.worldmonster.WorldMonsterMod;
import com.worldbattles.worldmonster.entity.WorldMonsterEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
public class WorldMonsterModel extends GeoModel<WorldMonsterEntity>{
 private static final ResourceLocation MODEL=new ResourceLocation(WorldMonsterMod.MOD_ID,"geo/worldmonster.geo.json");
 private static final ResourceLocation TEXTURE=new ResourceLocation(WorldMonsterMod.MOD_ID,"textures/entity/worldmonster.png");
 private static final ResourceLocation ANIMATION=new ResourceLocation(WorldMonsterMod.MOD_ID,"animations/worldmonster.animation.json");
 @Override public ResourceLocation getModelResource(WorldMonsterEntity e){return MODEL;}
 @Override public ResourceLocation getTextureResource(WorldMonsterEntity e){return TEXTURE;}
 @Override public ResourceLocation getAnimationResource(WorldMonsterEntity e){return ANIMATION;}
}