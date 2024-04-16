package dev.emortal.immortal.demo;

import dev.emortal.immortal.Game;
import dev.emortal.immortal.PlayerGameTracker;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class StartGameCommand extends Command {
    public StartGameCommand(@NotNull PlayerGameTracker playerGameTracker, @NotNull Instance lobbyInstance) {
        super("startgame");

        setDefaultExecutor((sender, context) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be executed by a player");
                return;
            }

            sender.sendMessage("Started new parkourtag game");
            Set<Player> playersCopy = new HashSet<>(player.getInstance().getPlayers());
            Game parkourTagGame = new ParkourTagGame(playerGameTracker, playersCopy, lobbyInstance);
            parkourTagGame.start(); // Start the game immediately
            // TODO: Provide utils for creating lobby countdowns
        });
    }
}
