package ru.leymooo.antirelog.utils;

import org.bukkit.entity.Player;

import ru.leymooo.antirelog.PlayerStorage;
import ru.leymooo.config.Settings;

public class PvPUtils {

    private final PlayerStorage playerStorage;

    public PvPUtils(PlayerStorage playerStorage) {
        this.playerStorage = playerStorage;
    }

    public PlayerStorage getPlayerStorage() {
        return this.playerStorage;
    }

    private void startPvp(Player p) {
        if (p.hasPermission("antirelog.bypass"))
            return;
        if (!playerStorage.isInPvP(p)) {
            TitlesUtils.sendTitles(p, false);
            Utils.sendMessage(Settings.IMP.MESSAGES.PVP_STARTED, p);
        }
        BossBarUtils.setNewBossBar(p, Settings.IMP.PVP_TIME);
        playerStorage.addPlayerPvP(p);
        ActionBarUtils.sendAction(p, Settings.IMP.MESSAGES.PVP_ACTIONBAR.replace("%time%", Integer.toString(Settings.IMP.PVP_TIME)));

    }

    public void startPvp(Player player, Player damager) {
        startPvp(player);
        startPvp(damager);
    }

    public boolean checkPlayerOrStartPvp(Player player, Player damager) {
        if (Utils.checkPlayer(damager)) {
            return true;
        }
        if (Settings.IMP.PVP_TIME != -1) {
            startPvp(player);
            startPvp(damager);
        }
        return false;
    }

}
