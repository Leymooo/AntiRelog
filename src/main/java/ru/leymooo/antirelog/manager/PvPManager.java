package ru.leymooo.antirelog.manager;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import ru.leymooo.antirelog.Antirelog;
import ru.leymooo.antirelog.config.Settings;
import ru.leymooo.antirelog.event.PvpPreStartEvent;
import ru.leymooo.antirelog.event.PvpPreStartEvent.PvPStatus;
import ru.leymooo.antirelog.event.PvpStartedEvent;
import ru.leymooo.antirelog.event.PvpStoppedEvent;
import ru.leymooo.antirelog.event.PvpTimeUpdateEvent;
import ru.leymooo.antirelog.util.ActionBar;
import ru.leymooo.antirelog.util.CommandMapUtils;
import ru.leymooo.antirelog.util.Utils;
import ru.leymooo.antirelog.util.VersionUtils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class PvPManager {

    private final Settings settings;
    private final Antirelog plugin;
    private final Map<Player, Integer> pvpMap = new HashMap<>();
    private final Map<Player, Integer> silentPvpMap = new HashMap<>();
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
            if (pvpMap.isEmpty() && silentPvpMap.isEmpty()) {
                return;
            }
            iterateMap(pvpMap, false);
            iterateMap(silentPvpMap, true);

        }, 20, 20);
        this.bossbarManager.createBossBars();
    }

    private void iterateMap(Map<Player, Integer> map, boolean bypassed) {
        if (!map.isEmpty()) {
            List<Player> playersInPvp = new ArrayList<>(map.keySet());
            for (Player player : playersInPvp) {
                int currentTime = bypassed ? getTimeRemainingInPvPSilent(player) : getTimeRemainingInPvP(player);
                int timeRemaining = currentTime - 1;
                if (timeRemaining <= 0 || (settings.isDisablePvpInIgnoredRegion() && isInIgnoredRegion(player))) {
                    if (bypassed) {
                        stopPvPSilent(player);
                    } else {
                        stopPvP(player);
                    }
                } else {
                    updatePvpMode(player, bypassed, timeRemaining);
                    callUpdateEvent(player, currentTime, timeRemaining);
                }
            }
        }
    }

    public boolean isInPvP(Player player) {
        return pvpMap.containsKey(player);
    }

    public boolean isInSilentPvP(Player player) {
        return silentPvpMap.containsKey(player);
    }

    public int getTimeRemainingInPvP(Player player) {
        return pvpMap.getOrDefault(player, 0);
    }

    public int getTimeRemainingInPvPSilent(Player player) {
        return silentPvpMap.getOrDefault(player, 0);
    }

    public void playerDamagedByPlayer(Player attacker, Player defender) {
        if (defender != attacker && attacker != null && defender != null && (attacker.getWorld() == defender.getWorld())) {
            if (defender.getGameMode() == GameMode.CREATIVE) { //i dont have time to determite, why some events is called when defender in creative
                return;
            }

            if (attacker.hasMetadata("NPC") || defender.hasMetadata("NPC")) {
                return;
            }

            if (defender.isDead() || attacker.isDead()) {
                return;
            }
            tryStartPvP(attacker, defender);
        }
    }

    private void tryStartPvP(Player attacker, Player defender) {
        if (isInIgnoredWorld(attacker)) {
            return;
        }

        if (isInIgnoredRegion(attacker) || isInIgnoredRegion(defender)) {
            return;
        }

        if (!isPvPModeEnabled() && settings.isDisablePowerups()) {
            if (!isHasBypassPermission(attacker)) {
                powerUpsManager.disablePowerUpsWithRunCommands(attacker);
            }
            if (!isHasBypassPermission(defender)) {
                powerUpsManager.disablePowerUps(defender);
            }
            return;
        }

        if (!isPvPModeEnabled()) {
            return;
        }

        boolean attackerBypassed = isHasBypassPermission(attacker);
        boolean defenderBypassed = isHasBypassPermission(defender);

        if (attackerBypassed && defenderBypassed) {
            return;
        }

        boolean attackerInPvp = isInPvP(attacker) || isInSilentPvP(attacker);
        boolean defenderInPvp = isInPvP(defender) || isInSilentPvP(defender);
        PvPStatus pvpStatus = PvPStatus.ALL_NOT_IN_PVP;
        if (attackerInPvp && defenderInPvp) {
            updateAttackerAndCallEvent(attacker, defender, attackerBypassed);
            updateDefenderAndCallEvent(defender, attacker, defenderBypassed);
            return;
        } else if (attackerInPvp) {
            pvpStatus = PvPStatus.ATTACKER_IN_PVP;
        } else if (defenderInPvp) {
            pvpStatus = PvPStatus.DEFENDER_IN_PVP;
        }
        if (pvpStatus == PvPStatus.ATTACKER_IN_PVP || pvpStatus == PvPStatus.DEFENDER_IN_PVP) {
            if (callPvpPreStartEvent(defender, attacker, pvpStatus)) {
                if (attackerInPvp) {
                    updateAttackerAndCallEvent(attacker, defender, attackerBypassed);
                    startPvp(defender, defenderBypassed, false);
                } else {
                    updateDefenderAndCallEvent(defender, attacker, defenderBypassed);
                    startPvp(attacker, attackerBypassed, true);
                }
                Bukkit.getPluginManager().callEvent(new PvpStartedEvent(defender, attacker, settings.getPvpTime(), pvpStatus));
            }
            return;
        }

        if (callPvpPreStartEvent(defender, attacker, pvpStatus)) {
            startPvp(attacker, attackerBypassed, true);
            startPvp(defender, defenderBypassed, false);
            Bukkit.getPluginManager().callEvent(new PvpStartedEvent(defender, attacker, settings.getPvpTime(), pvpStatus));
        }

    }


    private void startPvp(Player player, boolean bypassed, boolean attacker) {
        if (!bypassed) {
            String message = Utils.color(settings.getMessages().getPvpStarted());
            if (!message.isEmpty()) {
                player.sendMessage(message);
            }
            if (attacker && settings.isDisablePowerups()) {
                powerUpsManager.disablePowerUpsWithRunCommands(player);
            }
            sendTitles(player, true);
        }
        updatePvpMode(player, bypassed, settings.getPvpTime());
        player.setNoDamageTicks(0);
    }

    private void updatePvpMode(Player player, boolean bypassed, int newTime) {
        if (bypassed) {
            silentPvpMap.put(player, newTime);
        } else {
            pvpMap.put(player, newTime);
            bossbarManager.setBossBar(player, newTime);
            String actionBar = settings.getMessages().getInPvpActionbar();
            if (!actionBar.isEmpty()) {
                sendActionBar(player, Utils.color(Utils.replaceTime(actionBar, newTime)));
            }
            if (settings.isDisablePowerups()) {
                powerUpsManager.disablePowerUps(player);
            }
            //player.setNoDamageTicks(0);
        }
    }

    private boolean callPvpPreStartEvent(Player defender, Player attacker, PvPStatus pvpStatus) {
        PvpPreStartEvent pvpPreStartEvent = new PvpPreStartEvent(defender, attacker, settings.getPvpTime(), pvpStatus);
        Bukkit.getPluginManager().callEvent(pvpPreStartEvent);
        if (pvpPreStartEvent.isCancelled()) {
            return false;
        }
        return true;
    }

    private void updateAttackerAndCallEvent(Player attacker, Player defender, boolean bypassed) {
        int oldTime = bypassed ? getTimeRemainingInPvPSilent(attacker) : getTimeRemainingInPvP(attacker);
        updatePvpMode(attacker, bypassed, settings.getPvpTime());
        PvpTimeUpdateEvent pvpTimeUpdateEvent = new PvpTimeUpdateEvent(attacker, oldTime, settings.getPvpTime());
        pvpTimeUpdateEvent.setDamagedPlayer(defender);
        Bukkit.getPluginManager().callEvent(pvpTimeUpdateEvent);
    }

    private void updateDefenderAndCallEvent(Player defender, Player attackedBy, boolean bypassed) {
        int oldTime = bypassed ? getTimeRemainingInPvPSilent(defender) : getTimeRemainingInPvP(defender);
        updatePvpMode(defender, bypassed, settings.getPvpTime());
        PvpTimeUpdateEvent pvpTimeUpdateEvent = new PvpTimeUpdateEvent(defender, oldTime, settings.getPvpTime());
        pvpTimeUpdateEvent.setDamagedBy(attackedBy);
        Bukkit.getPluginManager().callEvent(pvpTimeUpdateEvent);
    }

    private void callUpdateEvent(Player player, int oldTime, int newTime) {
        PvpTimeUpdateEvent pvpTimeUpdateEvent = new PvpTimeUpdateEvent(player, oldTime, newTime);
        Bukkit.getPluginManager().callEvent(pvpTimeUpdateEvent);
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
        silentPvpMap.remove(player);
        Bukkit.getPluginManager().callEvent(new PvpStoppedEvent(player));
    }

    public boolean isCommandWhiteListed(String command) {
        if (whiteListedCommands.isEmpty()) {
            return false; //all commands are blocked
        }
        return whiteListedCommands.contains(command.toLowerCase());
    }

    public PowerUpsManager getPowerUpsManager() {
        return powerUpsManager;
    }

    public BossbarManager getBossbarManager() {
        return bossbarManager;
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

    public boolean isBypassed(Player player) {
        return isHasBypassPermission(player) || isInIgnoredWorld(player);
    }

    public boolean isHasBypassPermission(Player player) {
        return player.hasPermission("antirelog.bypass");
    }

    public boolean isInIgnoredWorld(Player player) {
        return settings.getDisabledWorlds().contains(player.getWorld().getName().toLowerCase());
    }

    public boolean isInIgnoredRegion(Player player) {
        if (!plugin.isWorldguardEnabled() || settings.getIgnoredWgRegions().isEmpty()) {
            return false;
        }

        Set<String> regions = settings.getIgnoredWgRegions();
        Set<IWrappedRegion> wrappedRegions = WorldGuardWrapper.getInstance().getRegions(player.getLocation());
        if (wrappedRegions.isEmpty()) {
            return false;
        }
        for (IWrappedRegion region : wrappedRegions) {
            if (regions.contains(region.getId().toLowerCase())) {
                return true;
            }
        }
        return false;


    }
}
