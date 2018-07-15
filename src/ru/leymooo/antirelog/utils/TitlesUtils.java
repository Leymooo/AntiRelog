package ru.leymooo.antirelog.utils;

import org.bukkit.entity.Player;

import ru.leymooo.config.Settings;


public class TitlesUtils {
    
    private static String[][] titles = new String[2][2];
    private static boolean is111;
    
    
    public static void init(boolean is111) {
        TitlesUtils.is111 = is111;
        createTitles();
    }
    
    public static void sendTitles(Player p, boolean end) {
        if (Settings.IMP.TITLES_ENABLED) {
            int index = end ? 1 : 0;
            if (is111) {
                p.sendTitle(titles[index][0], titles[index][1], 10, 20, 10);
            } else {
                p.sendTitle(titles[index][0], titles[index][1]);
            }
        }
    }

    private static void createTitles() {
        if (Settings.IMP.TITLES_ENABLED) {
            String[] startTitle = Settings.IMP.MESSAGES.PVP_STARTED_TITLE.split("%nl%");
            if (startTitle.length == 1) {
                titles[0][0] = Utils.translate(startTitle[0]);
                titles[0][1] = null;
            } else {
                titles[0][0] = Utils.translate(startTitle[0]);
                titles[0][1] = Utils.translate(startTitle[1]);
            }
            String[] endTitle = Settings.IMP.MESSAGES.PVP_STOPPED_TITLE.split("%nl%");
            if (endTitle.length == 1) {
                titles[1][0] = Utils.translate(endTitle[0]);
                titles[1][1] = null;
            } else {
                titles[1][0] = Utils.translate(endTitle[0]);
                titles[1][1] = Utils.translate(endTitle[1]);
            }
        }
    }
}
