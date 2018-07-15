package ru.leymooo.config;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Settings extends Config {

    @Ignore
    public static final Settings IMP = new Settings();

    @Create
    public MESSAGES MESSAGES;

    @Comment({"Не используйте '\\n', используйте %nl%", "Чтобы отключить сообщение, оставте его пустым"})
    public static class MESSAGES {

        public String PVP_STARTED = "&bВы вошли в режим &e&lPVP&b!";
        public String PVP_STARTED_TITLE = "&bAntiRelog%nl%&aВы вошли в режим &ePVP&a!";

        public String PVP_STOPPED = "&bВы вышли из режима &e&lPVP&b!";
        public String PVP_STOPPED_TITLE = "&bAntiRelog%nl%&aВы вышли из режима &ePVP&a!";
        public String PVP_STOPPED_ACTIONBAR = "&e&lPVP &aокончено, Вы снова можете использовать команды и выходить из игры!";

        public String PVP_BOSSBAR = "&r&lРежим &c&lPVP &r&l- &a&l%time% &r&lсек.";
        public String PVP_ACTIONBAR = "&r&lРежим &c&lPVP&r&l, не выходите из игры &a&l%time% &r&lсек.";
        
        public String PVP_LEAVED = "&aИгрок &c&l%player% &aпокинул игру во время &b&lПВП&a и был наказан.";

        public String CHAT_DISABLED = "&b&lВы не можете использовать команды в &e&lPvP&b&l.";
        public String FLY_DISABLED = "&cВы ударили игрока! &aПолёт был отключен.";
        public String GOD_DISABLED = "&cВы ударили игрока! &aРежим бога был отключен.";
        public String GM_DISABLED = "&cВы ударили игрока! &aКреатив был отключен.";
        public String VANISH_DISABLED = "&cВы ударили игрока! &aНевидимость была отключен.";

        public String APPLE_DISABLED = "&b&lВы сможете использовать золотое яблоко через &a&l%time% &b&lсек.";

        @Comment({ "Какую команду выполнять когда игрок выходит во время пвп", "Оставте пустым чтобы отключить" })
        public String COMMAND_ON_LEAVE = "";

    }

    @Comment("-1 чтобы отключить")
    public int GOLDEN_APPLE_COOLDOWN = 60;

    @Comment("Как долго длиться ПВП режим? -1 чтобы отключить ПВП режим")
    public int PVP_TIME = 12;

    @Comment("Убивать ли игрока, если он вышел во время пвп?")
    public boolean KILL_ON_LEAVE = true;

    @Comment("Отключать ли у игрока который ударил FLY, GM, GOD, VANISH?")
    public boolean CHECKS_ENABLED = true;
    
    @Comment("Показывать ли сообщение всем игрокам что игрок вышел во время ПВП?")
    public boolean SHOW_LEAVED_MESSAGE = true;
    
    public boolean REMOVE_JOIN_MESSAGE = true;
    public boolean REMOVE_LEAVE_MESSAGE = true;
    public boolean REMOVE_DEATH_MESSAGE = true;
    
    @Comment("Включить ли таймер в боссбаре?")
    public boolean BOSSBAR_ENABLED = true;
    @Comment("Включить ли таймер в ActionBar?")
    public boolean ACTIONBAR_ENABLED = true;
    @Comment("Включить ли титлы в начале и вконце пвп?")
    public boolean TITLES_ENABLED = true;
    
    @Comment("В маленьком регистре")
    public List<String> DISABLED_WORLDS = Arrays.asList("world1", "world2");
    
    public void reload(File file) {
        load(file);
        save(file);
    }
}
