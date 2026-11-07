package com.worldbattles.worldmonster.entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
/** Delays melee damage until the monster's animated arm reaches its strike frame. */
public class WorldMonsterMeleeAttackGoal extends MeleeAttackGoal {
    private static final int WINDUP_TICKS = 9; // 0.4583 s at 20 ticks/s
    private LivingEntity pendingTarget;
    private int windupRemaining;
    public WorldMonsterMeleeAttackGoal(WorldMonsterEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
    }
    @Override protected void checkAndPerformAttack(LivingEntity target) {
        WorldMonsterEntity monster = (WorldMonsterEntity) this.mob;
        if (this.windupRemaining > 0) {
            this.windupRemaining--;
            if (this.windupRemaining == 0) {
                LivingEntity victim = this.pendingTarget;
                this.pendingTarget = null;
                if (victim != null && victim.isAlive() && monster.distanceToSqr(victim) <= this.getAttackReachSqr(victim))
                    monster.doHurtTarget(victim);
            }
            return;
        }
        if (this.isTimeToAttack() && this.getAttackReachSqr(target) >= monster.distanceToSqr(target)) {
            this.resetAttackCooldown();
            this.pendingTarget = target;
            this.windupRemaining = WINDUP_TICKS;
            monster.startAttackAnimation();
        }
    }
    @Override public void stop() {
        super.stop();
        this.pendingTarget = null;
        this.windupRemaining = 0;
    }
}