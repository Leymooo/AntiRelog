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

        boolean attackerInPvp = pvpManager.isInPvP(attacker) || pvpManager.isInSilentPvP(attacker);
        boolean defenderInPvp = pvpManager.isInPvP(defender) || pvpManager.isInSilentPvP(defender);

        if (attackerInPvp && defenderInPvp) {
            event.setCancelled(true);
            event.setResult(Result.DENY); //Deny means cancelled means pvp allowed
        } else if (settings.isJoinPvPInWorldGuard() && defenderInPvp) {
            event.setCancelled(true);
            event.setResult(Result.DENY); //Deny means cancelled means pvp allowed
        }
    }
}
