package ru.leymooo.antirelog.manager;

import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.kitteh.vanish.VanishPlugin;
import ru.leymooo.antirelog.Antirelog;

public class VanishManager {

    private final Antirelog plugin;

    private final boolean vanishAPI;
    private final VanishPlugin vanishNoPacket;

    public VanishManager(Antirelog plugin) {
        this.plugin = plugin;
        PluginManager pluginManager = Bukkit.getPluginManager();
        vanishAPI = pluginManager.isPluginEnabled("SuperVanish") || pluginManager.isPluginEnabled("PremiumVanish");
        if (pluginManager.isPluginEnabled("VanishNoPacket")) {
            this.vanishNoPacket = (VanishPlugin) pluginManager.getPlugin("VanishNoPacket");
        } else {
            vanishNoPacket = null;
        }
    }

    public void disableVanish(Player player) {
        if (vanishAPI && VanishAPI.isInvisible(player)) {
            VanishAPI.showPlayer(player);
        }
        if (vanishNoPacket != null && vanishNoPacket.getManager().isVanished(player)) {
            vanishNoPacket.getManager().toggleVanishQuiet(player, false);
        }
    }
}
