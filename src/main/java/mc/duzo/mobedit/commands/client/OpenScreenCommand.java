package mc.duzo.mobedit.commands.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import mc.duzo.mobedit.MobEditMod;
import mc.duzo.mobedit.client.screen.ScreenHelper;
import mc.duzo.mobedit.client.screen.editor.MobEditorScreen;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class OpenScreenCommand {
	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal(MobEditMod.MOD_ID)
				.then(literal("open_screen").executes(OpenScreenCommand::runCommand).requires(source -> source.hasPermissionLevel(2))));
	}

	private static int runCommand(CommandContext<FabricClientCommandSource> context) {
		ClientPlayerEntity player = context.getSource().getPlayer();

		if (player == null) return 0;

		ScreenHelper.setScreen(new MobEditorScreen(), 500);

		return Command.SINGLE_SUCCESS;
	}
}
