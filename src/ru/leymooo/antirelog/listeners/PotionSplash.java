package ru.leymooo.antirelog.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ru.leymooo.antirelog.utils.PvPUtils;
import ru.leymooo.antirelog.utils.Utils;
import ru.leymooo.config.Settings;

public class PotionSplash implements Listener {

    private final PvPUtils pvpUtils;

    public PotionSplash(PvPUtils pvpUtils) {
        this.pvpUtils = pvpUtils;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent e) {
        if (e.getPotion() != null && e.getPotion().getShooter() instanceof Player) {
            Player shooter = (Player) e.getPotion().getShooter();
            if (Settings.IMP.DISABLED_WORLDS.contains(shooter.getWorld().getName().toLowerCase()))
                return;
            for (LivingEntity en : e.getAffectedEntities()) {
                if (en.getType() == EntityType.PLAYER && en != shooter) {
                    if (Utils.checkPlayer(shooter)) {
                        e.setCancelled(true);
                        break;
                    } else {
                        for (PotionEffect ef : e.getPotion().getEffects()) {
                            if (ef.getType().equals(PotionEffectType.POISON)) {
                                pvpUtils.startPvp((Player) en, shooter);
                            }
                        }
                    }
                }
            }
        }
    }
}
