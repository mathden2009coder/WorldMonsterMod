package com.worldbattles.worldmonster.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.worldbattles.worldmonster.WorldMonsterMod;
import com.worldbattles.worldmonster.network.ModNetwork;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = WorldMonsterMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
class WorldSwordKeyRegistration {
    static final KeyMapping SHOCKWAVE = new KeyMapping("key.worldmonster.world_sword_shockwave",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.worldmonster");
    @SubscribeEvent public static void registerKeys(RegisterKeyMappingsEvent event) { event.register(SHOCKWAVE); }
}
@Mod.EventBusSubscriber(modid = WorldMonsterMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
class WorldSwordKeyInput {
    @SubscribeEvent public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && WorldSwordKeyRegistration.SHOCKWAVE.consumeClick()) ModNetwork.sendShockwave();
    }
}