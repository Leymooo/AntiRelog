package ru.leymooo.antirelog;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ru.leymooo.antirelog.listeners.AppleEat;
import ru.leymooo.antirelog.listeners.EnderPearlLaunch;
import ru.leymooo.antirelog.listeners.EntityEvents;
import ru.leymooo.antirelog.listeners.PotionSplash;
import ru.leymooo.antirelog.listeners.PvPListener;
import ru.leymooo.antirelog.utils.ActionBarUtils;
import ru.leymooo.antirelog.utils.BossBarUtils;
import ru.leymooo.antirelog.utils.PvPUtils;
import ru.leymooo.antirelog.utils.TitlesUtils;
import ru.leymooo.config.Settings;

public class Main extends JavaPlugin {

    private PvPUtils pvpUtils;
    private boolean       is188;

    @Override
    public void onEnable() {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        is188 = version.startsWith("v1_8_R");
        boolean is111 = !(is188 || version.startsWith("v1_9_R") || version.startsWith("v1_10_R"));
        ActionBarUtils.init(version);
        Settings.IMP.reload(new File(getDataFolder(), "config.yml"));
        if (!is188) {
            BossBarUtils.createBossBars();
        }
        TitlesUtils.init(is111);

        pvpUtils = new PvPUtils(new PlayerStorage());
        Bukkit.getPluginManager().registerEvents(new PvPListener(pvpUtils), this);
        Bukkit.getPluginManager().registerEvents(new AppleEat(pvpUtils.getPlayerStorage()), this);
        Bukkit.getPluginManager().registerEvents(new EnderPearlLaunch(pvpUtils.getPlayerStorage()), this);
        Bukkit.getPluginManager().registerEvents(new EntityEvents(pvpUtils, is188), this);
        Bukkit.getPluginManager().registerEvents(new PotionSplash(pvpUtils), this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new PvPTask(pvpUtils.getPlayerStorage()), 20, 20);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        if (!is188) {
            BossBarUtils.clearBossBars();
        }
        pvpUtils.getPlayerStorage().clear();
    }

}
