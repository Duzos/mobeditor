package mc.duzo.mobedit.common.edits;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.UUID;

public abstract class CustomAttribute {
	protected abstract UUID getId();
	protected abstract EntityAttributeModifier create(double target, LivingEntity entity);
	protected abstract EntityAttributeModifier create(double target, double current);
	public abstract EntityAttributeModifier apply(double target, LivingEntity entity);
	public abstract boolean hasModifier(LivingEntity entity);

	public void tryApply(double target, LivingEntity entity) {
		if (!hasModifier(entity)) {
			apply(target, entity);
		}
	}

	public static class Health extends CustomAttribute {
		public static Health INSTANCE = new Health();

		private static final UUID HEALTH_ID = UUID.fromString("22659aa7-4ce9-431f-a091-6b129adc5d01");

		@Override
		protected UUID getId() {
			return HEALTH_ID;
		}

		@Override
		protected EntityAttributeModifier create(double target, LivingEntity entity) {
			return create(target, entity.getAttributeBaseValue(EntityAttributes.GENERIC_MAX_HEALTH));
		}

		@Override
		protected EntityAttributeModifier create(double target, double current) {
			double result;

			if (target > current) {
				result = target - current;
			} else {
				result = target + current;
			}

			return new EntityAttributeModifier(this.getId(), "Xeon Custom Health", result, EntityAttributeModifier.Operation.ADDITION);
		}

		@Override
		public EntityAttributeModifier apply(double target, LivingEntity entity) {
			EntityAttributeModifier modifier = create(target, entity);
			entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(modifier);
			return modifier;
		}

		@Override
		public boolean hasModifier(LivingEntity entity) {
			return entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).getModifier(this.getId()) != null;
		}
	}
}
