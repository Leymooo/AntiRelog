package ru.leymooo.antirelog.util;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.util.logging.Level;

public class CommandMapUtils {

    private static CommandMap commandMap;
    private static Boolean tried = null;


    public static CommandMap getCommandMap() {
        if (commandMap == null && tried == null) {
            tried = true;
            try {
                Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                field.setAccessible(true);
                commandMap = (CommandMap) field.get(Bukkit.getServer());
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "[AntiRelog] Could not init command map", e);
            }
        }
        return commandMap;
    }

    public static Command getCommand(String command) {
        CommandMap map = getCommandMap();
        return map == null ? null : getCommandMap().getCommand(command);
    }

}
