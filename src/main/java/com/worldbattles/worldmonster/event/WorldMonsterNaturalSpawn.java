package com.worldbattles.worldmonster.event;

import com.worldbattles.worldmonster.entity.WorldMonsterEntity;
import com.worldbattles.worldmonster.registry.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "worldmonster", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WorldMonsterNaturalSpawn {
    private static long ticks;
    private WorldMonsterNaturalSpawn() {}
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ++ticks % 6000L != 0L) return;
        MinecraftServer server = event.getServer();
        if (server.getPlayerList().getPlayers().isEmpty() || hasLivingMonster(server)) return;
        if (server.overworld().getRandom().nextInt(4) != 0) return;
        List<ServerLevel> eligible = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels())
            if (level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && !level.players().isEmpty()) eligible.add(level);
        if (eligible.isEmpty()) return;
        ServerLevel level = eligible.get(server.overworld().getRandom().nextInt(eligible.size()));
        List<ServerPlayer> players = level.players();
        spawnNearPlayer(level, players.get(level.getRandom().nextInt(players.size())));
    }
    private static boolean hasLivingMonster(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels())
            for (net.minecraft.world.entity.Entity entity : level.getAllEntities())
                if (entity instanceof WorldMonsterEntity && entity.isAlive()) return true;
        return false;
    }
    private static void spawnNearPlayer(ServerLevel level, ServerPlayer player) {
        var random = level.getRandom();
        double angle = random.nextDouble() * Math.PI * 2.0D;
        int distance = 48 + random.nextInt(49);
        int x = (int)Math.floor(player.getX() + Math.cos(angle) * distance);
        int z = (int)Math.floor(player.getZ() + Math.sin(angle) * distance);
        BlockPos probe = new BlockPos(x, level.getMinBuildHeight(), z);
        if (!level.getWorldBorder().isWithinBounds(probe) || !level.hasChunkAt(probe)) return;
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        BlockPos pos = new BlockPos(x, y, z);
        if (y <= level.getMinBuildHeight() || y >= level.getMaxBuildHeight() - 2
                || !level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()
                || !level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)
                || player.distanceToSqr(x + 0.5D, y, z + 0.5D) < 32.0D * 32.0D) return;
        WorldMonsterEntity monster = ModEntities.WORLD_MONSTER.get().create(level);
        if (monster == null) return;
        monster.moveTo(x + 0.5D, y, z + 0.5D, random.nextFloat() * 360.0F, 0.0F);
        if (level.noCollision(monster) && level.addFreshEntity(monster))
            level.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("message.worldmonster.spawn")
                    .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD), false);
    }
}