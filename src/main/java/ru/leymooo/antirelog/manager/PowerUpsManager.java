package ru.leymooo.antirelog.manager;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import de.myzelyam.api.vanish.VanishAPI;
import me.libraryaddict.disguise.DisguiseAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.kitteh.vanish.VanishPlugin;
import ru.leymooo.antirelog.config.Settings;
import ru.leymooo.antirelog.util.Utils;

public class PowerUpsManager {

    private final Settings settings;

    private boolean vanishAPI, libsDisguises, cmi;
    private VanishPlugin vanishNoPacket;
    private Essentials essentials;

    public PowerUpsManager(Settings settings) {
        this.settings = settings;
        detectPlugins();
    }


    public boolean disablePowerUps(Player player) {
        if (player.hasPermission("antirelog.bypass.checks")) {
            return false;
        }

        boolean disabled = false;
        if (player.getGameMode() == GameMode.CREATIVE) {
            if (Bukkit.getDefaultGameMode() == GameMode.ADVENTURE) {
                player.setGameMode(GameMode.ADVENTURE);
            } else {
                player.setGameMode(GameMode.SURVIVAL);
            }
            disabled = true;
        }

        if (player.isFlying() || player.getAllowFlight()) {
            player.setFlying(false);
            player.setAllowFlight(false);
            disabled = true;
        }

        if (checkEssentials(player)) {
            disabled = true;
        }

        if (checkCMI(player)) {
            disabled = true;
        }

        if (vanishAPI && VanishAPI.isInvisible(player)) {
            VanishAPI.showPlayer(player);
            disabled = true;
        }
        if (vanishNoPacket != null && vanishNoPacket.getManager().isVanished(player)) {
            vanishNoPacket.getManager().toggleVanishQuiet(player, false);
            disabled = true;
        }
        if (libsDisguises && DisguiseAPI.isSelfDisguised(player)) {
            DisguiseAPI.undisguiseToAll(player);
        }
        return disabled;
    }


    public void disablePowerUpsWithRunCommands(Player player) {
        if (disablePowerUps(player) && !settings.getCommandsOnPowerupsDisable().isEmpty()) {
            settings.getCommandsOnPowerupsDisable().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    Utils.color(command.replace("%player%", player.getName()))));
            String message = settings.getMessages().getPvpStartedWithPowerups();
            if (!message.isEmpty()) {
                player.sendMessage(Utils.color(message));
            }
        }
    }

    public void detectPlugins() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        this.vanishAPI = pluginManager.isPluginEnabled("SuperVanish") || pluginManager.isPluginEnabled("PremiumVanish");
        this.vanishNoPacket = pluginManager.isPluginEnabled("VanishNoPacket") ? (VanishPlugin) pluginManager.getPlugin("VanishNoPacket")
                : null;
        this.essentials = pluginManager.isPluginEnabled("Essentials") ? (Essentials) pluginManager.getPlugin("Essentials") : null;
        this.libsDisguises = pluginManager.isPluginEnabled("LibsDisguises");
        this.cmi = pluginManager.isPluginEnabled("CMI");
    }


    private boolean checkEssentials(Player player) {
        boolean disabled = false;
        if (essentials != null) {
            User user = essentials.getUser(player);
            if (user.isVanished()) {
                user.setVanished(false);
                disabled = true;
            }
            if (user.isGodModeEnabled()) {
                user.setGodModeEnabled(false);
                disabled = true;
            }
        }
        return disabled;
    }

    private boolean checkCMI(Player player) {
        boolean disabled = false;
        if (cmi) {
            CMIUser user = CMI.getInstance().getPlayerManager().getUser(player);
            if (user != null) {
                if (user.isGod()) {
                    CMI.getInstance().getNMS().changeGodMode(player, false);
                    user.setTgod(0);
                    disabled = true;
                }
                if (user.isVanished()) {
                    user.setVanished(false);
                    disabled = true;
                }
            }
        }
        return disabled;
    }
}
