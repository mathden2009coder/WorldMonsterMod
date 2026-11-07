package com.worldbattles.worldmonster.client;
import com.worldbattles.worldmonster.entity.WorldMonsterEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
public class WorldMonsterRenderer extends GeoEntityRenderer<WorldMonsterEntity>{
 public WorldMonsterRenderer(EntityRendererProvider.Context context){super(context,new WorldMonsterModel());this.shadowRadius=0.8F;}
}