package dev.emortal.immortal.demo;

import dev.emortal.immortal.tracker.PlayerGameTracker;
import dev.emortal.immortal.tracker.Tracker;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;

public class Main {
    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();

        Tracker tracker = new PlayerGameTracker();

        // Create lobby lobbyInstance with a flat generator
        InstanceContainer lobbyInstance = MinecraftServer.getInstanceManager().createInstanceContainer();
        lobbyInstance.setGenerator(unit -> {
            unit.modifier().fillHeight(0, 1, Block.GRASS_BLOCK);
        });

        GlobalEventHandler global = MinecraftServer.getGlobalEventHandler();

        global.addListener(AsyncPlayerConfigurationEvent.class, e -> {
            e.setSpawningInstance(lobbyInstance);
            e.getPlayer().setRespawnPoint(new Pos(0, 2, 0));
        });

        new Lobby(tracker, lobbyInstance);

        server.start("0.0.0.0", 25565);
    }
}