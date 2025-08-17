package dev.kineticcat.hexportation.api.config;

import dev.kineticcat.hexportation.Hexportation;

/**
 * Platform-agnostic class for statically accessing current config values.
 * If any of the config types (common, client, server) are not needed in your mod,
 * feel free to remove anything related to them in this class and platform-specific config implementations.
 */
public class HexportationConfig {
    private static final CommonConfigAccess dummyCommon = new CommonConfigAccess() {
    };
    private static final ClientConfigAccess dummyClient = new ClientConfigAccess() {
    };
    private static final ServerConfigAccess dummyServer = new ServerConfigAccess() {
    };
    private static CommonConfigAccess common = dummyCommon;
    private static ClientConfigAccess client = dummyClient;
    private static ServerConfigAccess server = dummyServer;

    public static CommonConfigAccess getCommon() {
        return common;
    }

    public static void setCommon(CommonConfigAccess common) {
        if (HexportationConfig.common != dummyCommon) {
            Hexportation.LOGGER.warn("CommonConfigAccess was replaced! Old {} New {}", HexportationConfig.common.getClass().getName(), common.getClass().getName());
        }
        HexportationConfig.common = common;
    }

    public static ClientConfigAccess getClient() {
        return client;
    }

    public static void setClient(ClientConfigAccess client) {
        if (HexportationConfig.client != dummyClient) {
            Hexportation.LOGGER.warn("ClientConfigAccess was replaced! Old {} New {}", HexportationConfig.client.getClass().getName(), client.getClass().getName());
        }
        HexportationConfig.client = client;
    }

    public static ServerConfigAccess getServer() {
        return server;
    }

    public static void setServer(ServerConfigAccess server) {

        if (HexportationConfig.server != dummyServer) {
            Hexportation.LOGGER.warn("ServerConfigAccess was replaced! Old {} New {}", HexportationConfig.server.getClass().getName(), server.getClass().getName());
        }
        HexportationConfig.server = server;
    }

    public static int bound(int toBind, int lower, int upper) {
        return Math.min(Math.max(toBind, lower), upper);
    }

    public static double bound(double toBind, double lower, double upper) {
        return Math.min(Math.max(toBind, lower), upper);
    }

    public interface CommonConfigAccess {
    }

    public interface ClientConfigAccess {
    }

    public interface ServerConfigAccess {
    }
}
