package ru.leymooo.antirelog.listeners;

import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import ru.leymooo.antirelog.utils.PvPUtils;
import ru.leymooo.config.Settings;

public class EntityEvents implements Listener {

    private final PvPUtils pvpUtils;
    private final boolean  is188;

    public EntityEvents(PvPUtils pvpUtils, boolean is188) {
        this.pvpUtils = pvpUtils;
        this.is188 = is188;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        Player player = (Player) e.getEntity();
        if (Settings.IMP.DISABLED_WORLDS.contains(player.getWorld().getName().toLowerCase()))
            return;
        Player damager = getDamager(e.getDamager());
        if (damager != null && damager != player && pvpUtils.checkPlayerOrStartPvp(player, damager)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCombust(EntityCombustByEntityEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        Player player = (Player) e.getEntity();
        if (Settings.IMP.DISABLED_WORLDS.contains(player.getWorld().getName().toLowerCase()))
            return;
        Player damager = getDamager(e.getCombuster());
        if (damager != null && damager != player && pvpUtils.checkPlayerOrStartPvp(player, damager)) {
            e.setCancelled(true);
        }
    }

    private Player getDamager(Entity damager) {
        if (damager instanceof Player) {
            return (Player) damager;
        } else if (damager instanceof Projectile) {
            Projectile proj = (Projectile) damager;
            if (proj.getShooter() instanceof Player) {
                return (Player) proj.getShooter();
            }
        } else if (!is188 && damager instanceof AreaEffectCloud) {
            AreaEffectCloud aec = (AreaEffectCloud) damager;
            if (aec.getSource() instanceof Player) {
                return (Player) aec.getSource();
            }
        }
        return null;
    }
}
