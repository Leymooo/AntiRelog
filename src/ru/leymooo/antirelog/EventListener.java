package ru.leymooo.antirelog;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import ru.leymooo.config.Settings;

public class EventListener implements Listener {

    private Main main;
    private ConcurrentHashMap<Player, Integer> playersInPvp = new ConcurrentHashMap<Player, Integer>();
    private HashMap<Player, Long> appleCooldown = new HashMap<Player, Long>();

    public EventListener(Main main) {
        this.main = main;
        startScheduler();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        Player player = (Player) e.getEntity();
        if (Settings.IMP.DISABLED_WORLDS.contains(player.getWorld().getName().toLowerCase()))
            return;
        if (e.getDamager() instanceof Player && e.getDamager() != player && checkPlayerAndStartPvp(player, (Player) e.getDamager())) {
            e.setCancelled(true);
        } else if (e.getDamager() instanceof Projectile) {
            Projectile proj = (Projectile) e.getDamager();
            if (proj.getShooter() instanceof Player && proj.getShooter() != player && checkPlayerAndStartPvp(player, (Player) proj.getShooter())) {
                e.setCancelled(true);
            }
        } else if (!Main.is188 && e.getDamager() instanceof AreaEffectCloud) {
            AreaEffectCloud aec = (AreaEffectCloud) e.getDamager();
            if (aec.getSource() instanceof Player && aec.getSource() != player && checkPlayerAndStartPvp(player, (Player) aec.getSource())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCombust(EntityCombustByEntityEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        Player player = (Player) e.getEntity();
        if (Settings.IMP.DISABLED_WORLDS.contains(player.getWorld().getName().toLowerCase()))
            return;
        Entity damager = e.getCombuster();
        if (damager instanceof Player && damager != player && checkPlayerAndStartPvp(player, (Player) damager)) {
            e.setCancelled(true);
        } else if (damager instanceof Projectile) {
            ProjectileSource damager1 = ((Projectile) damager).getShooter();
            if (damager1 instanceof Player && damager1 != player && checkPlayerAndStartPvp(player, (Player) damager1)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent e) {
        if (e.getPotion() != null && e.getPotion().getShooter() instanceof Player) {
            Player shooter = (Player) e.getPotion().getShooter();
            if (Settings.IMP.DISABLED_WORLDS.contains(shooter.getWorld().getName().toLowerCase()))
                return;
            for (LivingEntity en : e.getAffectedEntities()) {
                if (en.getType() == EntityType.PLAYER && en != shooter) {
                    if (main.utils.checkPlayer(shooter)) {
                        e.setCancelled(true);
                        break;
                    } else {
                        for (PotionEffect ef : e.getPotion().getEffects()) {
                            if (ef.getType().equals(PotionEffectType.POISON)) {
                                startPvp((Player) en);
                                startPvp(shooter);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAppleEat(PlayerItemConsumeEvent e) {
        if (Settings.IMP.GOLDEN_APPLE_COOLDOWN == -1 || e.getItem() == null || e.getItem().getType() != Material.GOLDEN_APPLE || e.getItem().getDurability() != 1) {
            return;
        }
        if (Settings.IMP.DISABLED_WORLDS.contains(e.getPlayer().getWorld().getName().toLowerCase()) || e.getPlayer().hasPermission("antirelog.bypass")) {
            return;
        }
        if (!appleCooldown.containsKey(e.getPlayer())) {
            appleCooldown.put(e.getPlayer(), System.currentTimeMillis());
            return;
        }
        long cd = System.currentTimeMillis() - appleCooldown.get(e.getPlayer());
        if (cd <= (Settings.IMP.GOLDEN_APPLE_COOLDOWN * 1000)) {
            e.setCancelled(true);
            main.utils.sendMessage(Settings.IMP.MESSAGES.APPLE_DISABLED.replace("%time%", Math.round(Settings.IMP.GOLDEN_APPLE_COOLDOWN - (cd / 1000)) + ""), e.getPlayer());
        } else {
            appleCooldown.put(e.getPlayer(), System.currentTimeMillis());
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (playersInPvp.containsKey(e.getPlayer())) {
            e.setCancelled(true);
            main.utils.sendMessage(Settings.IMP.MESSAGES.CHAT_DISABLED, e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(PlayerKickEvent e) {
        playersInPvp.remove(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        if (Settings.IMP.KILL_ON_LEAVE && playersInPvp.containsKey(e.getPlayer())) {
            e.getPlayer().setHealth(0);
            String message = main.utils.translate(Settings.IMP.MESSAGES.PVP_LEAVED).replace("%player%", e.getPlayer().getName());
            if (!message.trim().isEmpty() && Settings.IMP.SHOW_LEAVED_MESSAGE) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(message);
                }
            }
            if (!Settings.IMP.MESSAGES.COMMAND_ON_LEAVE.trim().isEmpty()) {
                String command = ChatColor.translateAlternateColorCodes('&', Settings.IMP.MESSAGES.COMMAND_ON_LEAVE).replace("%player%", e.getPlayer().getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
        if (Settings.IMP.REMOVE_LEAVE_MESSAGE) {
            e.setQuitMessage(null);
        }
        appleCooldown.remove(e.getPlayer());
        playersInPvp.remove(e.getPlayer());
        main.utils.setNewBossBar(e.getPlayer(), 0);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent e) {
        if (Settings.IMP.REMOVE_DEATH_MESSAGE) {
            e.setDeathMessage(null);
        }
        playersInPvp.remove((Player) e.getEntity());
        main.utils.setNewBossBar((Player) e.getEntity(), 0);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        if (Settings.IMP.REMOVE_JOIN_MESSAGE) {
            e.setJoinMessage(null);
        }
    }

    private void startPvp(Player p) {
        if (p.hasPermission("antirelog.bypass"))
            return;
        if (!playersInPvp.containsKey(p)) {
            main.utils.sendTitles(p, false);
            main.utils.sendMessage(Settings.IMP.MESSAGES.PVP_STARTED, p);
        }
        main.utils.setNewBossBar(p, Settings.IMP.PVP_TIME);
        playersInPvp.put(p, Settings.IMP.PVP_TIME);
        main.utils.sendAction(Settings.IMP.MESSAGES.PVP_ACTIONBAR.replace("%time%", Integer.toString(Settings.IMP.PVP_TIME)), p);

    }

    private boolean checkPlayerAndStartPvp(Player player, Player damager) {
        if (main.utils.checkPlayer(damager)) {
            return true;
        }
        if (Settings.IMP.PVP_TIME != -1) {
            startPvp(player);
            startPvp(damager);
        }
        return false;
    }

    private void startScheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
            @Override
            public void run() {
                for (Player p : playersInPvp.keySet()) {
                    if (p.isOnline()) {
                        int i = playersInPvp.get(p);
                        if (i == 0) {
                            main.utils.sendMessage(Settings.IMP.MESSAGES.PVP_STOPPED, p);
                            main.utils.sendAction(Settings.IMP.MESSAGES.PVP_STOPPED_ACTIONBAR, p);
                            main.utils.setNewBossBar(p, 0);
                            playersInPvp.remove(p);
                            main.utils.sendTitles(p, true);
                            continue;
                        }
                        main.utils.setNewBossBar(p, i);
                        main.utils.sendAction(Settings.IMP.MESSAGES.PVP_ACTIONBAR.replace("%time%", Integer.toString(i)), p);
                        playersInPvp.replace(p, i - 1);
                    } else
                        playersInPvp.remove(p);
                }
            }
        }, 20, 20);
    }

}
