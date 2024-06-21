package mc.duzo.mobedit.common.edits;

import mc.duzo.mobedit.common.edits.attribute.drop.DropAttribute;
import mc.duzo.mobedit.common.edits.attribute.enchants.EnchantmentAttribute;
import mc.duzo.mobedit.common.edits.attribute.holder.AttributeHolder;
import mc.duzo.mobedit.common.edits.edited.EditedEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.List;

public class ItemUtil {
	private static final String ATTRIBUTES_KEY = "EditedAttributes";
	private static final String ENCHANTS_KEY = "EditedEnchants";
	private static final String DROPS_KEY = "EditedDrops";

	public static ItemStack createSpawnEgg(EntityType<?> type, List<AttributeHolder> attributes) {
		SpawnEggItem spawnEggItem = SpawnEggItem.forEntity(type);
		if (spawnEggItem == null) {
			return null;
		}

		ItemStack stack = new ItemStack(spawnEggItem);

		NbtCompound attributeNbt = new NbtCompound();
		for (AttributeHolder attr : attributes) {
			attributeNbt.put(attr.getName(), attr.serialize());
		}
		stack.getOrCreateNbt().put(ATTRIBUTES_KEY, attributeNbt);

		return stack;
	}
	public static ItemStack createSpawnEgg(EditedEntity edited) {
		SpawnEggItem spawnEggItem = SpawnEggItem.forEntity(Registries.ENTITY_TYPE.get(edited.getEntityIndex()));
		if (spawnEggItem == null) {
			return null;
		}

		ItemStack stack = new ItemStack(spawnEggItem);

		stack.getOrCreateNbt().put(ATTRIBUTES_KEY, AttributeHolder.serializeList(edited.getAttributes()));
		stack.getNbt().put(ENCHANTS_KEY, EnchantmentAttribute.serializeList(edited.getEnchants()));
		stack.getNbt().put(DROPS_KEY, edited.getDrops().serialize());

		if (edited.getName().isPresent()) {
			stack.setCustomName(Text.of(edited.getName().get()));
		}

		return stack;
	}
	public static boolean isCustomSpawnEgg(ItemStack stack) {
		return stack.getOrCreateNbt().contains(ATTRIBUTES_KEY);
	}

	public static List<AttributeHolder> getAttributes(ItemStack stack) {
		NbtCompound attributesNbt = stack.getOrCreateNbt().getCompound(ATTRIBUTES_KEY);
		return AttributeHolder.deserializeList(attributesNbt);
	}

	public static List<EnchantmentAttribute> getEnchants(ItemStack stack) {
		NbtCompound enchantmentsNbt = stack.getOrCreateNbt().getCompound(ENCHANTS_KEY);
		return EnchantmentAttribute.deserializeList(enchantmentsNbt);
	}
	public static DropAttribute getDrops(ItemStack stack) {
		return new DropAttribute(stack.getOrCreateNbt().getCompound(DROPS_KEY));
	}
}
