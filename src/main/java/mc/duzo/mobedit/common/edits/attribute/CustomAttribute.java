package mc.duzo.mobedit.common.edits.attribute;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.UUID;

public abstract class CustomAttribute {
	public CustomAttribute() {

	}
	public CustomAttribute(NbtCompound nbt) {
		this();

		this.deserialize(nbt);
	}

	protected abstract UUID getAttributeUuid();
	public abstract String getId();
	protected abstract EntityAttributeModifier create(double target, LivingEntity entity);
	protected abstract EntityAttributeModifier create(double target, double current);
	protected abstract EntityAttributeModifier apply(double target, LivingEntity entity);
	public abstract boolean hasModifier(LivingEntity entity);
	public void tryApply(double target, LivingEntity entity) {
		if (!hasModifier(entity)) {
			apply(target, entity);
		}
	}
	public NbtCompound serialize(double target) {
		NbtCompound compound = new NbtCompound();

		compound.putDouble("Target", target);

		return compound;
	}
	public void deserialize(NbtCompound nbt) {}

	public static CustomAttribute fromId(Identifier id, NbtCompound data) {
		return null;
	}

	public abstract static class SimpleAttribute extends CustomAttribute {
		private static final String ATTRIBUTE_ID_KEY = "TargetAttribute";

		private final EntityAttribute targetAttribute;

		public SimpleAttribute(EntityAttribute attribute) {
			super();
			this.targetAttribute = attribute;
		}

		public SimpleAttribute(NbtCompound nbt) {
			super(nbt);

			if (!nbt.contains(ATTRIBUTE_ID_KEY)) {
				throw new RuntimeException("Attribute not found in NBT: " + ATTRIBUTE_ID_KEY);
			}

			Identifier attrId = new Identifier(nbt.getString(ATTRIBUTE_ID_KEY));
			EntityAttribute foundAttr = Registries.ATTRIBUTE.get(attrId);
			if (foundAttr == null) {
				throw new RuntimeException("Attribute not found in registry: " + attrId);
			}

			this.targetAttribute = foundAttr;
		}

		@Override
		public NbtCompound serialize(double target) {
			NbtCompound data = super.serialize(target);

			Identifier attrId = Registries.ATTRIBUTE.getId(this.targetAttribute);

			if (attrId == null) {
				throw new RuntimeException("Attribute ID not found in registry: " + this.targetAttribute);
			}

			data.putString(ATTRIBUTE_ID_KEY, attrId.toString());

			return data;
		}

		protected EntityAttribute getTargetAttribute() {
			return this.targetAttribute;
		}

		@Override
		protected EntityAttributeModifier create(double target, LivingEntity entity) {
			return create(target, entity.getAttributeBaseValue(this.getTargetAttribute()));
		}

		@Override
		protected EntityAttributeModifier create(double target, double current) {
			double result;

			if (target > current) {
				result = target - current;
			} else {
				result = -(current - target);
			}

			return new EntityAttributeModifier(this.getAttributeUuid(), "Xeon Custom " + this.getId(), result, EntityAttributeModifier.Operation.ADDITION);
		}

		@Override
		protected EntityAttributeModifier apply(double target, LivingEntity entity) {
			EntityAttributeModifier modifier = create(target, entity);
			entity.getAttributeInstance(this.getTargetAttribute()).addPersistentModifier(modifier);
			return modifier;
		}

		@Override
		public boolean hasModifier(LivingEntity entity) {
			return entity.getAttributeInstance(this.getTargetAttribute()).getModifier(this.getAttributeUuid()) != null;
		}
	}
}
