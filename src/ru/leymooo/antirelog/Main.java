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
import ru.leymooo.antirelog.utils.TitlesUtils;
import ru.leymooo.config.Settings;

public class Main extends JavaPlugin {

    private PlayerStorage storage;
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

        storage = new PlayerStorage();
        Bukkit.getPluginManager().registerEvents(new PvPListener(storage), this);
        Bukkit.getPluginManager().registerEvents(new AppleEat(), this);
        Bukkit.getPluginManager().registerEvents(new EnderPearlLaunch(), this);
        Bukkit.getPluginManager().registerEvents(new EntityEvents(storage, is188), this);
        Bukkit.getPluginManager().registerEvents(new PotionSplash(storage), this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new PvPTask(storage), 20, 20);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        if (!is188) {
            BossBarUtils.clearBossBars();
        }
        storage.clear();
    }

}
