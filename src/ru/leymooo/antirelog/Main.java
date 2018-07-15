package ru.leymooo.antirelog;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ru.leymooo.config.Settings;

public class Main extends JavaPlugin {
    public Utils utils = new Utils();

    public static boolean is111 = false;
    public static boolean is188 = false;

    @Override
    public void onEnable() {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        is188 = version.startsWith("v1_8_R");
        is111 = !(is188 || version.startsWith("v1_9_R") || version.startsWith("v1_10_R"));
        ActionBar.init(version);
        Settings.IMP.reload(new File(getDataFolder(), "config.yml"));
        if (!is188) {
            utils.createBossBars();
        }
        utils.createTitles();
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        utils.clearBossBars();
    }

}
