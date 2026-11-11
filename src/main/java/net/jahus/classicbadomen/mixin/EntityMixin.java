package net.jahus.classicbadomen.mixin;

import net.jahus.classicbadomen.config.ModConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.OminousBottleAmplifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Intercepts vanilla ominous-bottle drops from raiders (raid captains outside a raid, etc.).
 * We do not re-check raid/captain conditions: if vanilla is about to spawn the bottle, that is enough.
 *
 * Signature in 26.3: spawnAtLocation(ServerLevel, ItemStack, float)
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

	@Inject(
			method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
			at = @At("HEAD"),
			cancellable = true
	)
	private void classicbadomen$onSpawnAtLocation(ServerLevel level, ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
		if (!ModConfig.enabled()) {
			return;
		}

		Entity self = (Entity) (Object) this;
		if (!(self instanceof Raider raider)) {
			return;
		}
		if (stack == null || stack.isEmpty() || !stack.is(Items.OMINOUS_BOTTLE)) {
			return;
		}

		boolean effectApplied = false;

		// Probability roll. On failure, we still may suppress the bottle if policy is "never".
		if (raider.getRandom().nextDouble() < ModConfig.badOmenProbability()) {
			Player beneficiary = resolveBeneficiary(raider);
			if (beneficiary instanceof ServerPlayer serverPlayer) {
				int amplifier = resolveAmplifier(serverPlayer, stack);
				serverPlayer.addEffect(new MobEffectInstance(
						MobEffects.BAD_OMEN,
						ModConfig.durationTicks(),
						amplifier
				));
				effectApplied = true;
			}
		}

		if (ModConfig.shouldSuppressBottle(effectApplied)) {
			cir.setReturnValue(null);
		}
	}

	/**
	 * Who receives Bad Omen: kill credit if a player, else last attacker if a player,
	 * else owner of a tamed animal that got the kill credit.
	 */
	@Unique
	private static Player resolveBeneficiary(Raider raider) {
		Entity credit = raider.getKillCredit();
		if (credit instanceof Player player) {
			return player;
		}
		if (credit instanceof TamableAnimal pet && pet.isTame()) {
			LivingEntity owner = pet.getOwner();
			if (owner instanceof Player player) {
				return player;
			}
		}
		LivingEntity last = raider.getLastHurtByMob();
		if (last instanceof Player player) {
			return player;
		}
		return null;
	}

	/**
	 * {@code stack} mode: +1 over current Bad Omen (cap amplifier 4 = level V).
	 * {@code drop} mode: amplifier from the bottle component (0–4).
	 */
	@Unique
	private static int resolveAmplifier(ServerPlayer player, ItemStack bottle) {
		if (ModConfig.levelsFromDrop()) {
			OminousBottleAmplifier fromBottle = bottle.get(DataComponents.OMINOUS_BOTTLE_AMPLIFIER);
			if (fromBottle != null) {
				return Math.clamp(fromBottle.value(), 0, 4);
			}
			return 0;
		}
		MobEffectInstance current = player.getEffect(MobEffects.BAD_OMEN);
		int next = current == null ? 0 : current.getAmplifier() + 1;
		return Math.min(4, next);
	}
}
