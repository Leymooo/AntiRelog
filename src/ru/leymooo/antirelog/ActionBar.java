package ru.leymooo.antirelog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

public class ActionBar {

    private static Class<?> craftPlayerClass;
    private static Class<?> chatComponentTextClass;
    private static Class<?> iChatBaseComponentClass;
    private static Class<?> packetPlayOutChat;
    private static Class<?> packetClass;
    private static Class<?> chatMessageTypeClass;
    private static Object chatMessageType;
    private static String nmsver;

    public static void init(String ver) {
        nmsver = ver;
        try {
            craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
            chatComponentTextClass = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
            iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
            packetPlayOutChat = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
            packetClass = Class.forName("net.minecraft.server." + nmsver + ".Packet");
            if (nmsver.startsWith("v1_12_R")) {
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
        if (!player.isOnline()) {
            return; 
        }
        try {
            Object craftPlayer = craftPlayerClass.cast(player);
            Object nmsMessage = chatComponentTextClass.getConstructor(String.class).newInstance(message);
            Object ppoc = !nmsver.startsWith("v1_12_R") ?packetPlayOutChat.getConstructor(iChatBaseComponentClass, byte.class).newInstance(nmsMessage, (byte) 2) :packetPlayOutChat.getConstructor(new Class<?>[]{iChatBaseComponentClass, chatMessageTypeClass}).newInstance(nmsMessage, chatMessageType);
            Method m1 = craftPlayerClass.getDeclaredMethod("getHandle");
            Object h = m1.invoke(craftPlayer);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket", packetClass);
            m5.invoke(pc, ppoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
