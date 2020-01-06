package ru.leymooo.antirelog.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.codemc.worldguardwrapper.event.WrappedDisallowedPVPEvent;
import ru.leymooo.antirelog.config.Settings;
import ru.leymooo.antirelog.manager.PvPManager;

public class WorldGuardListener implements Listener {

    private final Settings settings;
    private final PvPManager pvpManager;

    public WorldGuardListener(Settings settings, PvPManager pvpManager) {
        this.settings = settings;
        this.pvpManager = pvpManager;
    }

    @EventHandler
    public void onPvP(WrappedDisallowedPVPEvent event) {
        if (!pvpManager.isPvPModeEnabled() || !settings.isIgnoreWorldGuard()) {
            return;
        }

        Player attacker = event.getAttacker();
        Player defender = event.getDefender();

        if (pvpManager.isInPvP(attacker) && pvpManager.isInPvP(defender)) {
            event.setCancelled(true);
            event.setResult(Result.DENY); //Deny means cancelled means pvp allowed
        } else if (settings.isJoinPvPInWorldGuard() && pvpManager.isInPvP(defender)) {
            event.setCancelled(true);
            event.setResult(Result.DENY); //Deny means cancelled means pvp allowed
        }
    }
}
