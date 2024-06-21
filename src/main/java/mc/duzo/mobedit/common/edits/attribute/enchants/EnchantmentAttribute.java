package mc.duzo.mobedit.common.edits.attribute.enchants;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentAttribute { // Theyre called "enchants" but theyre really effects
	private final StatusEffectInstance effect;

	public EnchantmentAttribute(StatusEffectInstance effect) {
		this.effect = effect;
	}
	public EnchantmentAttribute(NbtCompound data) {
		this(StatusEffectInstance.fromNbt(data));
	}

	public void apply(LivingEntity entity) {
		entity.addStatusEffect(effect);
	}

	public String getName() {
		return this.effect.getEffectType().getName().getString();
	}
	public int getDuration() {
		return this.effect.getDuration();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EnchantmentAttribute that = (EnchantmentAttribute) o;

		return effect.equals(that.effect);
	}

	@Override
	public int hashCode() {
		return effect.hashCode();
	}

	public NbtCompound serialize() {
		NbtCompound data = new NbtCompound();

		this.effect.writeNbt(data);

		return data;
	}
	private void deserialize(NbtCompound data) {

	}

	public static NbtCompound serializeList(List<EnchantmentAttribute> list) {
		NbtCompound nbt = new NbtCompound();

		for (EnchantmentAttribute attr : list) {
			nbt.put(attr.getName(), attr.serialize());
		}

		return nbt;
	}
	public static List<EnchantmentAttribute> deserializeList(NbtCompound nbt) {
		List<EnchantmentAttribute> list = new ArrayList<>();

		for (String name : nbt.getKeys()) {
			list.add(new EnchantmentAttribute(nbt.getCompound(name)));
		}

		return list;
	}
}
