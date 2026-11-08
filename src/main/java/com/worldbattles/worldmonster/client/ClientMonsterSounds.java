package com.worldbattles.worldmonster.client;

import com.worldbattles.worldmonster.entity.WorldMonsterEntity;
import com.worldbattles.worldmonster.registry.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import java.util.HashMap;
import java.util.Map;

/** Client-side sounds whose source position is refreshed from the monster every tick. */
public final class ClientMonsterSounds {
    private static final Map<Integer, AttachedSound> ACTIVE = new HashMap<>();
    private ClientMonsterSounds() {}

    public static void play(WorldMonsterEntity entity, byte event) {
        Minecraft mc = Minecraft.getInstance();
        int id = entity.getId();
        AttachedSound previous = ACTIVE.remove(id);
        if (previous != null) mc.getSoundManager().stop(previous);

        SoundEvent sound = switch (event) {
            case 100 -> ModSounds.BREATHING.get();
            case 101 -> ModSounds.GROWL_1.get();
            case 102 -> ModSounds.GROWL_2.get();
            case 103 -> ModSounds.GROWL_3.get();
            case 104 -> ModSounds.ATTACK_1.get();
            case 105 -> ModSounds.ATTACK_2.get();
            case 106 -> ModSounds.HURT_1.get();
            case 107 -> ModSounds.HURT_2.get();
            case 108 -> ModSounds.HURT_3.get();
            case 109 -> ModSounds.CRACKLING.get();
            default -> null;
        };
        if (sound == null) return;
        AttachedSound attached = new AttachedSound(entity, sound);
        ACTIVE.put(id, attached);
        mc.getSoundManager().play(attached);
    }

    private static final class AttachedSound extends AbstractTickableSoundInstance {
        private final WorldMonsterEntity entity;
        private AttachedSound(WorldMonsterEntity entity, SoundEvent sound) {
            super(sound, SoundSource.HOSTILE, RandomSource.create());
            this.entity = entity;
            this.volume = 1.0F;
            this.pitch = 1.0F;
            this.looping = false;
            this.delay = 0;
            this.attenuation = Attenuation.LINEAR;
            updatePosition();
        }
        private void updatePosition() {
            this.x = entity.getX();
            this.y = entity.getY() + entity.getBbHeight() * 0.5D;
            this.z = entity.getZ();
        }
        @Override public void tick() {
            if (entity.isRemoved()) { this.stop(); return; }
            updatePosition();
        }
    }
}