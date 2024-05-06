package dev.emortal.immortal.utils;

import net.minestom.server.entity.Player;
import net.minestom.server.inventory.click.ClickType;
import org.jetbrains.annotations.NotNull;

public interface NpcHandler {

    void handlePlayerInteract(@NotNull Player player, @NotNull ClickType clickType);

}