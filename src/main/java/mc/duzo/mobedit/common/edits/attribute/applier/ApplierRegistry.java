package mc.duzo.mobedit.common.edits.attribute.applier;

import mc.duzo.mobedit.MobEditMod;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ApplierRegistry {
	private static final RegistryKey<Registry<AttributeApplier>> REGISTRY_KEY = RegistryKey.ofRegistry(new Identifier(MobEditMod.MOD_ID, "custom_attributes"));
	public static final Registry<AttributeApplier> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

	// Custom Attributes can go here (?)
	public static class Health extends AttributeApplier.SimpleAttributeApplier {
		private static final UUID HEALTH_ID = UUID.fromString("22659aa7-4ce9-431f-a091-6b129adc5d01");
		public static final Identifier REFERENCE = new Identifier(MobEditMod.MOD_ID, "health");

		public Health() {
			super(EntityAttributes.GENERIC_MAX_HEALTH);
		}

		@Override
		protected UUID getAttributeUuid() {
			return HEALTH_ID;
		}

		@Override
		public String getName() {
			return "Health";
		}

		@Override
		public Identifier getId() {
			return REFERENCE;
		}
	}
}
