package mc.duzo.mobedit.common.edits.attribute.holder;

import mc.duzo.mobedit.common.edits.attribute.applier.AttributeApplier;
import mc.duzo.mobedit.common.edits.attribute.applier.ApplierRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class AttributeHolder {
	private final AttributeApplier applier;
	private double target;

	public AttributeHolder(AttributeApplier applier, double target) {
		this.applier = applier;
		this.target = target;
	}
	private AttributeHolder(AttributeApplier applier) {
		this(applier, 1);
	}
	public AttributeHolder(NbtCompound data) {
		this(ApplierRegistry.REGISTRY.get(
				Identifier.tryParse(
						data.getString("Applier")
				)
		));

		this.deserialize(data);
	}

	public NbtCompound serialize() {
		NbtCompound data = new NbtCompound();

		data.putString("Applier", this.applier.getId().toString());
		data.putDouble("Target", this.target);

		return data;
	}
	private void deserialize(NbtCompound data) {
		this.target = data.getDouble("Target");
	}

	private AttributeApplier getApplier() {
		return this.applier;
	}

	public boolean tryApply(LivingEntity entity) {
		return this.getApplier().tryApply(this.target, entity);
	}
	public String getName() {
		return this.getApplier().getName();
	}

	public double getTarget() {
		return target;
	}
	private void setTarget(double target) {
		this.target = target;
	}
}
