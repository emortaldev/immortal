package dev.emortal.immortal.demo;

import dev.emortal.immortal.Game;
import dev.emortal.immortal.tracker.Tracker;
import dev.emortal.immortal.utils.Countdown;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.Task;

import java.util.HashSet;
import java.util.Set;

public class Lobby {

    private static final int MIN_PLAYERS = 2;

    private Task countdownTask = null;

    private final Tracker tracker;
    private final Instance instance;
    public Lobby(Tracker tracker, Instance instance) {
        this.tracker = tracker;
        this.instance = instance;

        EventNode<InstanceEvent> eventNode = instance.eventNode();

        eventNode.addListener(AddEntityToInstanceEvent.class, e -> {
            if (!(e.getEntity() instanceof Player player)) return;

            player.scheduleNextTick((a) -> {
                playerJoined(player);
            });
        });
        eventNode.addListener(RemoveEntityFromInstanceEvent.class, e -> {
            if (!(e.getEntity() instanceof Player player)) return;

            player.scheduleNextTick((a) -> {
                playerLeft(player);
            });
        });
    }

    private void playerJoined(Player player) {
        int players = instance.getPlayers().size();

        this.instance.sendMessage(
                Component.text()
                        .append(Component.text(player.getUsername()))
                        .append(Component.text(" joined. "))
                        .append(Component.text(players))
                        .append(Component.text("/"))
                        .append(Component.text(MIN_PLAYERS))
                        .build()
        );

        if (players >= MIN_PLAYERS) {
            if (countdownTask == null) beginCountdown();
        }
    }

    private void playerLeft(Player player) {
        int players = instance.getPlayers().size();

        this.instance.sendMessage(
                Component.text()
                        .append(Component.text(player.getUsername()))
                        .append(Component.text(" left. "))
                        .append(Component.text(players))
                        .append(Component.text("/"))
                        .append(Component.text(MIN_PLAYERS))
                        .build()
        );

        if (players < MIN_PLAYERS) {
            cancelCountdown();
        }
    }

    private void beginCountdown() {
        this.instance.sendMessage(Component.text("Reached minimum player count!"));

        countdownTask = Countdown.startCountdown(instance.scheduler(), 10, (num) -> {
            this.instance.sendMessage(Component.text("Starting game in " + num + " seconds!"));
        }, () -> {
            Set<Player> playersCopy = new HashSet<>(this.instance.getPlayers());
            Game parkourTagGame = new ParkourTagGame(tracker, playersCopy, instance);
            parkourTagGame.start(); // Start the game immediately

            countdownTask = null;
        });
    }

    private void cancelCountdown() {
        if (countdownTask != null) countdownTask.cancel();
        this.instance.sendMessage(Component.text("Not enough players!"));
    }
}
