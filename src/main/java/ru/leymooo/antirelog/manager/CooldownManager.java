package ru.leymooo.antirelog.manager;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.entity.Player;

public class CooldownManager {

    private final Table<Player, CooldownType, Long> cooldowns = HashBasedTable.create();

    public void addCooldown(Player player, CooldownType type) {
        cooldowns.put(player, type, System.currentTimeMillis());
    }

    public boolean hasCooldown(Player player, CooldownType type, long duration) {
        Long added = cooldowns.get(player, type);
        if (added == null) {
            return false;
        }
        return (System.currentTimeMillis() - added) < duration;
    }

    public long getRemaining(Player player, CooldownType type, long duration) {
        Long added = cooldowns.get(player, type);
        return duration - (System.currentTimeMillis() - added);
    }

    public void remove(Player player) {
        cooldowns.row(player).clear();
    }

    public void clearAll() {
        cooldowns.clear();
    }

    public enum CooldownType {
        GOLDEN_APPLE,
        ENC_GOLDEN_APPLE,
        ENDER_PEARL;
    }
}
