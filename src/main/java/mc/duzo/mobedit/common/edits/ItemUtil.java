package mc.duzo.mobedit.common.edits;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {
	private static final String ATTRIBUTES_KEY = "EditedAttributes";

	public static ItemStack createSpawnEgg(EntityType<?> type, CustomAttribute... attributes) {
		SpawnEggItem spawnEggItem = SpawnEggItem.forEntity(type);
		if (spawnEggItem == null) {
			return null;
		}

		ItemStack stack = new ItemStack(spawnEggItem);

		NbtCompound attributeNbt = new NbtCompound();
		for (CustomAttribute attr : attributes) {
			attributeNbt.put(attr.getId(), attr.serialize());
		}
		stack.getOrCreateNbt().put(ATTRIBUTES_KEY, attributeNbt);

		return stack;
	}
	public static boolean isCustomSpawnEgg(ItemStack stack) {
		return stack.getOrCreateNbt().contains(ATTRIBUTES_KEY);
	}

	public static List<CustomAttribute> getAttributes(ItemStack stack) {
		NbtCompound attributesNbt = stack.getNbt().getCompound(ATTRIBUTES_KEY);
		List<CustomAttribute> attributes = new ArrayList<>();
		for (String key : attributesNbt.getKeys()) {
			attributes.add(CustomAttribute.fromId(key, attributesNbt.getCompound(key)));
		}
		return attributes;
	}
}
