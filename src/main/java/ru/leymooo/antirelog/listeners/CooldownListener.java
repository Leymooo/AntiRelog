package ru.leymooo.antirelog.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import ru.leymooo.antirelog.config.Settings;
import ru.leymooo.antirelog.manager.CooldownManager;
import ru.leymooo.antirelog.manager.CooldownManager.CooldownType;
import ru.leymooo.antirelog.manager.PvPManager;
import ru.leymooo.antirelog.util.Utils;
import ru.leymooo.antirelog.util.VersionUtils;

import java.util.concurrent.TimeUnit;

public class CooldownListener implements Listener {

    private final CooldownManager cooldownManager;
    private final PvPManager pvpManager;
    private final Settings settings;

    public CooldownListener(CooldownManager cooldownManager, PvPManager pvpManager, Settings settings) {
        this.cooldownManager = cooldownManager;
        this.pvpManager = pvpManager;
        this.settings = settings;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAppleEat(PlayerItemConsumeEvent event) {
        ItemStack itemStack = event.getItem();
        boolean is113 = VersionUtils.isVersion(13);
        if (itemStack.getType() == Material.GOLDEN_APPLE || (is113 && itemStack.getType() == Material.ENCHANTED_GOLDEN_APPLE)) {
            boolean isEnchantedApple = (is113 && itemStack.getType() == Material.ENCHANTED_GOLDEN_APPLE) || itemStack.getDurability() >= 1;
            long cooldownTime = (isEnchantedApple ? settings.getEnchantedGoldenAppleCooldown() : settings.getGoldenAppleCooldown()) * 1000;
            if (!pvpManager.canStartPvP(event.getPlayer()) || cooldownTime <= 0) {
                return;
            }
            CooldownType cooldownType = isEnchantedApple ? CooldownType.ENC_GOLDEN_APPLE : CooldownType.GOLDEN_APPLE;
            boolean isActive = !pvpManager.isPvPModeEnabled() || pvpManager.isInPvP(event.getPlayer());
            if (isActive && cooldownManager.hasCooldown(event.getPlayer(), cooldownType, cooldownTime)) {
                event.setCancelled(true);
                long remaining = cooldownManager.getRemaining(event.getPlayer(), cooldownType, cooldownTime);
                int remainingInt = (int) TimeUnit.MILLISECONDS.toSeconds(remaining);
                String message = settings.getMessages().getAppleDisabled();
                if (!message.isEmpty()) {
                    event.getPlayer().sendMessage(Utils.color(Utils.replaceTime(message.replace("%time%",
                            Math.round(remaining / 1000) + ""), remainingInt)));
                }
                return;
            }
            cooldownManager.addCooldown(event.getPlayer(), cooldownType);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPerlLaunch(ProjectileLaunchEvent e) {
        if (settings.getEnderPearlCooldown() > 0 && e.getEntityType() == EntityType.ENDER_PEARL && e.getEntity().getShooter() instanceof Player) {
            Player p = (Player) e.getEntity().getShooter();
            if (pvpManager.canStartPvP(p)) {
                cooldownManager.addCooldown(p, CooldownType.ENDER_PEARD);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent e) {
        if (settings.getEnderPearlCooldown() <= 0 || !e.hasItem() || e.getItem().getType() != Material.ENDER_PEARL || !pvpManager.canStartPvP(e.getPlayer())) {
            return;
        }

        if (cooldownManager.hasCooldown(e.getPlayer(), CooldownType.ENDER_PEARD, settings.getEnderPearlCooldown() * 1000)) {
            boolean isActive = !pvpManager.isPvPModeEnabled() || pvpManager.isInPvP(e.getPlayer());
            if (isActive && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                long remaining = cooldownManager.getRemaining(e.getPlayer(), CooldownType.ENDER_PEARD,
                        settings.getEnderPearlCooldown() * 1000);
                int remainingInt = (int) TimeUnit.MILLISECONDS.toSeconds(remaining);
                e.setCancelled(true);
                String message = settings.getMessages().getEnderPearlDisabled();
                if (!message.isEmpty()) {
                    e.getPlayer().sendMessage(Utils.color(Utils.replaceTime(message.replace("%time%",
                            Math.round(remaining / 1000) + ""), remainingInt)));
                }
            }
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cooldownManager.remove(event.getPlayer());
    }
}
