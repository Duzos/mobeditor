package mc.duzo.mobedit.mixin.server;

import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class MobEntityMixin extends LivingEntityMixin {
	@Inject(at = @At("RETURN"), method = "getXpToDrop", cancellable = true)
	protected void mobedit$getXpToDrop(CallbackInfoReturnable<Integer> cir) {
		super.mobedit$getXpToDrop(cir);
	}
}
