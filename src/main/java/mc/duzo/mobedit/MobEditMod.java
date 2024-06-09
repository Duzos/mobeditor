package mc.duzo.mobedit;

import mc.duzo.mobedit.commands.Commands;
import mc.duzo.mobedit.network.MobEditNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
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
		Commands.Server.init();
		MobEditNetworking.Server.init();
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

	}

	public static Optional<MinecraftServer> getServer() {
		return Optional.ofNullable(MobEditMod.SERVER);
	}

	public static double round(double value, int precision) {
		int scale = (int) Math.pow(10, precision);
		return (double) Math.round(value * scale) / scale;
	}
}