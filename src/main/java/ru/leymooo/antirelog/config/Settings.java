package ru.leymooo.antirelog.config;

import ru.leymooo.annotatedyaml.ConfigOptions.Comment;
import ru.leymooo.annotatedyaml.ConfigOptions.ConfigKey;
import ru.leymooo.annotatedyaml.ConfigOptions.Final;
import ru.leymooo.annotatedyaml.Configuration;
import ru.leymooo.annotatedyaml.ConfigurationProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Settings extends Configuration {

    @Final
    @ConfigKey("config-version")
    private String configVersion = "1.2";
    private Messages messages = new Messages();
    @Comment("Кулдавн для обычных золотых яблок во время пвп.")
    @ConfigKey("golden-apple-cooldown")
    private int goldenAppleCooldown = 30;
    @Comment("Кулдавн для зачарованых золотых яблок во время пвп.")
    @ConfigKey("enchanted-golden-apple-cooldown")
    private int enchantedGoldenAppleCooldown = 60;
    @Comment("Кулдавн для жемчугов края во время пвп.")
    @ConfigKey("ender-pearl-cooldown")
    private int enderPearlCooldown = 15;
    @Comment("Длительность пвп")
    @ConfigKey("pvp-time")
    private int pvpTime = 12;
    @Comment("Убивать ли игрока если он вышел во время пвп?")
    @ConfigKey("kill-on-leave")
    private boolean killOnLeave = true;
    @Comment("Убивать ли игрока если его кикнули во время пвп?")
    @ConfigKey("kill-on-kick")
    private boolean killOnKick = true;
    @Comment("Выполнять ли команды, если игрока кикнули во время пвп?")
    @ConfigKey("run-commands-on-kick")
    private boolean runCommandsOnKick = true;
    @Comment("Какой текст должен быть впричине кика, чтобы его убило/выполнились команды. Если пусто, то будет убивать/выполняться " +
            "команды всегда")
    @ConfigKey("kick-messages")
    private List<String> kickMessages = Arrays.asList("спам", "реклама", "анти-чит");
    @Comment({"Какие команды запускать от консоли при выходе игрока во время пвп?", "commands-on-leave:", "- command1", "- command2 " +
            "%player%"})
    @ConfigKey("commands-on-leave")
    private List<String> commandsOnLeave = new ArrayList<>(0);
    @Comment("Отключать ли у игрока который ударил FLY, GM, GOD, VANISH?")
    @ConfigKey("disable-powerups")
    private boolean disablePowerups = true;
    @Comment("Отключать ли возможность телепортироваться во время пвп? (Отключает любые способы кроме жемчуга края)")
    @ConfigKey("disable-teleports-in-pvp")
    private boolean disableTeleportsInPvp = true;
    @Comment("Игнорировать ли PVP deny во время пвп между игроками?")
    @ConfigKey("ignore-worldguard")
    private boolean ignoreWorldGuard = true;
    @Comment({"Включать ли игроку, который не участвует в пвп и удрарил другого игрока в pvp, pvp режим",
            "Если два игрока дерутся на територии где PVP deny и их ударить, то у того кто ударил так-же включится PVP режим"})
    @ConfigKey("join-pvp-in-worldguard")
    private boolean joinPvPInWorldGuard = false;
    @Comment("Скрывать ли сообщения о заходе игроков?")
    @ConfigKey("hide-join-message")
    private boolean hideJoinMessage = false;
    @Comment("Скрывать ли сообщения о выходе игроков?")
    @ConfigKey("hide-leave-message")
    private boolean hideLeaveMessage = false;
    @Comment("Скрывать ли сообщение о смерти игроков?")
    @ConfigKey("hide-death-message")
    private boolean hideDeathMessage = false;
    @Comment("Миры в котором плагин не работает")
    private List<String> disabledWorlds = Arrays.asList("world1", "world2");

    public Settings(ConfigurationProvider provider) {
        super(provider);
    }

    public String getConfigVersion() {
        return configVersion;
    }

    public Messages getMessages() {
        return messages;
    }

    public int getGoldenAppleCooldown() {
        return goldenAppleCooldown;
    }

    public int getEnchantedGoldenAppleCooldown() {
        return enchantedGoldenAppleCooldown;
    }

    public int getEnderPearlCooldown() {
        return enderPearlCooldown;
    }

    public int getPvpTime() {
        return pvpTime;
    }

    public boolean isKillOnLeave() {
        return killOnLeave;
    }

    public boolean isKillOnKick() {
        return killOnKick;
    }

    public boolean isRunCommandsOnKick() {
        return runCommandsOnKick;
    }

    public List<String> getKickMessages() {
        return kickMessages;
    }

    public boolean isDisablePowerups() {
        return disablePowerups;
    }

    public boolean isDisableTeleportsInPvp() {
        return disableTeleportsInPvp;
    }

    public boolean isIgnoreWorldGuard() {
        return ignoreWorldGuard;
    }

    public boolean isJoinPvPInWorldGuard() {
        return joinPvPInWorldGuard;
    }

    public boolean isHideJoinMessage() {
        return hideJoinMessage;
    }

    public boolean isHideLeaveMessage() {
        return hideLeaveMessage;
    }

    public boolean isHideDeathMessage() {
        return hideDeathMessage;
    }

    public List<String> getCommandsOnLeave() {
        return commandsOnLeave;
    }

    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }


    @Override
    public String toString() {
        return "Settings{" +
                "configVersion='" + configVersion + '\'' +
                ", messages=" + messages +
                ", goldenAppleCooldown=" + goldenAppleCooldown +
                ", enchantedGoldenAppleCooldown=" + enchantedGoldenAppleCooldown +
                ", enderPearlCooldown=" + enderPearlCooldown +
                ", pvpTime=" + pvpTime +
                ", killOnLeave=" + killOnLeave +
                ", killOnKick=" + killOnKick +
                ", runCommandsOnKick=" + runCommandsOnKick +
                ", kickMessages=" + kickMessages +
                ", commandsOnLeave=" + commandsOnLeave +
                ", disablePowerups=" + disablePowerups +
                ", disableTeleportsInPvp=" + disableTeleportsInPvp +
                ", ignoreWorldGuard=" + ignoreWorldGuard +
                ", joinPvPInWorldGuard=" + joinPvPInWorldGuard +
                ", hideJoinMessage=" + hideJoinMessage +
                ", hideLeaveMessage=" + hideLeaveMessage +
                ", hideDeathMessage=" + hideDeathMessage +
                ", disabledWorlds=" + disabledWorlds +
                '}';
    }
}
