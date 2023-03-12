package ru.leymooo.antirelog.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ActionBar {

    private static Class<?> craftPlayerClass;
    private static Class<?> chatComponentTextClass;
    private static Class<?> iChatBaseComponentClass;
    private static Class<?> packetPlayOutChat;
    private static Class<?> packetClass;

    public static void sendAction(Player player, String message) {

        if (VersionUtils.isVersion(9)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        } else {
        	try {
            if (craftPlayerClass == null) {
                String nmsver = "v1_8_R3";
                craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
                chatComponentTextClass = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
                iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
                packetPlayOutChat = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
                packetClass = Class.forName("net.minecraft.server." + nmsver + ".Packet");
            }

            Object craftPlayer = craftPlayerClass.cast(player);
            Object nmsMessage = chatComponentTextClass.getConstructor(String.class).newInstance(message);
            Object playOutChat = packetPlayOutChat.getConstructor(iChatBaseComponentClass, byte.class).newInstance(nmsMessage,
                    (byte) 2);
            Method getNmsPlayerMethod = craftPlayerClass.getDeclaredMethod("getHandle");
            Object nmsPlayer = getNmsPlayerMethod.invoke(craftPlayer);
            Field playerConnectionField = nmsPlayer.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(nmsPlayer);
            Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass);
            sendPacketMethod.invoke(playerConnection, playOutChat);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
      }
    }
}
