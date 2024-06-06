package mc.duzo.mobedit;

import mc.duzo.mobedit.common.edits.ItemUtil;
import mc.duzo.mobedit.common.edits.attribute.AttributeRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Random;

public class MobEditMod implements ModInitializer {
	public static final String MOD_ID = "mobedit";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Random RANDOM = new Random();

	private static MinecraftServer SERVER;

	@Override
	public void onInitialize() {
		Register.initialize();

		registerEvents();
	}

	private void registerEvents() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> MobEditMod.SERVER = server);
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> MobEditMod.SERVER = null);
		ServerWorldEvents.UNLOAD.register((server, world) -> {
			if (world.getRegistryKey() == World.OVERWORLD) {
				MobEditMod.SERVER = null;
			}
		});

		ServerWorldEvents.LOAD.register((server, world) -> {
			if (world.getRegistryKey() == World.OVERWORLD) {
				MobEditMod.SERVER = server;
			}
		});

		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof LivingEntity) this.onLoadEntity((LivingEntity) entity);
		});
	}

	private void onLoadEntity(LivingEntity entity) {
		if (entity instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) entity).giveItemStack(ItemUtil.createSpawnEgg(EntityType.PIG, new AttributeRegistry.Health()));
		}
	}

	public static Optional<MinecraftServer> getServer() {
		return Optional.ofNullable(MobEditMod.SERVER);
	}
}