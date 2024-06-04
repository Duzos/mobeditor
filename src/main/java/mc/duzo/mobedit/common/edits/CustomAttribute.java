package mc.duzo.mobedit.common.edits;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public abstract class CustomAttribute {
	public CustomAttribute(double target) {
		this.target = target;
	}
	public CustomAttribute(NbtCompound nbt) {
		this(nbt.getDouble("Target"));

		this.deserialize(nbt);
	}

	protected double target;
	protected abstract UUID getAttributeUuid();
	protected abstract String getId();
	protected abstract EntityAttributeModifier create(double target, LivingEntity entity);
	protected abstract EntityAttributeModifier create(double target, double current);
	protected abstract EntityAttributeModifier apply(double target, LivingEntity entity);
	public EntityAttributeModifier apply(LivingEntity entity) {
		return apply(this.target, entity);
	}
	public abstract boolean hasModifier(LivingEntity entity);
	public void tryApply(LivingEntity entity) {
		if (!hasModifier(entity)) {
			apply(entity);
		}
	}
	public NbtCompound serialize() {
		NbtCompound compound = new NbtCompound();

		compound.putDouble("Target", this.target);

		return compound;
	}
	public void deserialize(NbtCompound nbt) {}

	public static CustomAttribute fromId(String id, NbtCompound data) {
		// this is bad, cannot easily add new attributes because of hardcoding

		if (id.equalsIgnoreCase("Health")) {
			return new Health(data);
		}

		return null;
	}


	public static class Health extends CustomAttribute {
		private static final UUID HEALTH_ID = UUID.fromString("22659aa7-4ce9-431f-a091-6b129adc5d01");

		public Health(double target) {
			super(target);
		}

		public Health(NbtCompound data) {
			super(data);
		}

		@Override
		protected UUID getAttributeUuid() {
			return HEALTH_ID;
		}

		@Override
		protected String getId() {
			return "Health";
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
				result = -(current - target);
			}

			return new EntityAttributeModifier(this.getAttributeUuid(), "Xeon Custom Health", result, EntityAttributeModifier.Operation.ADDITION);
		}

		@Override
		protected EntityAttributeModifier apply(double target, LivingEntity entity) {
			EntityAttributeModifier modifier = create(target, entity);
			entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(modifier);
			return modifier;
		}

		@Override
		public boolean hasModifier(LivingEntity entity) {
			return entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).getModifier(this.getAttributeUuid()) != null;
		}
	}
}
