package ru.leymooo.antirelog.config;


import ru.leymooo.annotatedyaml.Annotations.*;

import ru.leymooo.annotatedyaml.Configuration;
import ru.leymooo.annotatedyaml.ConfigurationProvider;

import java.util.*;
import java.util.stream.Collectors;

public class Settings extends Configuration {

    @Final
    @Key("config-version")
    private String configVersion = "1.7";
    private Messages messages = new Messages();

    @Comment("Кулдавн для обычных золотых яблок во время пвп.")
    @Key("golden-apple-cooldown")
    private int goldenAppleCooldown = 30;

    @Comment({"Кулдавн для зачарованых золотых яблок во время пвп.", "Значение 0 отключает кулдаун; -1 отключает использование во время " +
            "пвп"})
    @Key("enchanted-golden-apple-cooldown")
    private int enchantedGoldenAppleCooldown = 60;

    @Comment({"Кулдавн для жемчугов края во время пвп.", "Значение 0 отключает кулдаун; -1 отключает использование во время " +
            "пвп"})
    @Key("ender-pearl-cooldown")
    private int enderPearlCooldown = 15;

    @Comment({"Кулдавн для корусов во время пвп.", "https://minecraft-ru.gamepedia.com/Плод_коруса", "Значение 0 отключает кулдаун; -1 " +
            "отключает использование во время пвп"})
    @Key("chorus-cooldown")
    private int сhorusCooldown = 7;

    @Comment({"Кулдавн для фейверков во время пвп. (чтобы не убегали на элитрах)", "Значение 0 отключает кулдаун; -1 отключает использование во время пвп"})
    @Key("firework-cooldown")
    private int fireworkCooldown = 60;

    @Comment({"Кулдавн для тотемов бесмертия во время пвп.", "Значение 0 отключает кулдаун; -1 " +
            "отключает использование во время пвп"})
    @Key("totem-cooldown")
    private int totemCooldown = 60;

    @Comment("Длительность пвп")
    @Key("pvp-time")
    private int pvpTime = 12;

    @Comment("Отключить ли возможность писать команды в пвп?")
    @Key("disable-commands-in-pvp")
    private boolean disableCommandsInPvp = true;

    @Comment({"Команды которые можно писать во время пвп", "Команды писать без '/' (кол-во '/' - 1)", "Плагин будет пытаться сам " +
            "определить алисы для команд (msg,tell,m), но для некоторых команд возможно придется самому прописать алиасы",
            "commands-whitelist:", "- command", "- command2", "- /expand"})
    @Key("commands-whitelist")
    private List<String> whiteListedCommands = new ArrayList<>(0);

    @Key("cancel-interact-with-entities")
    @Comment("Отменять ли взаимодействие с энтити, во время пвп")
    private boolean cancelInteractWithEntities = false;

    @Comment("Убивать ли игрока если он вышел во время пвп?")
    @Key("kill-on-leave")
    private boolean killOnLeave = true;

    @Comment("Убивать ли игрока если его кикнули во время пвп?")
    @Key("kill-on-kick")
    private boolean killOnKick = true;

    @Comment("Выполнять ли команды, если игрока кикнули во время пвп?")
    @Key("run-commands-on-kick")
    private boolean runCommandsOnKick = true;

    @Comment("Какой текст должен быть впричине кика, чтобы его убило/выполнились команды. Если пусто, то будет убивать/выполняться " +
            "команды всегда")
    @Key("kick-messages")
    private List<String> kickMessages = Arrays.asList("спам", "реклама", "анти-чит");

    @Comment({"Какие команды запускать от консоли при выходе игрока во время пвп?", "commands-on-leave:", "- command1", "- command2 " +
            "%player%"})
    @Key("commands-on-leave")
    private List<String> commandsOnLeave = new ArrayList<>(0);

    @Comment("Отключать ли у игрока который ударил FLY, GM, GOD, VANISH?")
    @Key("disable-powerups")
    private boolean disablePowerups = true;

    @Comment({"Какие команды выполнять, если были отключены усиления у игрока", "Данную настройку можно использовать например для того, " +
            "чтобы наложить на игрока отрицательный эффект, если он начал пвп в ГМ/ФЛАЕ/и тд",
            "commands-on-powerups-disable: ", "- command1 %player%", "- effect give %player% weakness 10"
    })
    @Key("commands-on-powerups-disable")
    private List<String> commandsOnPowerupsDisable = new ArrayList<>(0);

    @Comment({"Отключать ли возможность телепортироваться во время пвп?"})
    @Key("disable-teleports-in-pvp")
    private boolean disableTeleportsInPvp = true;

