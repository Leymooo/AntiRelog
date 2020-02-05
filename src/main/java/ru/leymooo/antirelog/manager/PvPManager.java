package ru.leymooo.antirelog.manager;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import ru.leymooo.antirelog.Antirelog;
import ru.leymooo.antirelog.config.Settings;
import ru.leymooo.antirelog.util.ActionBar;
import ru.leymooo.antirelog.util.CommandMapUtils;
import ru.leymooo.antirelog.util.Utils;
import ru.leymooo.antirelog.util.VersionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PvPManager {

    private final Settings settings;
    private final Antirelog plugin;
    private final Map<Player, Integer> pvpMap = new HashMap<>();
    private final PowerUpsManager powerUpsManager;
    private final BossbarManager bossbarManager;
    private final Set<String> whiteListedCommands = new HashSet<>();

    public PvPManager(Settings settings, Antirelog plugin) {
        this.settings = settings;
        this.plugin = plugin;
        this.powerUpsManager = new PowerUpsManager(settings);
        this.bossbarManager = new BossbarManager(settings);
        onPluginEnable();
    }

    public void onPluginDisable() {
        pvpMap.clear();
        this.bossbarManager.clearBossbars();
    }

    public void onPluginEnable() {
        whiteListedCommands.clear();
        if (settings.isDisableCommandsInPvp() && !settings.getWhiteListedCommands().isEmpty()) {
            settings.getWhiteListedCommands().forEach(wcommand -> {
                Command command = CommandMapUtils.getCommand(wcommand);
                whiteListedCommands.add(wcommand.toLowerCase());
                if (command != null) {
                    whiteListedCommands.add(command.getName().toLowerCase());
                    command.getAliases().forEach(alias -> whiteListedCommands.add(alias.toLowerCase()));
                }
            });
        }
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (pvpMap.isEmpty()) {
                return;
            }
            Set<Player> playersInPvp = new HashSet<>(pvpMap.keySet());
            for (Player player : playersInPvp) {
                int timeRemaining = getTimeRemainingInPvP(player) - 1;
                if (timeRemaining <= 0) {
                    stopPvP(player);
                } else {
                    updatePvpMode(player, timeRemaining);
                }
            }
        }, 20, 20);
        this.bossbarManager.createBossBars();
    }

    public boolean isInPvP(Player player) {
        return pvpMap.containsKey(player);
    }

    public int getTimeRemainingInPvP(Player player) {
        return pvpMap.getOrDefault(player, 0);
    }

    public void playerDamagedByPlayer(Player attacker, Player defender) {
        if (defender != attacker && attacker != null && defender != null) {
            tryStartPvP(attacker, true);
            tryStartPvP(defender, false);
        }
    }

    private void tryStartPvP(Player player, boolean attacker) {
        if (!canStartPvP(player)) {
            return;
        }
        if (!isPvPModeEnabled() && settings.isDisablePowerups()) {
            if (attacker) {
                powerUpsManager.disablePowerUpsWithRunCommands(player);
            } else {
                powerUpsManager.disablePowerUps(player);
            }
            return;
        }
        if (!isPvPModeEnabled()) {
            return;
        }
        if (!isInPvP(player)) {
            String message = Utils.color(settings.getMessages().getPvpStarted());
            if (!message.isEmpty()) {
                player.sendMessage(message);
            }
            if (attacker && settings.isDisablePowerups()) {
                powerUpsManager.disablePowerUpsWithRunCommands(player);
            }
            sendTitles(player, true);
        }
        updatePvpMode(player, settings.getPvpTime());
    }

    public void stopPvP(Player player) {
        stopPvPSilent(player);
        sendTitles(player, false);
        String message = Utils.color(settings.getMessages().getPvpStopped());
        if (!message.isEmpty()) {
            player.sendMessage(message);
        }
        String actionBar = settings.getMessages().getPvpStoppedActionbar();
        if (!actionBar.isEmpty()) {
            sendActionBar(player, Utils.color(actionBar));
        }
    }

    public void stopPvPSilent(Player player) {
        pvpMap.remove(player);
        bossbarManager.clearBossbar(player);
    }

    public boolean isCommandWhiteListed(String command) {
        if (whiteListedCommands.isEmpty()) {
            return false; //all commands are blocked
        }
        return whiteListedCommands.contains(command.toLowerCase());
    }

    private void updatePvpMode(Player player, int newTime) {
        pvpMap.put(player, newTime);
        bossbarManager.setBossBar(player, newTime);
        String actionBar = settings.getMessages().getInPvpActionbar();
        if (!actionBar.isEmpty()) {
            sendActionBar(player, Utils.color(Utils.replaceTime(actionBar, newTime)));
        }
        if (settings.isDisablePowerups()) {
            powerUpsManager.disablePowerUps(player);
        }
    }

    private void sendTitles(Player player, boolean isPvpStarted) {
        String title = isPvpStarted ? settings.getMessages().getPvpStartedTitle() : settings.getMessages().getPvpStoppedTitle();
        String subtitle = isPvpStarted ? settings.getMessages().getPvpStartedSubtitle() : settings.getMessages().getPvpStoppedSubtitle();
        title = title.isEmpty() ? null : Utils.color(title);
        subtitle = subtitle.isEmpty() ? null : Utils.color(subtitle);
        if (title == null && subtitle == null) {
            return;
        }
        if (VersionUtils.isVersion(11)) {
            player.sendTitle(title, subtitle, 10, 30, 10);
        } else {
            player.sendTitle(title, subtitle);
        }
    }

    private void sendActionBar(Player player, String message) {
        ActionBar.sendAction(player, message);
    }

    public boolean isPvPModeEnabled() {
        return settings.getPvpTime() > 0;
    }

    public boolean canStartPvP(Player player) {
        return !player.hasPermission("antirelog.bypass") && !settings.getDisabledWorlds().contains(player.getWorld().getName());
    }
}
