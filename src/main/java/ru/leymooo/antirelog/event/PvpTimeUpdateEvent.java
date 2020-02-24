package ru.leymooo.antirelog.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class PvpTimeUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final int oldTime, newTime;

    private Player damagedPlayer;
    private Player damagedBy;

    public PvpTimeUpdateEvent(Player player, int oldTime, int newTime) {
        this.player = player;
        this.oldTime = oldTime;
        this.newTime = newTime;
    }

    public Player getPlayer() {
        return player;
    }

    public int getOldTime() {
        return oldTime;
    }

    public int getNewTime() {
        return newTime;
    }

    public Player getDamagedPlayer() {
        return damagedPlayer;
    }

    public void setDamagedPlayer(Player damagedPlayer) {
        this.damagedPlayer = damagedPlayer;
    }

    public Player getDamagedBy() {
        return damagedBy;
    }

    public void setDamagedBy(Player damagedBy) {
        this.damagedBy = damagedBy;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
