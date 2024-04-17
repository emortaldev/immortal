package dev.emortal.immortal.tracker;

import dev.emortal.immortal.Game;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerGameTracker implements Tracker {

    private final Map<UUID, Game> playerGameMap = new ConcurrentHashMap<>();
    private final Map<Class<Game>, Set<Game>> gameMap = new ConcurrentHashMap<>();

    public @Nullable Game getGame(UUID uuid) {
        return this.playerGameMap.get(uuid);
    }

    public void setGame(UUID uuid, Game game) {
        this.playerGameMap.put(uuid, game);
    }

    public @Nullable Set<Game> findGames(Class<Game> gameClass) {
        return gameMap.get(gameClass);
    }

    public void removePlayer(UUID uuid) {
        this.playerGameMap.remove(uuid);
    }

    public void registerGame(Game game) {
        if (gameMap.containsKey(game.getClass())) {
            gameMap.get(game.getClass()).add(game);
        } else {
            Set<Game> games = new HashSet<>();
            games.add(game);
            gameMap.put((Class<Game>) game.getClass(), games);
        }
    }
    public void unregisterGame(Game game) {
        Set<Game> games = gameMap.get(game.getClass());
        if (games == null) return;

        if (games.size() == 1) gameMap.remove(game.getClass());
        else gameMap.get(game.getClass()).remove(game);
    }

}
