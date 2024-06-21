package mc.duzo.mobedit.common.edits.attribute.drop;

import mc.duzo.mobedit.common.edits.attribute.applier.CustomAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * for editing the drops of a mob
 * @author duzo
 */
public class DropAttribute {
	private final List<ItemStack> drops;
	private final List<ItemStack> defaultDrops;

	public DropAttribute(List<ItemStack> drops, List<ItemStack> defaultDrops) {
		this.drops = drops;
		this.defaultDrops = defaultDrops;
	}
	public DropAttribute(List<ItemStack> drops, LivingEntity entity) {
		this(drops, getDefaultLoot(entity));
	}
	public DropAttribute(List<ItemStack> defaultDrops) {
		this(new ArrayList<>(), defaultDrops);
	}
	public DropAttribute(LivingEntity entity) {
		this(getDefaultLoot(entity));
	}
	public DropAttribute(NbtCompound data) {
		this(getStackFromNbt(data, "Drops"), getStackFromNbt(data, "DefaultDrops"));
	}


	public void addDrop(ItemStack stack) {
		this.drops.add(stack);
	}
	public void removeDrop(ItemStack stack) {
		this.drops.remove(stack);
	}

	public List<ItemStack> getDrops() {
		return this.drops;
	}
	public List<ItemStack> getDefault() {
		return this.defaultDrops;
	}
	public boolean isEdited() {
		return !this.drops.isEmpty();
	}

	public void apply(LivingEntity entity) {
		((CustomAttributes) entity).mobedit$setDropModifier(this);
	}

	public static List<ItemStack> getDefaultLoot(LivingEntity entity) {
		List<ItemStack> list = new ArrayList<>();

		if (entity.getWorld().isClient()) return list; // laazy.

		Identifier identifier = entity.getLootTable();
		LootTable lootTable = entity.getWorld().getServer().getLootManager().getLootTable(identifier);
		LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld)entity.getWorld()).add(LootContextParameters.THIS_ENTITY, entity).add(LootContextParameters.ORIGIN, entity.getPos());
		LootContextParameterSet lootContextParameterSet = builder.build(LootContextTypes.ENTITY);
		lootTable.generateLoot(lootContextParameterSet, entity.getLootTableSeed(), list::add);

		return list;
	}

	public NbtCompound serialize() {
		NbtCompound data = new NbtCompound();

		NbtCompound drops = new NbtCompound();
		for (ItemStack stack : this.drops) {
			drops.put(stack.getName().getString(), stack.writeNbt(new NbtCompound()));
		}
		data.put("Drops", drops);

		NbtCompound defaultDrops = new NbtCompound();
		for (ItemStack stack : this.defaultDrops) {
			defaultDrops.put(stack.getName().getString(), stack.writeNbt(new NbtCompound()));
		}
		data.put("DefaultDrops", defaultDrops);

		return data;
	}
	public void deserialize() {

	}

	private static List<ItemStack> getStackFromNbt(NbtCompound nbt, String key) {
		NbtCompound drops = nbt.getCompound(key);

		List<ItemStack> list = new ArrayList<>();

		for (String string : drops.getKeys()) {
			list.add(ItemStack.fromNbt(drops.getCompound(string)));
		}

		return list;
	}
}
