package dev.emortal.immortal.demo;

import dev.emortal.immortal.Game;
import dev.emortal.immortal.tracker.Tracker;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.ServerFlag;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ParkourTagGame extends Game {

    private static final Team ALIVE_TEAM = MinecraftServer.getTeamManager().createBuilder("aliveTeam")
            .teamColor(NamedTextColor.GREEN)
            .build();
    private static final Team DEAD_TEAM = MinecraftServer.getTeamManager().createBuilder("deadTeam")
            .teamColor(NamedTextColor.RED)
            .prefix(Component.text("DEAD ", NamedTextColor.GOLD))
            .build();

    private final Instance lobbyInstance;

    public ParkourTagGame(@NotNull Tracker tracker, @NotNull Set<Player> players, @NotNull Instance lobbyInstance) {
        super(tracker, players);

        this.lobbyInstance = lobbyInstance;
    }

    @Override
    public void onStart() {
        registerEvents();
    }

    private void registerEvents() {
        // Make sure to use getEventNode() as it only listens to events happening in this game
        // (otherwise you may receive events from other games)

        getEventNode().addListener(EntityAttackEvent.class, e -> {
            // Ignore non-players
            if (!(e.getEntity() instanceof Player killer)) return;
            if (!(e.getTarget() instanceof Player target)) return;

            // Ignore dead players
            if (killer.getGameMode() != GameMode.ADVENTURE) return;
            if (target.getGameMode() != GameMode.ADVENTURE) return;

            killPlayer(target, killer);
        });
    }

    private void killPlayer(Player target, Player killer) {
        getInstance().sendMessage(
                Component.text()
                        .append(Component.text(target.getUsername()))
                        .append(Component.text(" was killed by "))
                        .append(Component.text(killer.getUsername()))
                        .build()
        );

        target.setTeam(DEAD_TEAM);
        target.setGameMode(GameMode.SPECTATOR);
        target.setInvisible(true);

        killer.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 1f, 1f), Sound.Emitter.self());

        checkVictory();
    }

    private void checkVictory() {
        List<Player> alivePlayers = new ArrayList<>();
        for (Player player : getPlayers()) {
            if (player.getGameMode() == GameMode.ADVENTURE) alivePlayers.add(player);
        }
        if (alivePlayers.size() == 1) {
            victory(alivePlayers.getFirst());
        }
    }

    private void victory(Player winner) {
        getInstance().sendMessage(
                Component.text()
                        .append(Component.text(winner.getUsername()))
                        .append(Component.text(" won the game!"))
                        .build()
        );

        // End the game after 6 seconds
        // You should consider using instance.scheduler(), entity.scheduler(), player.scheduler() where possible as it
        // will automatically cancel the task if the instance is unregistered, entity is removed, or player disconnects
        getInstance().scheduler().buildTask(() -> {
            end();
        }).delay(TaskSchedule.tick(ServerFlag.SERVER_TICKS_PER_SECOND * 6)).schedule();
    }

    @Override
    public void onPlayerJoin(Player player) {
        player.setTeam(ALIVE_TEAM);
        player.setGameMode(GameMode.ADVENTURE);
        player.setInvisible(false);
    }

    @Override
    public void onPlayerLeave(Player player, boolean disconnected) {
        getInstance().sendMessage(
                Component.text()
                        .append(Component.text(player.getUsername()))
                        .append(Component.text(" left the game"))
                        .build()
        );
        player.setTeam(null);
    }

    @Override
    public void onEnd() {
        for (Player player : getPlayers()) {
            player.setTeam(null);
            player.setGameMode(GameMode.ADVENTURE);
            player.setInvisible(false);
            player.setInstance(lobbyInstance);
        }
    }

    @Override
    public @NotNull Pos pickSpawnPoint(Player player) {
        return new Pos(0, 2, 0);
    }

    @Override
    public @NotNull Instance createInstance() {
        InstanceContainer instance = MinecraftServer.getInstanceManager().createInstanceContainer();
        instance.setGenerator(unit -> {
            unit.modifier().fillHeight(0, 1, Block.IRON_BLOCK);
        });


        // Only load certain chunks
        instance.enableAutoChunkLoad(false);
        int radius = 3;
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                instance.loadChunk(x, z);
            }
        }

        return instance;
    }

}
