package mc.duzo.mobedit.common.edits;

import mc.duzo.mobedit.common.edits.attribute.applier.AttributeApplier;
import mc.duzo.mobedit.common.edits.attribute.holder.AttributeHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EditedEntity {
	private int entityIndex;
	private LivingEntity entityCache;
	private List<AttributeHolder> attributes;

	public EditedEntity(int index, List<AttributeHolder> attributes) {
		this.entityIndex = index;
		this.attributes = attributes;
	}
	public EditedEntity(int index) {
		this(index, new ArrayList<>());
	}
	public EditedEntity(NbtCompound data) {
		this(data.getInt("EntityIndex"), AttributeHolder.deserializeList(data.getCompound("Attributes")));
	}

	public Optional<LivingEntity> getSelectedEntity() {
		if (this.entityCache == null) {
			Entity created = Registries.ENTITY_TYPE.get(this.entityIndex).create(MinecraftClient.getInstance().world);
			if (!(created instanceof LivingEntity)) {
				return Optional.empty();
			}

			this.entityCache = (LivingEntity) created;
		}

		return Optional.of(this.entityCache);
	}
	public int getEntityIndex() {
		return this.entityIndex;
	}
	public void addAttribute(AttributeHolder attribute) {
		this.attributes.add(attribute);
	}
	public List<AttributeHolder> getAttributes() {
		return this.attributes;
	}
	private Optional<AttributeHolder> getAttribute(String name) {
		return this.attributes
				.stream()
				.filter(attribute -> attribute.getName().equals(name))
				.findFirst();
	}
	public Optional<AttributeHolder> getAttribute(AttributeApplier applier) {
		return this.getAttribute(applier.getName());
	}
	public AttributeHolder getAttribute(AttributeApplier applier, AttributeHolder fallback) {
		return this.getAttribute(applier)
				.orElse(fallback);
	}

	public NbtCompound serialize() {
		NbtCompound nbt = new NbtCompound();
		nbt.putInt("EntityIndex", this.entityIndex);
		nbt.put("Attributes", AttributeHolder.serializeList(this.attributes));
		return nbt;
	}
}
