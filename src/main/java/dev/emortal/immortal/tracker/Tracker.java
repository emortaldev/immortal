package dev.emortal.immortal.tracker;

import dev.emortal.immortal.Game;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface Tracker {

    @Nullable Game getGame(UUID uuid);
    default @Nullable Game getGame(Player player) {
        return getGame(player.getUuid());
    }

    void setGame(UUID uuid, Game game);
    default void setGame(Player player, Game game) {
        setGame(player.getUuid(), game);
    }

    @Nullable Set<Game> getGames();
    @Nullable Set<Game> getGames(Class<Game> gameClass);

    void removePlayer(UUID uuid);
    default void removePlayer(Player player) {
        removePlayer(player.getUuid());
    }

    void registerGame(Game game);
    void unregisterGame(Game game);

}