    @Comment("Игнорировать ли PVP deny во время пвп между игроками?")
    @Key("ignore-worldguard")
    private boolean ignoreWorldGuard = true;

    @Comment({"Включать ли игроку, который не участвует в пвп и удрарил другого игрока в pvp, pvp режим",
            "Если два игрока дерутся на територии где PVP deny и их ударить, то у того кто ударил так-же включится PVP режим"})
    @Key("join-pvp-in-worldguard")
    private boolean joinPvPInWorldGuard = false;

    @Comment({"В каких регионах не будет работать плагин", "ignored-worldguard-regions:", "- duels1", "- region2"})
    @Key("ignored-worldguard-regions")
    private List<String> ignoredWgRegions = new ArrayList<>(0);
    @Ignore
    private Set<String> ignoredWgRegionsSet;

    @Comment("Отключать ли активный ПВП режим когда игрок заходит в игнорируемый регион?")
    @Key("disable-pvp-in-ignored-region")
    private boolean disablePvpInIgnoredRegion = false;

    @Comment("Скрывать ли сообщения о заходе игроков?")
    @Key("hide-join-message")
    private boolean hideJoinMessage = false;

    @Comment("Скрывать ли сообщения о выходе игроков?")
    @Key("hide-leave-message")
    private boolean hideLeaveMessage = false;

    @Comment("Скрывать ли сообщение о смерти игроков?")
    @Key("hide-death-message")
    private boolean hideDeathMessage = false;

    @Comment("Миры в котором плагин не работает")
    private List<String> disabledWorlds = Arrays.asList("world1", "world2");
    @Ignore
    private Set<String> disabledWorldsSet;

    @Override
    public void loaded() {
        this.ignoredWgRegionsSet = ignoredWgRegions.stream().map(String::toLowerCase).collect(Collectors.toSet());
        this.disabledWorldsSet = disabledWorlds.stream().map(String::toLowerCase).collect(Collectors.toSet());
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

    public int getСhorusCooldown() {
        return сhorusCooldown;
    }

    public int getFireworkCooldown() {
        return fireworkCooldown;
    }

    public int getTotemCooldown() {
        return totemCooldown;
    }

    public int getPvpTime() {
        return pvpTime;
    }

    public boolean isDisableCommandsInPvp() {
        return disableCommandsInPvp;
    }

    public boolean isCancelInteractWithEntities() {
        return cancelInteractWithEntities;
    }

    public List<String> getCommandsOnPowerupsDisable() {
        return commandsOnPowerupsDisable;
    }

    public List<String> getWhiteListedCommands() {
        return whiteListedCommands;
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

    public Set<String> getIgnoredWgRegions() {
        return ignoredWgRegionsSet;
    }

    public boolean isDisablePvpInIgnoredRegion() {
        return disablePvpInIgnoredRegion;
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

    public Set<String> getDisabledWorlds() {
        return disabledWorldsSet;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "configVersion='" + configVersion + '\'' +
                ", messages=" + messages +
                ", goldenAppleCooldown=" + goldenAppleCooldown +
                ", enchantedGoldenAppleCooldown=" + enchantedGoldenAppleCooldown +
                ", enderPearlCooldown=" + enderPearlCooldown +
                ", сhorusCooldown=" + сhorusCooldown +
                ", fireworkCooldown=" + fireworkCooldown +
                ", totemCooldown=" + totemCooldown +
                ", pvpTime=" + pvpTime +
                ", disableCommandsInPvp=" + disableCommandsInPvp +
                ", whiteListedCommands=" + whiteListedCommands +
                ", cancelInteractWithEntities=" + cancelInteractWithEntities +
                ", killOnLeave=" + killOnLeave +
                ", killOnKick=" + killOnKick +
                ", runCommandsOnKick=" + runCommandsOnKick +
                ", kickMessages=" + kickMessages +
                ", commandsOnLeave=" + commandsOnLeave +
                ", disablePowerups=" + disablePowerups +
                ", commandsOnPowerupsDisable=" + commandsOnPowerupsDisable +
                ", disableTeleportsInPvp=" + disableTeleportsInPvp +
                ", ignoreWorldGuard=" + ignoreWorldGuard +
                ", joinPvPInWorldGuard=" + joinPvPInWorldGuard +
                ", ignoredWgRegions=" + ignoredWgRegions +
                ", disablePvpInIgnoredRegion=" + disablePvpInIgnoredRegion +
                ", hideJoinMessage=" + hideJoinMessage +
                ", hideLeaveMessage=" + hideLeaveMessage +
                ", hideDeathMessage=" + hideDeathMessage +
                ", disabledWorlds=" + disabledWorlds +
                '}';
    }
}
