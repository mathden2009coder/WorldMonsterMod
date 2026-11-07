package com.worldbattles.worldmonster.entity;
import com.worldbattles.worldmonster.registry.ModSounds;
import net.minecraft.network.syncher.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
public class WorldMonsterEntity extends Monster implements GeoEntity {
 private static final EntityDataAccessor<Integer> ATTACK_TICKS=SynchedEntityData.defineId(WorldMonsterEntity.class,EntityDataSerializers.INT);
 private final AnimatableInstanceCache cache=GeckoLibUtil.createInstanceCache(this);
 private static final RawAnimation IDLE=RawAnimation.begin().thenLoop("animation.worldmonster.idle");
 private static final RawAnimation WALK=RawAnimation.begin().thenLoop("animation.worldmonster.walking");
 private static final RawAnimation RUN=RawAnimation.begin().thenLoop("animation.worldmonster.running");
 private static final RawAnimation ATTACK=RawAnimation.begin().thenPlay("animation.worldmonster.attacking");
 public WorldMonsterEntity(EntityType<? extends Monster> type,Level level){super(type,level);this.xpReward=35;}
 public static AttributeSupplier.Builder createAttributes(){return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH,120.0D).add(Attributes.MOVEMENT_SPEED,0.315D).add(Attributes.ATTACK_DAMAGE,9.0D).add(Attributes.ARMOR,4.0D).add(Attributes.KNOCKBACK_RESISTANCE,0.55D).add(Attributes.FOLLOW_RANGE,32.0D);}
 @Override protected void defineSynchedData(){super.defineSynchedData();this.entityData.define(ATTACK_TICKS,0);}
 @Override protected void registerGoals(){this.goalSelector.addGoal(0,new FloatGoal(this));this.goalSelector.addGoal(2,new MeleeAttackGoal(this,1.28D,false));this.goalSelector.addGoal(5,new RandomStrollGoal(this,0.85D));this.goalSelector.addGoal(6,new LookAtPlayerGoal(this,Player.class,18.0F));this.goalSelector.addGoal(7,new RandomLookAroundGoal(this));this.targetSelector.addGoal(1,new NearestAttackableTargetGoal<>(this,Player.class,true));}
 @Override public void tick(){super.tick();if(!level().isClientSide&&entityData.get(ATTACK_TICKS)>0)entityData.set(ATTACK_TICKS,entityData.get(ATTACK_TICKS)-1);}
 @Override public boolean doHurtTarget(Entity target){boolean hit=super.doHurtTarget(target);if(hit&&!level().isClientSide)entityData.set(ATTACK_TICKS,15);return hit;}
 public boolean isPlayingAttackAnimation(){return entityData.get(ATTACK_TICKS)>0;}
 @Override protected SoundEvent getHurtSound(DamageSource source){return switch(random.nextInt(3)){case 0->ModSounds.HURT_1.get();case 1->ModSounds.HURT_2.get();default->ModSounds.HURT_3.get();};}
 @Override protected SoundEvent getDeathSound(){return SoundEvents.RAVAGER_DEATH;}
 @Override public MobType getMobType(){return MobType.UNDEAD;}
 @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllers){controllers.add(new AnimationController<>(this,"main",3,state->{if(isPlayingAttackAnimation())return state.setAndContinue(ATTACK);if(state.isMoving())return state.setAndContinue(getTarget()!=null?RUN:WALK);return state.setAndContinue(IDLE);}));}
 @Override public AnimatableInstanceCache getAnimatableInstanceCache(){return cache;}
}