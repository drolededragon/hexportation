package dev.kineticcat.hexportation.forge;

import dev.kineticcat.hexportation.api.config.HexportationConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class HexportationConfigForge {

    public static void init() {
        Pair<Common, ForgeConfigSpec> config = (new ForgeConfigSpec.Builder()).configure(Common::new);
        Pair<Client, ForgeConfigSpec> clientConfig = (new ForgeConfigSpec.Builder()).configure(Client::new);
        Pair<Server, ForgeConfigSpec> serverConfig = (new ForgeConfigSpec.Builder()).configure(Server::new);
        HexportationConfig.setCommon(config.getLeft());
        HexportationConfig.setClient(clientConfig.getLeft());
        HexportationConfig.setServer(serverConfig.getLeft());
        ModLoadingContext mlc = ModLoadingContext.get();
        mlc.registerConfig(ModConfig.Type.COMMON, config.getRight());
        mlc.registerConfig(ModConfig.Type.CLIENT, clientConfig.getRight());
        mlc.registerConfig(ModConfig.Type.SERVER, serverConfig.getRight());
    }

    public static class Common implements HexportationConfig.CommonConfigAccess {
        public Common(ForgeConfigSpec.Builder builder) {

        }
    }

    public static class Client implements HexportationConfig.ClientConfigAccess {
        public Client(ForgeConfigSpec.Builder builder) {

        }
    }

    public static class Server implements HexportationConfig.ServerConfigAccess {
        // costs of actions
        private static ForgeConfigSpec.DoubleValue congratsCost;
        private static ForgeConfigSpec.DoubleValue signumCost;

        public Server(ForgeConfigSpec.Builder builder) {
            // No server configuration currently needed
        }

    }
}
