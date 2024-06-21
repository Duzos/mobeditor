package mc.duzo.mobedit.mixin.server;

import mc.duzo.mobedit.common.edits.ItemUtil;
import mc.duzo.mobedit.common.edits.attribute.enchants.EnchantmentAttribute;
import mc.duzo.mobedit.common.edits.attribute.holder.AttributeHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin<T extends Entity> {
	@Inject(at = @At("RETURN"), method = "Lnet/minecraft/entity/EntityType;spawnFromItemStack(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/SpawnReason;ZZ)Lnet/minecraft/entity/Entity;", cancellable = true)
	public void mobedit$spawnFromItemStack(ServerWorld world, @Nullable ItemStack stack, @Nullable PlayerEntity player, BlockPos pos, SpawnReason spawnReason, boolean alignPosition, boolean invertY, CallbackInfoReturnable<@Nullable T> cir) {
		if (stack == null) return;
		if (!ItemUtil.isCustomSpawnEgg(stack)) return;

		T result = cir.getReturnValue();
		List<AttributeHolder> attributes = ItemUtil.getAttributes(stack);

		for (AttributeHolder attr : attributes) {
			attr.tryApply((LivingEntity) result);
		}

		List<EnchantmentAttribute> enchants = ItemUtil.getEnchants(stack);

		for (EnchantmentAttribute attr : enchants) {
			attr.apply((LivingEntity) result);
		}
	}
}
