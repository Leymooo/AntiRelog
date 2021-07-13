package ru.leymooo.antirelog.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.MethodAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.leymooo.antirelog.config.Settings;
import ru.leymooo.antirelog.manager.CooldownManager;
import ru.leymooo.antirelog.manager.CooldownManager.CooldownType;
import ru.leymooo.antirelog.manager.PvPManager;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class ProtocolLibUtils {

    private static boolean hasProtocolLib;
    private static Class<?> ITEM_CLASS;
    private static MethodAccessor getItem = null;
    private static MethodAccessor getMaterial = null;

    static {
        hasProtocolLib = Bukkit.getPluginManager().isPluginEnabled("ProtocolLib") && VersionUtils.isVersion(9);
        if (hasProtocolLib) {
            boolean is117 = VersionUtils.isVersion(17);
            ITEM_CLASS = MinecraftReflection.getMinecraftClass(is117 ? "world.item.Item" :"Item");
            getItem = Accessors.getMethodAccessor(MinecraftReflection
                            .getCraftBukkitClass("util.CraftMagicNumbers"),
                    "getItem", Material.class);
            getMaterial = Accessors.getMethodAccessor(MinecraftReflection
                            .getCraftBukkitClass("util.CraftMagicNumbers"),
                    "getMaterial", ITEM_CLASS);
        }

    }

    public static PacketContainer createCooldownPacket(Material material, int ticks) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.SET_COOLDOWN);
        packetContainer.getModifier().writeDefaults();
        packetContainer.getModifier().withType(ITEM_CLASS).write(0, getItem.invoke(null, material));
        packetContainer.getIntegers().write(0, ticks);
        return packetContainer;

    }

    public static void sendPacket(PacketContainer packetContainer, Player player) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void createListener(CooldownManager cooldownManager, PvPManager pvPManager, Plugin plugin) {

        Settings settings = cooldownManager.getSettings();
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.LOWEST, PacketType.Play.Server.SET_COOLDOWN) {
            List<CooldownType> types = Arrays.asList(CooldownType.CHORUS, CooldownType.ENDER_PEARL);

            @Override
            public void onPacketSending(PacketEvent event) {
                Material material = (Material) getMaterial.invoke(null, event.getPacket().getModifier().withType(ITEM_CLASS).read(0));
                int duration = event.getPacket().getIntegers().read(0);
                duration = duration * 50;

                for (CooldownType cooldownType : types) {
                    if (material == cooldownType.getMaterial()) {
                        boolean hasCooldown = cooldownManager.hasCooldown(event.getPlayer(), cooldownType, cooldownType.getCooldown(settings) * 1000);
                        if (hasCooldown) {
                            long remaning = cooldownManager.getRemaining(event.getPlayer(), cooldownType, cooldownType.getCooldown(settings) * 1000);
                            if (Math.abs(remaning - duration) > 100) {
                                if (!pvPManager.isPvPModeEnabled() || pvPManager.isInPvP(event.getPlayer())) {
                                    if (duration == 0) {
                                        event.setCancelled(true);
                                        return;
                                    }
                                    event.getPacket().getIntegers().write(0, (int) Math.ceil(remaning / 50f));
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
