package mc.duzo.mobedit.common.edits.attribute.applier;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.UUID;

public abstract class AttributeApplier {
	protected abstract UUID getAttributeUuid();
	public abstract String getName();
	public abstract Identifier getId();
	protected abstract EntityAttributeModifier create(double target, LivingEntity entity);
	protected abstract EntityAttributeModifier create(double target, double current);
	protected abstract EntityAttributeModifier apply(double target, LivingEntity entity);
	public abstract boolean hasModifier(LivingEntity entity);
	public boolean tryApply(double target, LivingEntity entity) {
		if (!hasModifier(entity)) {
			apply(target, entity);
			return true;
		}

		return false;
	}

	public static AttributeApplier fromId(Identifier id, NbtCompound data) {
		return null;
	}

	public abstract static class SimpleAttributeApplier extends AttributeApplier {
		private final EntityAttribute targetAttribute;

		public SimpleAttributeApplier(EntityAttribute attribute) {
			super();
			this.targetAttribute = attribute;
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

			return new EntityAttributeModifier(this.getAttributeUuid(), "Xeon Custom " + this.getName(), result, EntityAttributeModifier.Operation.ADDITION);
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
