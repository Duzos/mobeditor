package mc.duzo.mobedit.network;

import mc.duzo.mobedit.MobEditMod;
import mc.duzo.mobedit.common.edits.EditedEntity;
import mc.duzo.mobedit.common.edits.ItemUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class MobEditNetworking {
	public static final Identifier REQUEST_EGG = new Identifier(MobEditMod.MOD_ID, "request_egg");

	public static class Server {
		public static void init() {
			ServerPlayNetworking.registerGlobalReceiver(REQUEST_EGG, (server, player, handler, buf, responseSender) -> {
				receiveEggRequest(player, buf);
			});
		}

		private static void receiveEggRequest(ServerPlayerEntity source, PacketByteBuf buf) {
			if (!(source.hasPermissionLevel(2))) return; // Require OP

			NbtCompound data = buf.readNbt();
			EditedEntity created = new EditedEntity(data);
			source.giveItemStack(ItemUtil.createSpawnEgg(created));
		}
	}

	public static class Client {
		public static void init() {

		}
	}
}
