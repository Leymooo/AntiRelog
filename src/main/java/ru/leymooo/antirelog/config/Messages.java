package ru.leymooo.antirelog.config;

import ru.leymooo.annotatedyaml.ConfigOptions.Comment;
import ru.leymooo.annotatedyaml.ConfigOptions.ConfigKey;
import ru.leymooo.annotatedyaml.Configuration;

@Comment("Для того, чтобы отключить сообщение, оставьте его пустым")
public class Messages extends Configuration {
    @ConfigKey("pvp-started")
    private String pvpStarted = "&bВы начали &e&lPVP&b!";
    @ConfigKey("pvp-started-title")
    private String pvpStartedTitle = "&bAntiRelog";
    @ConfigKey("pvp-started-subtitle")
    private String pvpStartedSubtitle = "Вы вошли в режим &ePVP&a!";
    @ConfigKey("pvp-stopped")
    private String pvpStopped = "&e&lPVP &bокончено";
    @ConfigKey("pvp-stopped-title")
    private String pvpStoppedTitle = "&bAntiRelog";
    @ConfigKey("pvp-stopped-subtitle")
    private String pvpStoppedSubtitle = "Вы вышли из режима &ePVP&a!";
    @ConfigKey("pvp-stopped-actionbar")
    private String pvpStoppedActionbar = "&e&lPVP &aокончено, Вы снова можете использовать команды и выходить из игры!";
    @ConfigKey("in-pvp-bossbar")
    private String inPvpBossbar = "&r&lРежим &c&lPVP &r&l- &a&l%time% &r&l%formated-sec%.";
    @ConfigKey("in-pvp-actionbar")
    private String inPvpActionbar = "&r&lРежим &c&lPVP&r&l, не выходите из игры &a&l%time% &r&l%formated-sec%.";
    @ConfigKey("pvp-leaved")
    private String pvpLeaved = "&aИгрок &c&l%player% &aпокинул игру во время &b&lПВП&a и был наказан.";
    @ConfigKey("commands-disabled")
    private String commandsDisabled = "&b&lВы не можете использовать команды в &e&lPvP&b&l. &b&lПодождите &a&l%time% &b&l%formated-sec%.";
    @ConfigKey("item-cooldown")
    private String itemCooldown = "&b&lВы сможете использовать этот предмет через &a&l%time% &b&lсек.";
    @ConfigKey("item-disabled-in-pvp")
    private String itemDisabledInPvp = "&b&lВы не можете использовать этот предмет в &e&lPVP &b&lрежиме";
    @ConfigKey("pvp-started-with-powerups")
    @Comment("Данное сообщение будет появляться только тогда, когда настроена функция 'commands-on-powerups-disable'")
    private String pvpStartedWithPowerups = "&c&lВы начали пвп с включеным GM/FLY/и тд и за это получили негативный эффект";

    public String getPvpStarted() {
        return pvpStarted;
    }

    public String getPvpStartedTitle() {
        return pvpStartedTitle;
    }

    public String getPvpStartedSubtitle() {
        return pvpStartedSubtitle;
    }

    public String getPvpStopped() {
        return pvpStopped;
    }

    public String getPvpStoppedTitle() {
        return pvpStoppedTitle;
    }

    public String getPvpStoppedSubtitle() {
        return pvpStoppedSubtitle;
    }

    public String getPvpStoppedActionbar() {
        return pvpStoppedActionbar;
    }

    public String getInPvpBossbar() {
        return inPvpBossbar;
    }

    public String getInPvpActionbar() {
        return inPvpActionbar;
    }

    public String getPvpLeaved() {
        return pvpLeaved;
    }

    public String getCommandsDisabled() {
        return commandsDisabled;
    }

    public String getItemCooldown() {
        return itemCooldown;
    }

    public String getItemDisabledInPvp() {
        return itemDisabledInPvp;
    }

    public String getPvpStartedWithPowerups() {
        return pvpStartedWithPowerups;
    }

    @Override
    public String toString() {
        return "Messages{" +
                "pvpStarted='" + pvpStarted + '\'' +
                ", pvpStartedTitle='" + pvpStartedTitle + '\'' +
                ", pvpStartedSubtitle='" + pvpStartedSubtitle + '\'' +
                ", pvpStopped='" + pvpStopped + '\'' +
                ", pvpStoppedTitle='" + pvpStoppedTitle + '\'' +
                ", pvpStoppedSubtitle='" + pvpStoppedSubtitle + '\'' +
                ", pvpStoppedActionbar='" + pvpStoppedActionbar + '\'' +
                ", inPvpBossbar='" + inPvpBossbar + '\'' +
                ", inPvpActionbar='" + inPvpActionbar + '\'' +
                ", pvpLeaved='" + pvpLeaved + '\'' +
                ", commandsDisabled='" + commandsDisabled + '\'' +
                ", itemCooldown='" + itemCooldown + '\'' +
                ", itemDisabledInPvp='" + itemDisabledInPvp + '\'' +
                ", pvpStartedWithPowerups='" + pvpStartedWithPowerups + '\'' +
                '}';
    }
}
