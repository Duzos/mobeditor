package mc.duzo.mobedit.common.edits.attribute.applier;

import mc.duzo.mobedit.MobEditMod;
import mc.duzo.mobedit.Register;
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

	public static final ApplierRegistry.Health HEALTH = Register.register(ApplierRegistry.REGISTRY, "health", new ApplierRegistry.Health());
	public static final ApplierRegistry.MovementSpeed MOVEMENT_SPEED = Register.register(ApplierRegistry.REGISTRY, "movement_speed", new ApplierRegistry.MovementSpeed());
	public static final ApplierRegistry.AttackDamage ATTACK_DAMAGE = Register.register(ApplierRegistry.REGISTRY, "attack_damage", new ApplierRegistry.AttackDamage());

	public static void initialize() {
	}

	// Custom Attributes can go here (?)
	// TODO - auto create these from the Attribute registry if possible
	private static class Health extends AttributeApplier.SimpleAttributeApplier {
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
	private static class MovementSpeed extends AttributeApplier.SimpleAttributeApplier {
		private static final UUID ID = UUID.fromString("910c6dfd-de58-48b8-86ff-06a69548cb59");
		public static final Identifier REFERENCE = new Identifier(MobEditMod.MOD_ID, "movement_speed");

		public MovementSpeed() {
			super(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		}

		@Override
		protected UUID getAttributeUuid() {
			return ID;
		}

		@Override
		public String getName() {
			return "Movement Speed";
		}

		@Override
		public Identifier getId() {
			return REFERENCE;
		}
	}
	private static class AttackDamage extends AttributeApplier.SimpleAttributeApplier {
		private static final UUID ID = UUID.fromString("da07e8d1-755a-497e-8239-da1fa8c787c7");
		public static final Identifier REFERENCE = new Identifier(MobEditMod.MOD_ID, "attack_damage");

		public AttackDamage() {
			super(EntityAttributes.GENERIC_ATTACK_DAMAGE);
		}

		@Override
		protected UUID getAttributeUuid() {
			return ID;
		}

		@Override
		public String getName() {
			return "Attack Damage";
		}

		@Override
		public Identifier getId() {
			return REFERENCE;
		}
	}
}
