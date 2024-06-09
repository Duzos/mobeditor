package mc.duzo.mobedit.common.edits;

import mc.duzo.mobedit.common.edits.attribute.holder.AttributeHolder;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {
	private static final String ATTRIBUTES_KEY = "EditedAttributes";

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

		NbtCompound attributeNbt = new NbtCompound();
		for (AttributeHolder attr : edited.getAttributes()) {
			attributeNbt.put(attr.getName(), attr.serialize());
		}
		stack.getOrCreateNbt().put(ATTRIBUTES_KEY, attributeNbt);

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

		List<AttributeHolder> attributes = new ArrayList<>();
		for (String key : attributesNbt.getKeys()) {
			attributes.add(new AttributeHolder(attributesNbt.getCompound(key)));
		}

		return attributes;
	}
}
