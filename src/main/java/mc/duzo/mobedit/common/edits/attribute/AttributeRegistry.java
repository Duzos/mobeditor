package mc.duzo.mobedit.common.edits.attribute;

import mc.duzo.mobedit.MobEditMod;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class AttributeRegistry {
	private static final RegistryKey<Registry<CustomAttribute>> REGISTRY_KEY = RegistryKey.ofRegistry(new Identifier(MobEditMod.MOD_ID, "custom_attributes"));
	public static final Registry<CustomAttribute> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

	// Custom Attributes can go here (?)
	public static class Health extends CustomAttribute.SimpleAttribute {
		private static final UUID HEALTH_ID = UUID.fromString("22659aa7-4ce9-431f-a091-6b129adc5d01");

		public Health() {
			super(EntityAttributes.GENERIC_MAX_HEALTH);
		}

		public Health(NbtCompound data) {
			super(data);
		}

		@Override
		protected UUID getAttributeUuid() {
			return HEALTH_ID;
		}

		@Override
		public String getId() {
			return "Health";
		}
	}
}
