package mc.duzo.mobedit.common.edits.attribute.applier;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.UUID;

public abstract class AttributeApplier {
	protected abstract UUID getAttributeUuid();
	public abstract String getName();
	public abstract Identifier getId();
	protected abstract EntityAttributeModifier create(double target, LivingEntity entity);
	protected abstract EntityAttributeModifier create(double target, double current);
	protected abstract EntityAttributeModifier apply(double target, LivingEntity entity);
	public abstract Optional<Double> getDefault(LivingEntity entity);
	public abstract boolean hasModifier(LivingEntity entity);
	public boolean tryApply(double target, LivingEntity entity) {
		if (!hasModifier(entity)) {
			apply(target, entity);
			return true;
		}

		return false;
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
			if (entity.getAttributeInstance(this.getTargetAttribute()) == null) return null;

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

			EntityAttributeInstance instance = entity.getAttributeInstance(this.getTargetAttribute());
			if (instance == null) return modifier;

			instance.addPersistentModifier(modifier);

			return modifier;
		}

		@Override
		public boolean hasModifier(LivingEntity entity) {
			EntityAttributeInstance instance = entity.getAttributeInstance(this.getTargetAttribute());
			if (instance == null) return false;

			return entity.getAttributeInstance(this.getTargetAttribute()).getModifier(this.getAttributeUuid()) != null;
		}

		@Override
		public Optional<Double> getDefault(LivingEntity entity) {
			EntityAttributeInstance instance = entity.getAttributeInstance(this.getTargetAttribute());
			if (instance == null) return Optional.empty();

			return Optional.of(instance.getBaseValue());
		}
	}
}
