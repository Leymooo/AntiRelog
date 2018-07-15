package ru.leymooo.antirelog.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import ru.leymooo.antirelog.utils.Utils;
import ru.leymooo.config.Settings;

public class AppleEat implements Listener {

    private boolean is113 = Material.matchMaterial("ENCHANTED_GOLDEN_APPLE") != null;

    private Material GOLDEN_APPLE = Material.matchMaterial("GOLDEN_APPLE");
    
    private Map<Player, Long> appleCooldown = new HashMap<Player, Long>();
    private Map<Player, Long> encAppleCooldown = new HashMap<Player, Long>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAppleEat(PlayerItemConsumeEvent e) {
        ItemStack item = e.getItem();
        if (item.getType() == GOLDEN_APPLE || (is113 && item.getType() == Material.ENCHANTED_GOLDEN_APPLE)) {

            boolean isEnc = (is113 && item.getType() == Material.ENCHANTED_GOLDEN_APPLE) || item.getDurability() >= 1;
            long cd = isEnc ? Settings.IMP.ENCHANTED_GOLDEN_APPLE_COOLDOWN : Settings.IMP.GOLDEN_APPLE_COOLDOWN;

            if (Settings.IMP.DISABLED_WORLDS.contains(e.getPlayer().getWorld().getName().toLowerCase())
                    || e.getPlayer().hasPermission("antirelog.bypass") || cd <= -1) {
                return;
            }

            Map<Player, Long> playerMap = isEnc ? encAppleCooldown : appleCooldown;

            if (!playerMap.containsKey(e.getPlayer())) {
                playerMap.put(e.getPlayer(), System.currentTimeMillis());
                return;
            }

            long left = System.currentTimeMillis() - playerMap.get(e.getPlayer());
            if (left <= (cd * 1000)) {
                e.setCancelled(true);
                Utils.sendMessage(Settings.IMP.MESSAGES.APPLE_DISABLED.replace("%time%",
                        Math.round(cd - (left / 1000)) + ""), e.getPlayer());
            } else {
                playerMap.put(e.getPlayer(), System.currentTimeMillis());
            }

        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent ev) {
        appleCooldown.remove(ev.getPlayer());
        encAppleCooldown.remove(ev.getPlayer());
    }
}
