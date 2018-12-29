package ru.leymooo.antirelog.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import ru.leymooo.config.Settings;

public class ActionBarUtils {

    private static Class<?> craftPlayerClass;
    private static Class<?> chatComponentTextClass;
    private static Class<?> iChatBaseComponentClass;
    private static Class<?> packetPlayOutChat;
    private static Class<?> packetClass;
    private static Class<?> chatMessageTypeClass;
    private static Object   chatMessageType;
    private static String   nmsver;

    private static boolean  is112;

    public static void init(String ver) {
        is112 = ver.startsWith("v1_12_R") || ver.startsWith("v1_13_R") || ver.startsWith("v1_14_R");
        nmsver = ver;
        try {
            craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
            chatComponentTextClass = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
            iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
            packetPlayOutChat = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
            packetClass = Class.forName("net.minecraft.server." + nmsver + ".Packet");
            if (is112) {
                chatMessageTypeClass = Class.forName("net.minecraft.server." + nmsver + ".ChatMessageType");
                for (Object obj : chatMessageTypeClass.getEnumConstants()) {
                    if (obj.toString().equals("GAME_INFO")) {
                        chatMessageType = obj;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void sendAction(Player player, String message) {
        if (!player.isOnline() || !Settings.IMP.ACTIONBAR_ENABLED) {
            return;
        }
        message = Utils.translate(message);
        try {
            Object craftPlayer = craftPlayerClass.cast(player);
            Object nmsMessage = chatComponentTextClass.getConstructor(String.class).newInstance(message);
            Object playOutChat = !is112 ? packetPlayOutChat.getConstructor(iChatBaseComponentClass, byte.class).newInstance(nmsMessage, (byte) 2)
                        : packetPlayOutChat.getConstructor(new Class<?>[] { iChatBaseComponentClass, chatMessageTypeClass })
                                    .newInstance(nmsMessage, chatMessageType);
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
