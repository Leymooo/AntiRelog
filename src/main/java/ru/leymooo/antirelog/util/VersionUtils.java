package ru.leymooo.antirelog.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.leymooo.antirelog.Antirelog;

import java.util.logging.Level;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtils {

    private static int majorVersion;
    private static int minorVersion = 0;
    private static boolean minorVersionResolved = false;

    static {
        detectServerVersion();
    }

    public static int getMajorVersion() {
        return majorVersion;
    }

    public static int getMinorVersion() {
        return minorVersion;
    }


    public static boolean isVersion(int major) {
        return majorVersion >= major;
    }

    public static boolean isVersion(int major, int minor) {
        return minorVersionResolved ? majorVersion >= major && minorVersion >= minor : majorVersion >= major;
    }

    private static void detectServerVersion() {
        //на все случаи жизни
        try {
            Pattern versionPattern = Pattern.compile("\\(MC: (\\d)\\.(\\d+)\\.?(\\d+?)?\\)");
            Matcher matcher = versionPattern.matcher(Bukkit.getVersion());

            matcher.find();
            MatchResult matchResult = matcher.toMatchResult();
            majorVersion = Integer.parseInt(matchResult.group(2), 10);
            if (matchResult.groupCount() >= 3) {
                minorVersion = Integer.parseInt(matchResult.group(3), 10);
                minorVersionResolved = true;
            }
            JavaPlugin.getPlugin(Antirelog.class).getLogger().info("Detected version: 1."  + majorVersion + "." + minorVersion);
        } catch (Exception e) {
            JavaPlugin.getPlugin(Antirelog.class).getLogger().log(Level.WARNING, "Failed to detect MC version, trying another method...");
            try {
                String[] split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
                majorVersion = Integer.parseInt(split[1]);
                if (split.length == 3) {
                    minorVersion = Integer.parseInt(split[2]);
                    minorVersionResolved = true;
                }
                JavaPlugin.getPlugin(Antirelog.class).getLogger().info("Detected version: 1."  + majorVersion + "." + minorVersion);
            } catch (Exception e2) {
                JavaPlugin.getPlugin(Antirelog.class).getLogger().log(Level.WARNING, "Failed to detect MC version, trying another method... ");
                try {
                    String[] split = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].split("_");
                    majorVersion = Integer.parseInt(split[1]);
                    JavaPlugin.getPlugin(Antirelog.class).getLogger().info("Detected version: 1."  + majorVersion + "." + minorVersion);
                } catch (Exception e3) {
                    JavaPlugin.getPlugin(Antirelog.class).getLogger().log(Level.WARNING, "Failed to detect MC version, trying another method... Fallback to 1" +
                            ".8.8.", e);
                    e2.printStackTrace();
                    e3.printStackTrace();
                    majorVersion = 8;
                    minorVersion = 8;
                    JavaPlugin.getPlugin(Antirelog.class).getLogger().info("Detected version: 1."  + majorVersion + "." + minorVersion);
                }
            }
        }
    }

}
