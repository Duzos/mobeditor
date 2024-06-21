package mc.duzo.mobedit.mixin.server;

import mc.duzo.mobedit.common.edits.ItemUtil;
import mc.duzo.mobedit.common.edits.attribute.drop.DropAttribute;
import mc.duzo.mobedit.common.edits.attribute.enchants.EnchantmentAttribute;
import mc.duzo.mobedit.common.edits.attribute.holder.AttributeHolder;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public class ItemMixin {

	@Inject(at = @At("TAIL"), method = "Lnet/minecraft/item/Item;appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/client/item/TooltipContext;)V", cancellable = true)
	private void mobedit$appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
		if (stack.getItem() instanceof SpawnEggItem) {
			if (!ItemUtil.isCustomSpawnEgg(stack)) return;

			for (AttributeHolder attr : ItemUtil.getAttributes(stack)) {
				tooltip.add(
						Text.literal(attr.getName()).formatted(Formatting.DARK_AQUA).append(
								Text.literal(": ").formatted(Formatting.GRAY)).append(
										Text.literal("" + attr.getTarget()).formatted(Formatting.AQUA)
						));
			}
			for (EnchantmentAttribute attr : ItemUtil.getEnchants(stack)) {
				tooltip.add(
						Text.literal(attr.getName()).formatted(Formatting.LIGHT_PURPLE));
			}

			DropAttribute dropper = ItemUtil.getDrops(stack);
			if (dropper.isEdited()) {
				for (ItemStack dropStack : dropper.getDrops()) {
					tooltip.add(
							dropStack.getName().copy().formatted(Formatting.YELLOW)
					);
				}
			}
		}
	}
}
