package mc.duzo.mobedit;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class MobEditMod implements ModInitializer {
	public static final String MOD_ID = "mobedit";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Random RANDOM = new Random();

	public static MinecraftServer SERVER;

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
	}

	public static boolean hasServer() {
		return SERVER != null;
	}
}