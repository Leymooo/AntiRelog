package ru.leymooo.antirelog;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import ru.leymooo.annotatedyaml.ConfigurationProvider;
import ru.leymooo.annotatedyaml.provider.BukkitConfigurationProvider;
import ru.leymooo.antirelog.listeners.CooldownListener;
import ru.leymooo.antirelog.listeners.PvPListener;
import ru.leymooo.antirelog.manager.CooldownManager;
import ru.leymooo.antirelog.config.Settings;
import ru.leymooo.antirelog.listeners.WorldGuardListener;
import ru.leymooo.antirelog.manager.PvPManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Antirelog extends JavaPlugin {
    private Settings settings = new Settings(new BukkitConfigurationProvider(new File(getDataFolder(), "config.yml")));
    private Essentials essentialsPlugin;
    private PvPManager pvpManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        loadConfig();
        pvpManager = new PvPManager(settings, this);
        cooldownManager = new CooldownManager();
        detectPlugins();
        getServer().getPluginManager().registerEvents(new PvPListener(pvpManager, settings), this);
        getServer().getPluginManager().registerEvents(new CooldownListener(cooldownManager, pvpManager, settings), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("antirelog.reload")) {
            reloadSettings();
            sender.sendMessage("§aReloaded");
        }
        return true;
    }

    private void loadConfig() {
        ConfigurationProvider provider = settings.getConfigurationProvider();
        File file = provider.getConfigFile();
        //rename old config
        if (file.exists() && provider.get("config-version") == null) {
            try {
                Files.move(file.toPath(), new File(file.getParentFile(), "config.old").toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            provider.reloadFileFromDisk();
        }
        if (!file.exists()) {
            //create new file
            settings.save();
        } else if (provider.isFileSuccessfullyLoaded()) {
            settings.load();
        } else {
            getLogger().warning("Can't load settings from file, using default...");
        }
    }

    public void reloadSettings() {
        settings.getConfigurationProvider().reloadFileFromDisk();
        if (settings.getConfigurationProvider().isFileSuccessfullyLoaded()) {
            settings.load();
        }
        getServer().getScheduler().cancelTasks(this);
        pvpManager.onPluginDisable();
        pvpManager.onPluginEnable();
        cooldownManager.clearAll();
    }

    public boolean hasEssentialsPlugin() {
        return essentialsPlugin != null;
    }

    public Essentials getEssentialsPlugin() {
        return essentialsPlugin;
    }

    private void detectPlugins() {
        Plugin essentials = Bukkit.getPluginManager().getPlugin("Essentials");
        if (essentials != null && essentials.isEnabled()) {
            this.essentialsPlugin = (Essentials) essentials;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            WorldGuardWrapper.getInstance().registerEvents(this);
            Bukkit.getPluginManager().registerEvents(new WorldGuardListener(settings, pvpManager), this);
        }
    }
}
