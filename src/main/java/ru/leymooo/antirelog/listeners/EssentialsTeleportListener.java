package ru.leymooo.antirelog.listeners;

import net.ess3.api.events.teleport.PreTeleportEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.leymooo.antirelog.config.Settings;
import ru.leymooo.antirelog.manager.PvPManager;

public class EssentialsTeleportListener implements Listener {

    private final PvPManager pvpManager;
    private final Settings settings;

    public EssentialsTeleportListener(PvPManager pvpManager, Settings settings) {
        this.pvpManager = pvpManager;
        this.settings = settings;
    }

    @EventHandler
    public void onPreTeleport(PreTeleportEvent event) {
        if (settings.isDisableTeleportsInPvp() && pvpManager.isInPvP(event.getTeleportee().getBase())) {
            event.setCancelled(true);
        }
    }
}
