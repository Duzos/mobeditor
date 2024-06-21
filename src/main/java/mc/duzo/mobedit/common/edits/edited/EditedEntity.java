package mc.duzo.mobedit.common.edits.edited;

import mc.duzo.mobedit.common.edits.attribute.applier.AttributeApplier;
import mc.duzo.mobedit.common.edits.attribute.holder.AttributeHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EditedEntity {
	private final UUID id;
	private int entityIndex;
	private LivingEntity entityCache;
	private List<AttributeHolder> attributes;
	private String name;
	private boolean valid;

	public EditedEntity(UUID id, int index, List<AttributeHolder> attributes) {
		this.entityIndex = index;
		this.attributes = attributes;
		this.valid = true;
		this.id = id;
	}
	public EditedEntity(int index) {
		this(UUID.randomUUID(), index, new ArrayList<>());
	}
	public EditedEntity(NbtCompound data) {
		this(data.getUuid("Id"), data.getInt("EntityIndex"), AttributeHolder.deserializeList(data.getCompound("Attributes")));

		this.deserialize(data);
	}

	public Optional<LivingEntity> getSelectedEntity(World world) {
		if (this.entityCache == null) {
			Entity created = Registries.ENTITY_TYPE.get(this.entityIndex).create(world);
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

	public Optional<String> getName() {
		return Optional.ofNullable(this.name);
	}
	public void setName(String name) {
		this.name = name;
	}

	public UUID getUuid() {
		return this.id;
	}

	@Override
	public String toString() {
		return "EditedEntity{" +
				"entityIndex=" + entityIndex +
				", entityCache=" + entityCache +
				", attributes=" + attributes +
				", name='" + name + '\'' +
				", valid=" + valid +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EditedEntity entity = (EditedEntity) o;

		if (getEntityIndex() != entity.getEntityIndex()) return false;
		if (!getAttributes().equals(entity.getAttributes())) return false;
		return getName().isPresent() ? getName().equals(entity.getName()) : entity.getName().isEmpty();
	}

	@Override
	public int hashCode() {
		int result = getEntityIndex();
		result = 31 * result + getAttributes().hashCode();
		result = 31 * result + (getName().isPresent() ? getName().hashCode() : 0);
		return result;
	}

	public NbtCompound serialize() {
		NbtCompound nbt = new NbtCompound();

		nbt.putUuid("Id", this.id);
		nbt.putInt("EntityIndex", this.entityIndex);
		nbt.put("Attributes", AttributeHolder.serializeList(this.attributes));
		nbt.putString("EntityIdentifier", Registries.ENTITY_TYPE.getId(Registries.ENTITY_TYPE.get(this.entityIndex)).toString());

		if (this.name != null) {
			nbt.putString("Name", this.name);
		}

		return nbt;
	}
	private void deserialize(NbtCompound data) {
		if (data.contains("Name")) {
			this.setName(data.getString("Name"));
		}

		if (data.contains("EntityIdentifier")) {
			if (this.entityIndex > Registries.ENTITY_TYPE.size()) {
				this.valid = false;
				return;
			}

			Identifier expected = Registries.ENTITY_TYPE.getId(Registries.ENTITY_TYPE.get(this.entityIndex));
			Identifier found = new Identifier(data.getString("EntityIdentifier"));

			this.valid = (found.equals(expected));
		}
	}

	/**
	 * @return whether the identifier read from nbt matches the one in the registry, set when read from nbt
	 */
	public boolean isValid() {
		return this.valid;
	}
}
