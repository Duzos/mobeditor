package mc.duzo.mobedit.commands;

import com.mojang.brigadier.CommandDispatcher;
import mc.duzo.mobedit.commands.client.OpenScreenCommand;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

public class Commands {
	public static class Server {
		public static void init() {
			CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> register(dispatcher)));
		}

		private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

		}
	}

	public static class Client {
		public static void init() {
			ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> register(dispatcher)));
		}

		private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
			OpenScreenCommand.register(dispatcher);
		}
	}
}
