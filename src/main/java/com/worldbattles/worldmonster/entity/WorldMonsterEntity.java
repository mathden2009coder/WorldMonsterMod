package com.worldbattles.worldmonster.entity;

import com.worldbattles.worldmonster.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WorldMonsterEntity extends Monster implements GeoEntity {
    private static final int ATTACK_WINDUP_TICKS = 9;
    private static final EntityDataAccessor<Integer> ATTACK_ANIMATION_TICKS =
            SynchedEntityData.defineId(WorldMonsterEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CHASING =
            SynchedEntityData.defineId(WorldMonsterEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private LivingEntity pendingAttackTarget;
    private int pendingAttackTicks;
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.worldmonster.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.worldmonster.walking");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("animation.worldmonster.running");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("animation.worldmonster.attacking");

    public WorldMonsterEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 35;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 120.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.315D).add(Attributes.ATTACK_DAMAGE, 9.0D)
                .add(Attributes.ARMOR, 4.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.55D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_ANIMATION_TICKS, 0);
        this.entityData.define(CHASING, false);
    }

    @Override protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.28D, false));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.85D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 18.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.entityData.get(ATTACK_ANIMATION_TICKS) > 0)
                this.entityData.set(ATTACK_ANIMATION_TICKS, this.entityData.get(ATTACK_ANIMATION_TICKS) - 1);

            if (this.pendingAttackTicks > 0) {
                this.pendingAttackTicks--;
                if (this.pendingAttackTicks == 0) {
                    LivingEntity victim = this.pendingAttackTarget;
                    this.pendingAttackTarget = null;
                    if (victim != null && victim.isAlive() && this.isWithinMeleeReach(victim))
                        super.doHurtTarget(victim);
                }
            }

            boolean chasing = this.getTarget() != null
                    && this.getTarget().isAlive()
                    && this.distanceToSqr(this.getTarget()) > 3.5D
                    && this.getNavigation().isInProgress();
            this.entityData.set(CHASING, chasing);

            if (this.isAlive() && this.random.nextInt(240) == 0)
                this.playSound(ModSounds.CRACKLING.get(), 0.9F, 0.9F + this.random.nextFloat() * 0.2F);
        }
    }

    private boolean isWithinMeleeReach(LivingEntity victim) {
        double reach = this.getBbWidth() * 2.0D;
        return this.distanceToSqr(victim) <= reach * reach + victim.getBbWidth();
    }

    /** Vanilla melee goal calls this at the start of its attack; damage is applied at the animation's impact frame. */
    @Override public boolean doHurtTarget(Entity target) {
        if (this.level().isClientSide || !(target instanceof LivingEntity victim))
            return false;
        if (this.pendingAttackTicks > 0)
            return false;

        this.startAttackAnimation();
        this.pendingAttackTarget = victim;
        this.pendingAttackTicks = ATTACK_WINDUP_TICKS;
        return true;
    }

    /** Starts the attack animation and sound immediately, before the delayed damage. */
    public void startAttackAnimation() {
        if (!this.level().isClientSide) {
            this.entityData.set(ATTACK_ANIMATION_TICKS, 15);
            this.playSound(this.random.nextBoolean() ? ModSounds.ATTACK_1.get() : ModSounds.ATTACK_2.get(),
                    1.0F, 0.92F + this.random.nextFloat() * 0.16F);
        }
    }

    public boolean isPlayingAttackAnimation() { return this.entityData.get(ATTACK_ANIMATION_TICKS) > 0; }
    public boolean isChasing() { return this.entityData.get(CHASING); }

    @Override protected SoundEvent getAmbientSound() {
        return this.random.nextInt(4) == 0
                ? (this.random.nextBoolean() ? ModSounds.GROWL_1.get() : ModSounds.GROWL_2.get())
                : ModSounds.BREATHING.get();
    }

    @Override protected SoundEvent getHurtSound(DamageSource source) {
        return switch (this.random.nextInt(3)) {
            case 0 -> ModSounds.HURT_1.get();
            case 1 -> ModSounds.HURT_2.get();
            default -> ModSounds.HURT_3.get();
        };
    }

    @Override protected SoundEvent getDeathSound() { return ModSounds.GROWL_3.get(); }

    @Override public void die(DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide && this.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SOUL, getX(), getY() + getBbHeight() * 0.5D, getZ(),
                    28, 0.55D, 0.65D, 0.55D, 0.035D);
            server.sendParticles(ParticleTypes.LARGE_SMOKE, getX(), getY() + 0.4D, getZ(),
                    18, 0.45D, 0.35D, 0.45D, 0.02D);
        }
    }

    @Override protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        this.spawnAtLocation(Items.SPIDER_EYE, 1 + this.random.nextInt(3));
        if (this.random.nextFloat() < 0.35F) this.spawnAtLocation(Items.GOLDEN_APPLE);
    }

    @Override public MobType getMobType() { return MobType.UNDEAD; }

    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 0, state -> {
            if (isPlayingAttackAnimation()) return state.setAndContinue(ATTACK);
            if (isChasing()) return state.setAndContinue(RUN);
            if (state.isMoving()) return state.setAndContinue(WALK);
            return state.setAndContinue(IDLE);
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }
}