package com.worldbattles.worldmonster.network;

import com.worldbattles.worldmonster.WorldMonsterMod;
import com.worldbattles.worldmonster.item.WorldSwordItem;
import com.worldbattles.worldmonster.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import java.util.function.Supplier;

public final class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(WorldMonsterMod.MOD_ID, "main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
    private ModNetwork() {}
    public static void register() {
        CHANNEL.registerMessage(0, ShockwavePacket.class, ShockwavePacket::encode, ShockwavePacket::decode, ShockwavePacket::handle);
    }
    public static void sendShockwave() { CHANNEL.sendToServer(new ShockwavePacket()); }
    public static class ShockwavePacket {
        public static void encode(ShockwavePacket packet, FriendlyByteBuf buffer) {}
        public static ShockwavePacket decode(FriendlyByteBuf buffer) { return new ShockwavePacket(); }
        public void handle(Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context context = supplier.get();
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if (player == null) return;
                ItemStack sword = player.getMainHandItem();
                if (!sword.is(ModItems.WORLD_SWORD.get())) sword = player.getOffhandItem();
                if (!sword.is(ModItems.WORLD_SWORD.get())) return;
                int level = WorldSwordItem.getLevel(sword);
                if (level < 4 || player.getCooldowns().isOnCooldown(ModItems.WORLD_SWORD.get())) return;
                releaseShockwave(player, level);
                player.getCooldowns().addCooldown(ModItems.WORLD_SWORD.get(), level == 5 ? 120 : 160);
            });
            context.setPacketHandled(true);
        }
    }
    private static void releaseShockwave(ServerPlayer player, int level) {
        ServerLevel world = player.serverLevel();
        double radius = level >= 5 ? 6.0D : 4.0D;
        float damage = level >= 5 ? 9.0F : 5.0F;
        double strength = level >= 5 ? 1.9D : 1.25D;
        world.playSound(null, player.blockPosition(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 1.0F, level >= 5 ? 0.65F : 0.85F);
        world.sendParticles(level >= 5 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SOUL,
                player.getX(), player.getY() + 0.7D, player.getZ(), level >= 5 ? 70 : 38,
                radius * 0.45D, 0.45D, radius * 0.45D, 0.08D);
        AABB area = player.getBoundingBox().inflate(radius, 2.5D, radius);
        for (LivingEntity victim : world.getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity != player && entity.isAlive() && !(entity instanceof ServerPlayer) && !entity.isAlliedTo(player))) {
            double dx = victim.getX() - player.getX(), dz = victim.getZ() - player.getZ();
            double distance = Math.max(0.1D, Math.sqrt(dx * dx + dz * dz));
            victim.hurt(player.damageSources().magic(), damage);
            victim.knockback(strength, dx / distance, dz / distance);
            if (level >= 5) victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50, 1));
            world.sendParticles(ParticleTypes.DAMAGE_INDICATOR, victim.getX(), victim.getY() + victim.getBbHeight() * 0.5D,
                    victim.getZ(), level >= 5 ? 10 : 5, 0.3D, 0.35D, 0.3D, 0.03D);
        }
    }
}