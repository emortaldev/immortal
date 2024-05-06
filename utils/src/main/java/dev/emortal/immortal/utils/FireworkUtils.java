package dev.emortal.immortal.utils;

import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.projectile.FireworkRocketMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.firework.FireworkEffect;
import net.minestom.server.item.firework.FireworkEffectType;
import net.minestom.server.item.metadata.FireworkMeta;
import net.minestom.server.network.packet.server.play.EntityStatusPacket;
import net.minestom.server.utils.PacketUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class FireworkUtils {

    public static void showFirework(Set<Player> players, Instance instance, Pos position, List<FireworkEffect> effects) {
        ItemMeta fwMeta = new FireworkMeta.Builder().effects(effects).build();
        ItemStack fwItem = ItemStack.builder(Material.FIREWORK_ROCKET).meta(fwMeta).build();

        Entity firework = new Entity(EntityType.FIREWORK_ROCKET);
        ((FireworkRocketMeta) firework.getEntityMeta()).setFireworkInfo(fwItem);
        firework.setNoGravity(true);
        firework.setInstance(instance, position);

        // Immediately explode
        PacketUtils.sendGroupedPacket(players, new EntityStatusPacket(firework.getEntityId(), (byte) 17));

        firework.remove();
    }

    public static FireworkEffect randomColorEffect() {
        return new FireworkEffect(
                false,
                false,
                FireworkEffectType.LARGE_BALL,
                List.of(new Color(java.awt.Color.HSBtoRGB(ThreadLocalRandom.current().nextFloat(), 1f, 1f))),
                List.of()
        );
    }

}