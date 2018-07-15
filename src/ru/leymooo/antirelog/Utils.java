package ru.leymooo.antirelog;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;

import ru.leymooo.config.Settings;

public class Utils {

    private static Pattern pattern = Pattern.compile("%nl%");

    private HashMap<Integer, BossBar> bossbars = new HashMap<>();

    private String[][] titles = new String[2][2];

    public boolean checkPlayer(Player p) {
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

    public void sendMessage(String message, Player p) {
        if (!message.isEmpty()) {
            p.sendMessage(translate(message));
        }
    }

    public void sendAction(String message, Player p) {
        if (Settings.IMP.ACTIONBAR_ENABLED && !message.isEmpty()) {
            ActionBar.sendAction(p, translate(message));
        }
    }

    public String translate(String msg) {
        return ChatColor.translateAlternateColorCodes('&', pattern.matcher(msg).replaceAll("\n"));
    }

    public void sendTitles(Player p, boolean end) {
        if (Settings.IMP.TITLES_ENABLED) {
            int index = end ? 1 : 0;
            if (Main.is111) {
                p.sendTitle(titles[index][0], titles[index][1], 10, 20, 10);
            } else {
                p.sendTitle(titles[index][0], titles[index][1]);
            }
        }
    }

    public void createTitles() {
        if (Settings.IMP.TITLES_ENABLED) {
            String[] startTitle = Settings.IMP.MESSAGES.PVP_STARTED_TITLE.split("%nl%");
            if (startTitle.length == 1) {
                titles[0][0] = translate(startTitle[0]);
                titles[0][1] = null;
            } else {
                titles[0][0] = translate(startTitle[0]);
                titles[0][1] = translate(startTitle[1]);
            }
            String[] endTitle = Settings.IMP.MESSAGES.PVP_STOPPED_TITLE.split("%nl%");
            if (endTitle.length == 1) {
                titles[1][0] = translate(endTitle[0]);
                titles[1][1] = null;
            } else {
                titles[1][0] = translate(endTitle[0]);
                titles[1][1] = translate(endTitle[1]);
            }
        }
    }

    public void setNewBossBar(Player p, int time) {
        if (bossbars.isEmpty()) {
            return;
        }
        for (BossBar bar : bossbars.values()) {
            bar.removePlayer(p);
        }
        if (time == 0)
            return;
        bossbars.get(time).addPlayer(p);
    }

    public void createBossBars() {
        if (Settings.IMP.BOSSBAR_ENABLED) {
            String title = ChatColor.translateAlternateColorCodes('&', Settings.IMP.MESSAGES.PVP_BOSSBAR);
            int time = Settings.IMP.PVP_TIME;
            double add = 1d / (double) time;
            double progress = add;
            for (int i = 1; i <= time; i++) {
                BossBar bar = Bukkit.createBossBar(title.replace("%time%", i + ""), BarColor.RED, BarStyle.SOLID, BarFlag.CREATE_FOG);
                bar.setProgress(progress);
                progress += add;
                bossbars.put(i, bar);
            }
        }
    }

    public void clearBossBars() {
        for (BossBar bar : bossbars.values()) {
            bar.removeAll();
        }
        bossbars.clear();
    }
}
