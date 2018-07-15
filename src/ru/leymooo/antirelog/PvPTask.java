package ru.leymooo.antirelog;

import java.util.Set;

import org.bukkit.entity.Player;

import ru.leymooo.antirelog.utils.ActionBarUtils;
import ru.leymooo.antirelog.utils.BossBarUtils;
import ru.leymooo.antirelog.utils.TitlesUtils;
import ru.leymooo.antirelog.utils.Utils;
import ru.leymooo.config.Settings;

public class PvPTask implements Runnable {

    private final PlayerStorage playerStorage;

    public PvPTask(PlayerStorage playerStorage) {
        this.playerStorage = playerStorage;
    }

    @Override
    public void run() {
        Set<Player> playerInPvP = playerStorage.getPlayersInPvp();
        try {
            for (Player p : playerInPvP) {
                int time = playerStorage.getPlayerPvPTime(p);
                BossBarUtils.setNewBossBar(p, time);
                if (time <= 0) {
                    playerStorage.removePlayerPvP(p);
                    Utils.sendMessage(Settings.IMP.MESSAGES.PVP_STOPPED, p);
                    ActionBarUtils.sendAction(p, Settings.IMP.MESSAGES.PVP_STOPPED_ACTIONBAR);
                    TitlesUtils.sendTitles(p, true);
                    continue;
                }
                playerStorage.updatePlayerPvP(p);
                ActionBarUtils.sendAction(p, Settings.IMP.MESSAGES.PVP_ACTIONBAR.replace("%time%", Integer.toString(time)));
            }
        } finally {
            playerInPvP.clear();
            playerInPvP = null;
        }

    }

}
