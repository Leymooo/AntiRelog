package ru.leymooo.antirelog.utils;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import ru.leymooo.config.Settings;

public class BossBarUtils {
    
    private static HashMap<Integer, BossBar> bossbars = new HashMap<>();
    
    public static void setNewBossBar(Player p, int time) {
        if (bossbars == null ||bossbars.isEmpty()) {
            return;
        }
        for (BossBar bar : bossbars.values()) {
            bar.removePlayer(p);
        }
        if (time <= 0)
            return;
        bossbars.get(time).addPlayer(p);
    }

    public static void createBossBars() {
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

    public static void clearBossBars() {
        for (BossBar bar : bossbars.values()) {
            bar.removeAll();
        }
        bossbars.clear();
        bossbars = null;
    }
}
