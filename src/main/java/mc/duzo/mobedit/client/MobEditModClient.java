package mc.duzo.mobedit.client;

import mc.duzo.mobedit.commands.Commands;
import mc.duzo.mobedit.common.edits.edited.client.EditedEntityFiles;
import mc.duzo.mobedit.network.MobEditNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.nio.file.Path;

import static mc.duzo.mobedit.MobEditMod.getSavePath;

public class MobEditModClient implements ClientModInitializer {
    public static EditedEntityFiles editedEntities;

    @Override
    public void onInitializeClient() {
        Commands.Client.init();
        MobEditNetworking.Client.init();

        editedEntities = new EditedEntityFiles(getClientSavePath());

        System.out.println(getClientSavePath());

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            editedEntities.writeToFiles(getClientSavePath());
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            editedEntities = new EditedEntityFiles(getClientSavePath());
        });
    }

    // Client read/write
    public static Path getClientSavePath() {
        return MinecraftClient.getInstance().getResourcePackDir().getParent(); // :3
    }
    public static Path getClientSavePath(String name, String suffix) throws IOException {
        return getSavePath(getClientSavePath(), name, suffix);
    }
}
