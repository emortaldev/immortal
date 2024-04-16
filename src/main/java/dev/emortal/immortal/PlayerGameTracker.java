package dev.emortal.immortal;

import net.minestom.server.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerGameTracker {

    private final Map<UUID, Game> playerGameMap = new ConcurrentHashMap<>();

    public @Nullable Game getGame(UUID uuid) {
        return this.playerGameMap.get(uuid);
    }
    public @Nullable Game getGame(Player player) {
        return getGame(player.getUuid());
    }

    public void setGame(UUID uuid, Game game) {
        this.playerGameMap.put(uuid, game);
    }
    public void setGame(Player player, Game game) {
        setGame(player.getUuid(), game);
    }

    public void removePlayer(UUID uuid) {
        this.playerGameMap.remove(uuid);
    }
    public void removePlayer(Player player) {
        removePlayer(player.getUuid());
    }

}
