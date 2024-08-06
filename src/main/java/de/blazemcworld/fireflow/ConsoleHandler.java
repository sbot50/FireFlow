package de.blazemcworld.fireflow;

import net.minestom.server.MinecraftServer;

import java.util.Scanner;

public class ConsoleHandler {

    public static void init() {
        new Thread(ConsoleHandler::loop).start();
    }

    private static void loop() {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String line = sc.nextLine();
            switch (line) {
                case "stop" -> {
                    FireFlow.LOGGER.info("Stopping...");
                    MinecraftServer.stopCleanly();
                    return;
                }
                default -> FireFlow.LOGGER.info("Unknown command!");
            }
        }
    }

}
