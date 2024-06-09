package mc.duzo.mobedit.client;

import mc.duzo.mobedit.commands.Commands;
import mc.duzo.mobedit.network.MobEditNetworking;
import net.fabricmc.api.ClientModInitializer;

public class MobEditModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Commands.Client.init();
        MobEditNetworking.Client.init();
    }
}
