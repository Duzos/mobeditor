package mc.duzo.mobedit.mixin.server;

import mc.duzo.mobedit.common.edits.attribute.applier.CustomAttributes;
import mc.duzo.mobedit.common.edits.attribute.drop.DropAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements CustomAttributes {
	@Unique
	public int defaultXp = -1;
	@Unique
	public int targetXp = -1;
	@Unique
	public DropAttribute customDrops;

	@Override
	public void mobeditor$setTargetXp(int target) {
		targetXp = target;
	}

	@Override
	public int mobeditor$getTargetXp() {
		return targetXp;
	}

	@Override
	public int mobeditor$getDefaultXp() {
		if (defaultXp == -1) {
			((LivingEntity) (Object) this).getXpToDrop();
		}

		return defaultXp;
	}

	@Override
	public void mobedit$setDefaultXp(int target) {
		defaultXp = target;
	}

	@Inject(at = @At("RETURN"), method = "getXpToDrop", cancellable = true)
	protected void mobedit$getXpToDrop(CallbackInfoReturnable<Integer> cir) {
		if (this.defaultXp == -1) {
			((CustomAttributes) this).mobedit$setDefaultXp(cir.getReturnValue());
		}

		int target = ((CustomAttributes) this).mobeditor$getTargetXp();
		if (target == -1) return;
		cir.setReturnValue(target);
	}

	@Override
	public DropAttribute mobedit$getDropModifier() {
		return this.customDrops;
	}

	@Override
	public void mobedit$setDropModifier(DropAttribute drop) {
		this.customDrops = drop;
	}

	@Inject(at = @At("HEAD"), method = "dropLoot", cancellable = true)
	private void mobedit$dropLoot(DamageSource damageSource, boolean causedByPlayer, CallbackInfo ci) {
		DropAttribute dropper = ((CustomAttributes) this).mobedit$getDropModifier();
		if (dropper != null) {
			if (!dropper.isEdited()) return;

			dropper.getDrops().forEach(((LivingEntity) (Object) this)::dropStack);
			ci.cancel();
		}
	}
}
