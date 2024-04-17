package dev.emortal.immortal;

import dev.emortal.immortal.tracker.Tracker;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class Game {

    private static final Tag<Boolean> IMMORTAL_INSTANCE_TAG = Tag.Boolean("immortalInstance");

    private final @NotNull Tracker tracker;
    private final @NotNull Set<Player> initialPlayers;
    private final @NotNull Instance instance; // TODO: Support multiple instances in the future

    public Game(@NotNull Tracker tracker, @NotNull Set<Player> initialPlayers) {
        this.tracker = tracker;
        this.initialPlayers = initialPlayers;

        this.instance = createInstance();
        this.instance.setTag(IMMORTAL_INSTANCE_TAG, true);

        tracker.registerGame(this);

        getEventNode().addListener(AddEntityToInstanceEvent.class, e -> {
            if (!(e.getEntity() instanceof Player player)) return;

            addPlayer(player);
        });

        getEventNode().addListener(RemoveEntityFromInstanceEvent.class, e -> {
            if (!(e.getEntity() instanceof Player player)) return;

            this.tracker.removePlayer(player);
            initialPlayers.remove(player);
            onPlayerLeave(player, true);


            // Unregister instance
            int playersRemaining = e.getInstance().getPlayers().size() - 1;
            if (playersRemaining == 0) {
                if (!e.getInstance().hasTag(IMMORTAL_INSTANCE_TAG)) return;

                e.getInstance().scheduleNextTick((instance) -> { // Need to wait a tick as event is called before player is fully removed
                    try {
                        MinecraftServer.getInstanceManager().unregisterInstance(instance);
                    } catch (IllegalStateException exception) {
                        exception.printStackTrace();
                    }
                });
            }
        });
    }

    private void addPlayer(Player player) {
        boolean successful = !getPlayers().contains(player);
        if (!successful) return;

        this.tracker.setGame(player, this);
        onPlayerJoin(player);
    }

    private void removePlayer(Player player) {
        boolean successful = getPlayers().contains(player);
        if (!successful) return;

        this.tracker.removePlayer(player);
        onPlayerLeave(player, false);
    }

    /**
     * Teleports all the players to the instance, then calls {@link #onStart()}
     */
    public void start() {
        for (Player player : this.initialPlayers) {
            if (player.getPlayerConnection().getConnectionState() != ConnectionState.PLAY) {
                throw new IllegalStateException("All players must be in the play state to start a game");
            }
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Player player : this.initialPlayers) {
            // Remove player from previous game if they were in one
            Game previousGame = tracker.getGame(player);
            if (previousGame != null) {
                previousGame.removePlayer(player);
            }

            CompletableFuture<Void> future = player.setInstance(this.instance, pickSpawnPoint(player))
                    .thenRun(() -> onPlayerJoin(player))
                    .exceptionally(e -> {
                        MinecraftServer.getExceptionManager().handleException(e);
                        return null;
                    });
            futures.add(future);
        }

        // Wait for all players to have joined the instance
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(this::onStart)
                .exceptionally(e -> {
                    MinecraftServer.getExceptionManager().handleException(e);
                    return null;
                });
    }

    /**
     * Called when the game is started by {@link #start()}
     * Consider using this to register event listeners and general game logic
     */
    public abstract void onStart();

    /**
     * Called when a player joins the game, via {@link #addPlayer(Player)}.
     * Use this to initialize the player, e.g. setting their gamemode, team
     */
    public abstract void onPlayerJoin(Player player);

    /**
     * Called when a player leaves the game, via {@link #removePlayer(Player)} or by disconnecting.
     * Use this to clean up data you have stored about the player, e.g. in hashmaps.
     * Note the player will not be teleported away from the instance automatically.
     */
    public abstract void onPlayerLeave(Player player, boolean disconnected);

    public void end() {
        tracker.unregisterGame(this);
        onEnd();
    }

    /**
     * Called once the game ends.
     * Use this to bring players back to a lobby or to kick them from the server.
     */
    public abstract void onEnd();

    public abstract @NotNull Pos pickSpawnPoint(Player player);

    /**
     * Called once upon game creation to create the instance.
     * Use this to choose how your instance should look, e.g. loading from anvil or loading with a generator.
     * Can also be used to preload chunks.
     * @return the instance that should be used in the game
     */
    public abstract @NotNull Instance createInstance();

    public @NotNull Set<Player> getPlayers() {
        return getInstance().getPlayers();
    }

    public @NotNull Instance getInstance() {
        return instance;
    }

    public @NotNull EventNode<InstanceEvent> getEventNode() {
        return getInstance().eventNode();
    }
}
