package ru.leymooo.antirelog.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.leymooo.antirelog.utils.Utils;
import ru.leymooo.config.Settings;

public class EnderPearlLaunch implements Listener {

    private Map<Player, Long> perlCooldown = new HashMap<Player, Long>();

    private Material ENDER_PEARL = Material.matchMaterial("ENDER_PEARL");

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPerlLaunch(ProjectileLaunchEvent e) {
        if (Settings.IMP.ENDER_PEARL_COOLDOWN > 0 && e.getEntityType() == EntityType.ENDER_PEARL
                && e.getEntity().getShooter() instanceof Player) {
            Player p = (Player) e.getEntity().getShooter();
            if (!p.hasPermission("antirelog.bypass") && !Settings.IMP.DISABLED_WORLDS.contains(p.getWorld().getName().toLowerCase())) {
                perlCooldown.put(p, System.currentTimeMillis());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (Settings.IMP.ENDER_PEARL_COOLDOWN > 0 && e.hasItem() && e.getItem().getType() == ENDER_PEARL
                    && !e.getPlayer().hasPermission("antirelog.bypass") && perlCooldown.containsKey(e.getPlayer())) {
                long left = System.currentTimeMillis() - perlCooldown.get(e.getPlayer());
                if (left <= (Settings.IMP.ENDER_PEARL_COOLDOWN * 1000)) {
                    e.setCancelled(true);
                    Utils.sendMessage(Settings.IMP.MESSAGES.ENDER_PEARL_DISABLED.replace("%time%",
                            Math.round(Settings.IMP.ENDER_PEARL_COOLDOWN - (left / 1000)) + ""), e.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent ev) {
        perlCooldown.remove(ev.getPlayer());
    }
}
