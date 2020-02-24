package ru.leymooo.antirelog.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.leymooo.antirelog.event.PvpPreStartEvent.PvPStatus;

public class PvpStartedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player defender;
    private final Player attacker;
    private final int pvpTime;
    private final PvPStatus pvpStatus;

    public PvpStartedEvent(Player defender, Player attacker, int pvpTime, PvPStatus pvpStatus) {
        this.defender = defender;
        this.attacker = attacker;
        this.pvpTime = pvpTime;
        this.pvpStatus = pvpStatus;
    }

    public Player getDefender() {
        return defender;
    }

    public Player getAttacker() {
        return attacker;
    }

    public int getPvpTime() {
        return pvpTime;
    }

    public PvPStatus getPvpStatus() {
        return pvpStatus;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
