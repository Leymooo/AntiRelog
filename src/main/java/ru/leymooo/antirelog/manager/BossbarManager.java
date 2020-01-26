package ru.leymooo.antirelog.manager;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import ru.leymooo.antirelog.config.Settings;
import ru.leymooo.antirelog.util.Utils;
import ru.leymooo.antirelog.util.VersionUtils;

import java.util.HashMap;
import java.util.Map;

public class BossbarManager {

    private final Map<Integer, BossBar> bossBars = new HashMap<>();
    private final Settings settings;

    public BossbarManager(Settings settings) {
        this.settings = settings;
    }

    public void createBossBars() {
        bossBars.clear();
        if (VersionUtils.isVersion(9) && settings.getPvpTime() > 0) {
            String title = Utils.color(settings.getMessages().getInPvpBossbar());
            if (!title.isEmpty()) {
                double add = 1d / (double) settings.getPvpTime();
                double progress = add;
                for (int i = 1; i <= settings.getPvpTime(); i++) {
                    String actualTitle = Utils.replaceTime(title, i);
                    BossBar bar = Bukkit.createBossBar(actualTitle, BarColor.RED, BarStyle.SOLID);
                    bar.setProgress(progress);
                    progress += add;
                    bossBars.put(i, bar);
                    if (progress > 1.000d) {
                        progress = 1.000d;
                    }
                }
            }
        }
    }

    public void setBossBar(Player player, int time) {
        if (!bossBars.isEmpty()) {
            for (BossBar bar : bossBars.values()) {
                bar.removePlayer(player);
            }
            bossBars.get(time).addPlayer(player);
        }
    }

    public void clearBossbar(Player player) {
        for (BossBar bar : bossBars.values()) {
            bar.removePlayer(player);
        }
    }

    public void clearBossbars() {
        if (!bossBars.isEmpty()) {
            for (BossBar bar : bossBars.values()) {
                bar.removeAll();
            }
        }
        bossBars.clear();
    }
}
