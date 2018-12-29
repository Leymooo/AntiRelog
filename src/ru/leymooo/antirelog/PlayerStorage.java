package ru.leymooo.antirelog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import ru.leymooo.config.Settings;

public class PlayerStorage {

    private Map<Player, Integer> playersInPvp = new HashMap<Player, Integer>();

    public void addPlayerPvP(Player p) {
        playersInPvp.put(p, Settings.IMP.PVP_TIME);
    }

    public void updatePlayerPvP(Player p) {
        Integer time = playersInPvp.getOrDefault(p, 0);
        playersInPvp.replace(p, time - 1);
    }

    public void removePlayerPvP(Player p) {
        playersInPvp.remove(p);
    }

    public boolean isInPvP(Player p) {
        return playersInPvp.containsKey(p);
    }

    public int getPlayerPvPTime(Player p) {
        return playersInPvp.getOrDefault(p, 0);
    }

    public Set<Player> getPlayersInPvp() {
        return new HashSet<>(playersInPvp.keySet());
    }

    public void clear() {
        playersInPvp.clear();
    }
}
