package com.worldbattles.worldmonster.client;

import com.worldbattles.worldmonster.WorldMonsterMod;
import com.worldbattles.worldmonster.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = WorldMonsterMod.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class WorldMonsterClientEvents {
    private WorldMonsterClientEvents() {}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.WORLD_MONSTER.get(), WorldMonsterRenderer::new);
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("world_sword", WorldSwordGeometryLoader.INSTANCE);
    }
}
