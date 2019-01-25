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

import ru.leymooo.antirelog.PlayerStorage;
import ru.leymooo.antirelog.utils.Utils;
import ru.leymooo.config.Settings;

public class AppleEat implements Listener {

    private final boolean           is113            = Material.matchMaterial("ENCHANTED_GOLDEN_APPLE") != null;
    private final Material          GOLDEN_APPLE     = Material.matchMaterial("GOLDEN_APPLE");
    private final Map<Player, Long> appleCooldown    = new HashMap<Player, Long>();
    private final Map<Player, Long> encAppleCooldown = new HashMap<Player, Long>();
    private final PlayerStorage     playerStorage;

    public AppleEat(PlayerStorage playerStorage) {
        this.playerStorage = playerStorage;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAppleEat(PlayerItemConsumeEvent e) {
        ItemStack item = e.getItem();
        if (item.getType() == GOLDEN_APPLE || (is113 && item.getType() == Material.ENCHANTED_GOLDEN_APPLE)) {
            boolean isEnchanted = (is113 && item.getType() == Material.ENCHANTED_GOLDEN_APPLE) || item.getDurability() >= 1;
            long cd = isEnchanted ? Settings.IMP.ENCHANTED_GOLDEN_APPLE_COOLDOWN : Settings.IMP.GOLDEN_APPLE_COOLDOWN;
            if (Settings.IMP.DISABLED_WORLDS.contains(e.getPlayer().getWorld().getName().toLowerCase())
                        || e.getPlayer().hasPermission("antirelog.bypass") || cd <= 0) {
                return;

            }
            Map<Player, Long> playerMap = isEnchanted ? encAppleCooldown : appleCooldown;
            if (playerMap.containsKey(e.getPlayer())) {
                long left = System.currentTimeMillis() - playerMap.get(e.getPlayer());
                boolean active = Settings.IMP.PVP_TIME > 0 ? playerStorage.isInPvP(e.getPlayer()) : true;
                if (active && (left <= (cd * 1000))) {
                    e.setCancelled(true);
                    Utils.sendMessage(Settings.IMP.MESSAGES.APPLE_DISABLED.replace("%time%", Math.round(cd - (left / 1000)) + ""),
                                e.getPlayer());
                    return;
                }
            }
            playerMap.put(e.getPlayer(), System.currentTimeMillis());

        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent ev) {
        appleCooldown.remove(ev.getPlayer());
        encAppleCooldown.remove(ev.getPlayer());
    }
}
