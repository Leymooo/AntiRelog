package ru.leymooo.antirelog.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.leymooo.antirelog.config.Messages;
import ru.leymooo.antirelog.config.Settings;
import ru.leymooo.antirelog.manager.PvPManager;
import ru.leymooo.antirelog.util.Utils;
import ru.leymooo.antirelog.util.VersionUtils;

public class PvPListener implements Listener {

    private final PvPManager pvpManager;
    private final Messages messages;
    private final Settings settings;

    public PvPListener(PvPManager pvpManager, Settings settings) {
        this.pvpManager = pvpManager;
        this.settings = settings;
        this.messages = settings.getMessages();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }
        Player target = (Player) event.getEntity();
        Player damager = getDamager(event.getDamager());
        pvpManager.playerDamagedByPlayer(damager, target);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCombust(EntityCombustByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player target = (Player) event.getEntity();
        Player damager = getDamager(event.getCombuster());
        pvpManager.playerDamagedByPlayer(damager, target);
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent e) {
        if (e.getPotion() != null && e.getPotion().getShooter() instanceof Player) {
            Player shooter = (Player) e.getPotion().getShooter();
            for (LivingEntity en : e.getAffectedEntities()) {
                if (en.getType() == EntityType.PLAYER && en != shooter) {
                    for (PotionEffect ef : e.getPotion().getEffects()) {
                        if (ef.getType().equals(PotionEffectType.POISON)) {
                            pvpManager.playerDamagedByPlayer(shooter, (Player) en);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent ev) {
        if (settings.isDisableTeleportsInPvp() && ev.getCause() != TeleportCause.ENDER_PEARL && pvpManager.isInPvP(ev.getPlayer())) {
            ev.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (pvpManager.isInPvP(e.getPlayer())) {
            e.setCancelled(true);
            String message = Utils.color(messages.getCommandsDisabled());
            if (!message.isEmpty()) {
                e.getPlayer().sendMessage(Utils.replaceTime(message, pvpManager.getTimeRemainingInPvP(e.getPlayer())));
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onKick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        if (!pvpManager.isInPvP(player)) {
            return;
        }

        pvpManager.pvpStoppedSilent(player);

        if (settings.getKickMessages().isEmpty()) {
            kickedInPvp(player);
            return;
        }
        if (e.getReason() == null) {
            return;
        }
        String reason = ChatColor.stripColor(e.getReason());
        for (String killReason : settings.getKickMessages()) {
            if (reason.contains(killReason)) {
                kickedInPvp(player);
                return;
            }
        }
    }

    private void kickedInPvp(Player player) {
        if (settings.isKillOnKick()) {
            player.setHealth(0);
            sendLeavedInPvpMessage(player);
        }
        if (settings.isRunCommandsOnKick()) {
            runCommands(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        if (settings.isHideLeaveMessage()) {
            e.setQuitMessage(null);
        }
        if (pvpManager.isInPvP(e.getPlayer())) {
            pvpManager.pvpStoppedSilent(e.getPlayer());
            if (settings.isKillOnLeave()) {
                sendLeavedInPvpMessage(e.getPlayer());
                e.getPlayer().setHealth(0);
            } else {
                pvpManager.pvpStoppedSilent(e.getPlayer());
            }
            runCommands(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent e) {
        if (settings.isHideDeathMessage()) {
            e.setDeathMessage(null);
        }
        pvpManager.pvpStoppedSilent(e.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        if (settings.isHideJoinMessage()) {
            e.setJoinMessage(null);
        }
    }

    private void sendLeavedInPvpMessage(Player p) {
        String message = Utils.color(messages.getPvpLeaved()).replace("%player%", p.getName());
        if (!message.isEmpty()) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.sendMessage(message);
            }
        }
    }

    private void runCommands(Player leaved) {
        if (!settings.getCommandsOnLeave().isEmpty()) {
            settings.getCommandsOnLeave().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    Utils.color(command).replace("%player%", leaved.getName())));
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
        } else if (damager instanceof TNTPrimed) {
            TNTPrimed tntPrimed = (TNTPrimed) damager;
            return getDamager(tntPrimed.getSource());
        } else if (VersionUtils.isVersion(9) && damager instanceof AreaEffectCloud) {
            AreaEffectCloud aec = (AreaEffectCloud) damager;
            if (aec.getSource() instanceof Player) {
                return (Player) aec.getSource();
            }
        }
        return null;
    }
}
