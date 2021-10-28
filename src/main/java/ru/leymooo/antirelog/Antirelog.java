package ru.leymooo.antirelog;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import ru.leymooo.annotatedyaml.Configuration;
import ru.leymooo.annotatedyaml.ConfigurationProvider;
import ru.leymooo.annotatedyaml.provider.BukkitConfigurationProvider;
import ru.leymooo.antirelog.config.Settings;
import ru.leymooo.antirelog.listeners.CooldownListener;
import ru.leymooo.antirelog.listeners.EssentialsTeleportListener;
import ru.leymooo.antirelog.listeners.PvPListener;
import ru.leymooo.antirelog.listeners.WorldGuardListener;
import ru.leymooo.antirelog.manager.BossbarManager;
import ru.leymooo.antirelog.manager.CooldownManager;
import ru.leymooo.antirelog.manager.PowerUpsManager;
import ru.leymooo.antirelog.manager.PvPManager;
import ru.leymooo.antirelog.util.ProtocolLibUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;
import ru.leymooo.antirelog.util.VersionUtils;

public class Antirelog extends JavaPlugin {
    private Settings settings;
    private PvPManager pvpManager;
    private CooldownManager cooldownManager;
    private boolean protocolLib;
    private boolean worldguard;

    @Override
    public void onEnable() {
        loadConfig();
        pvpManager = new PvPManager(settings, this);
        detectPlugins();
        cooldownManager = new CooldownManager(this, settings);
        if (protocolLib) {
            ProtocolLibUtils.createListener(cooldownManager, pvpManager, this);
        }
        getServer().getPluginManager().registerEvents(new PvPListener(this, pvpManager, settings), this);
        getServer().getPluginManager().registerEvents(new CooldownListener(this, cooldownManager, pvpManager, settings), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("antirelog.reload")) {
            reloadSettings();
            sender.sendMessage("§aReloaded");
            getLogger().info(settings.toString());
        }
        return true;
    }

    private void loadConfig() {
        fixFolder();
        settings = Configuration.builder(Settings.class)
                .file(new File(getDataFolder(), "config.yml"))
                .provider(BukkitConfigurationProvider.class).build();
        ConfigurationProvider provider = settings.getConfigurationProvider();
        provider.reloadFileFromDisk();
        File file = provider.getConfigFile();
        //rename old config
        if (file.exists() && provider.get("config-version") == null) {
            try {
                Files.move(file.toPath(), new File(file.getParentFile(), "config.old." + System.nanoTime()).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            provider.reloadFileFromDisk();
        }
        if (!file.exists()) {
            //create new file
            settings.save();
            settings.loaded();
            getLogger().info("config.yml успешно создан");
        } else if (provider.isFileSuccessfullyLoaded()) {
            if (settings.load()) {
                if (!((String) provider.get("config-version")).equals(settings.getConfigVersion())) {
                    getLogger().info("Конфиг был обновлен. Проверьте новые значения");
                    settings.save();
                }
                getLogger().info("Конфиг успешно загружен");
            } else {
                getLogger().warning("Не удалось загрузить конфиг");
                settings.loaded();
            }
        } else {
            getLogger().warning("Can't load settings from file, using default...");
        }
    }


    private void fixFolder() {
        File oldFolder = new File(getDataFolder().getParentFile(), "Antirelog");
        if (!oldFolder.exists()) {
            return;
        }


        try {

            File actualFolder = oldFolder.getCanonicalFile();
            //Check if folder name is Antirelog
            if (actualFolder.getName().equals("Antirelog")) {
                File oldConfig = new File(actualFolder, "config.yml");
                if (!oldConfig.exists()) {
                    deleteFolder(actualFolder.toPath());
                    return;
                }
                //save old config
                List<String> oldConfigLines = Files.readAllLines(oldConfig.toPath(), StandardCharsets.UTF_8);
                String firstLine = oldConfigLines.size() > 0 ? oldConfigLines.get(0) : null;
                //delete old folder
                deleteFolder(actualFolder.toPath());

                //create new folder if needed
                File newFolder = getDataFolder();
                if (!newFolder.exists()) {
                    newFolder.mkdir();
                }
                File oldConfigInNewFolder = new File(newFolder, "config.yml");

                if (firstLine != null && firstLine.startsWith("config-version")) {
                    if (oldConfigInNewFolder.exists()) {
                        //save old config
                        Files.move(oldConfigInNewFolder.toPath(), new File(oldConfigInNewFolder.getParentFile(),
                                "config.old." + System.nanoTime()).toPath());
                    }
                    //write old config from Antirelog folder to AntiRelog folder
                    Files.write(oldConfigInNewFolder.toPath(), oldConfigLines, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
                    getLogger().log(Level.WARNING, "Old config.yml file from folder 'Antirelog' was moved to 'AntiRelog' folder");
                } else {
                    //Olny write old config to different file
                    Files.write(new File(oldConfigInNewFolder.getParentFile(), "config.old." + System.nanoTime()).toPath(),
                            oldConfigLines, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
                    getLogger().log(Level.WARNING, "Old config.yml file from folder 'Antirelog' was moved to 'AntiRelog' folder with " +
                            "different name");
                }
            }
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Something going wrong while renaming folder Antirelog -> AntiRelog", e);
        }
    }

    private void deleteFolder(Path folder) throws IOException {
        try (Stream<Path> walk = Files.walk(folder)) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
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

    public boolean isProtocolLibEnabled() {
        return protocolLib;
    }

    public boolean isWorldguardEnabled() {
        return worldguard;
    }

    private void detectPlugins() {
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            WorldGuardWrapper.getInstance().registerEvents(this);
            Bukkit.getPluginManager().registerEvents(new WorldGuardListener(settings, pvpManager), this);
            worldguard = true;
        }
        try {
            Class.forName("net.ess3.api.events.teleport.PreTeleportEvent");
            Bukkit.getPluginManager().registerEvents(new EssentialsTeleportListener(pvpManager, settings), this);
        } catch (ClassNotFoundException e) {
        }
        protocolLib = Bukkit.getPluginManager().isPluginEnabled("ProtocolLib") && VersionUtils.isVersion(9);
    }

    public Settings getSettings() {
        return settings;
    }

    public PvPManager getPvpManager() {
        return pvpManager;
    }

    public PowerUpsManager getPowerUpsManager() {
        return pvpManager.getPowerUpsManager();
    }

    public BossbarManager getBossbarManager() {
        return pvpManager.getBossbarManager();
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}
