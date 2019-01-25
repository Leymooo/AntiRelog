package ru.leymooo.antirelog.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.leymooo.antirelog.utils.BossBarUtils;
import ru.leymooo.antirelog.utils.PvPUtils;
import ru.leymooo.antirelog.utils.Utils;
import ru.leymooo.config.Settings;
import ru.leymooo.config.Settings.KICK;

public class PvPListener implements Listener {

    private final PvPUtils pvpUtils;

    public PvPListener(PvPUtils pvpUtils) {
        this.pvpUtils = pvpUtils;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (pvpUtils.getPlayerStorage().isInPvP(e.getPlayer())) {
            e.setCancelled(true);
            Utils.sendMessage(Settings.IMP.MESSAGES.CHAT_DISABLED, e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(PlayerKickEvent e) {
        if (!pvpUtils.getPlayerStorage().isInPvP(e.getPlayer())) {
            return;
        }
        KICK kick = Settings.IMP.KICK;
        boolean killed = false;

        if (kick.KILL_ON_KICK && kick.KILL_MESSAGES.isEmpty()) {
            e.getPlayer().setHealth(0);
            killed = true;
        }

        if (!killed && kick.KILL_ON_KICK) {
            List<String> messages = kick.KILL_MESSAGES;
            String reason = ChatColor.stripColor(e.getReason());
            for (String message : messages) {
                if (reason.contains(message)) {
                    e.getPlayer().setHealth(0);
                    killed = true;
                    break;
                }
            }
        }

        if (killed) {
            onPvPLeave(e.getPlayer());
        }
        pvpUtils.getPlayerStorage().removePlayerPvP(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        if (Settings.IMP.KILL_ON_LEAVE && pvpUtils.getPlayerStorage().isInPvP(e.getPlayer())) {
            e.getPlayer().setHealth(0);
            onPvPLeave(e.getPlayer());
        }
        if (Settings.IMP.REMOVE_LEAVE_MESSAGE) {
            e.setQuitMessage(null);
        }
        pvpUtils.getPlayerStorage().removePlayerPvP(e.getPlayer());
        BossBarUtils.setNewBossBar(e.getPlayer(), 0);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent e) {
        if (Settings.IMP.REMOVE_DEATH_MESSAGE) {
            e.setDeathMessage(null);
        }
        pvpUtils.getPlayerStorage().removePlayerPvP(e.getEntity());
        BossBarUtils.setNewBossBar(e.getEntity(), 0);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        if (Settings.IMP.REMOVE_JOIN_MESSAGE) {
            e.setJoinMessage(null);
        }
    }

    private void onPvPLeave(Player p) {
        String message = Utils.translate(Settings.IMP.MESSAGES.PVP_LEAVED).replace("%player%", p.getName());
        if (!message.trim().isEmpty() && Settings.IMP.SHOW_LEAVED_MESSAGE) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.sendMessage(message);
            }
        }
        if (!Settings.IMP.MESSAGES.COMMAND_ON_LEAVE.trim().isEmpty()) {
            String command = ChatColor.translateAlternateColorCodes('&', Settings.IMP.MESSAGES.COMMAND_ON_LEAVE).replace("%player%",
                        p.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}
