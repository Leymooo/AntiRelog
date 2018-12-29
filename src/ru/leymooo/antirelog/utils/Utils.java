package ru.leymooo.antirelog.utils;

import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;

import ru.leymooo.config.Settings;

public class Utils {

    private static Pattern pattern = Pattern.compile("%nl%");

    public static boolean checkPlayer(Player p) {
        if (p.hasPermission("antirelog.bypass") || !Settings.IMP.CHECKS_ENABLED)
            return false;
        boolean returnValue = false;
        if (p.getGameMode() == GameMode.CREATIVE) {
            p.setGameMode(GameMode.SURVIVAL);
            sendMessage(Settings.IMP.MESSAGES.GM_DISABLED, p);
            returnValue = true;
        }
        if (p.isFlying() || p.getAllowFlight()) {
            p.setFlying(false);
            p.setAllowFlight(false);
            sendMessage(Settings.IMP.MESSAGES.FLY_DISABLED, p);
            returnValue = true;
        }
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
            Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            if (ess.getUser(p).isVanished()) {
                ess.getUser(p).setVanished(false);
                sendMessage(Settings.IMP.MESSAGES.VANISH_DISABLED, p);
                returnValue = true;
            }
            if (ess.getUser(p).isGodModeEnabled()) {
                ess.getUser(p).setGodModeEnabled(false);
                sendMessage(Settings.IMP.MESSAGES.GOD_DISABLED, p);
                returnValue = true;
            }
        }
        return returnValue;
    }

    public static void sendMessage(String message, Player p) {
        if (!message.isEmpty()) {
            p.sendMessage(translate(message));
        }
    }

    public static void sendAction(String message, Player p) {
        if (Settings.IMP.ACTIONBAR_ENABLED && !message.isEmpty()) {
            ActionBarUtils.sendAction(p, translate(message));
        }
    }

    public static String translate(String msg) {
        return ChatColor.translateAlternateColorCodes('&', pattern.matcher(msg).replaceAll("\n"));
    }

}
