package dev.emortal.immortal.demo;

import dev.emortal.immortal.Game;
import dev.emortal.immortal.tracker.Tracker;
import dev.emortal.immortal.utils.Countdown;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class StartGameCommand extends Command {
    public StartGameCommand(@NotNull Tracker tracker, @NotNull Instance lobbyInstance) {
        super("startgame");

        setDefaultExecutor((sender, context) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be executed by a player");
                return;
            }

            sender.sendMessage("Starting new parkourtag game");

            Countdown.startCountdown(player.getInstance().scheduler(), 5, num -> {
                player.getInstance().playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 1f, 1f), Sound.Emitter.self());
                player.getInstance().sendMessage(Component.text("Game starting in " + num + " seconds!"));
            }, () -> {
                Set<Player> playersCopy = new HashSet<>(player.getInstance().getPlayers());
                Game parkourTagGame = new ParkourTagGame(tracker, playersCopy, lobbyInstance);
                parkourTagGame.start(); // Start the game immediately
            });


        });
    }
}
