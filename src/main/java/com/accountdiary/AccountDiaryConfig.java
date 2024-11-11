//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.accountdiary;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.time.LocalDate;

@ConfigGroup("accountdiary")
public interface AccountDiaryConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General config options",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigSection(
            name = "Events",
            description = "Events to log",
            position = 1,
            closedByDefault = false
    )
    String eventsSection = "events";

    @ConfigItem(
            keyName = "previewScreenshot",
            name = "Preview Screenshot",
            description = "Display a downscaled version of an event's screenshot",
            section = "general"
    )
    default boolean previewScreenshot() {
        return true;
    }

    @ConfigItem(
            keyName = "openScreenshot",
            name = "Open Screenshot",
            description = "Open screenshot when event is clicked",
            section = "general"
    )
    default boolean openScreenshot() {
        return true;
    }

    @ConfigItem(
            keyName = "apiConnections",
            name = "Allow API Connections",
            description = "Upload your accomplishments to accountdiary website",
            position = 1,
            warning = "Enabling this option submits your IP address and account hash to a 3rd party website not controlled or verified by the RuneLite Developers.",
            section = "general"
    )
    default boolean apiConnections() {
        return false;
    }

    @ConfigItem(
            keyName = "levels",
            name = "Levels",
            description = "Log level ups",
            position = 0,
            section = "events"
    )
    default boolean levels() {
        return true;
    }

    @ConfigItem(
            keyName = "levelInterval",
            name = "Levels Interval",
            description = "Configure the interval at which level ups are logged (99 is always logged)",
            position = 1,
            section = "events"
    )
    default levelIntervals levelInterval() {
        return levelIntervals.INTERVAL_1;
    }

    @ConfigItem(
            keyName = "expMilestones",
            name = "Experience Milestones",
            description = "Log experience milestones",
            position = 2,
            section = "events"
    )
    default boolean expMilestones() {
        return true;
    }

    @ConfigItem(
            keyName = "expInterval",
            name = "Experience Interval",
            description = "Configure the interval at which experience milestones are logged (in millions)",
            position = 3,
            section = "events"
    )
    default int expInterval() {
        return 1;
    }

    @ConfigItem(
            keyName = "pet",
            name = "Pet",
            description = "Log pet",
            position = 4,
            section = "events"
    )
    default boolean pet() {
        return true;
    }

    @ConfigItem(
            keyName = "bossKillCount",
            name = "Boss Kill Count",
            description = "Log Boss Kill Count",
            position = 6,
            section = "events"
    )
    default boolean bossKillCount() {
        return false;
    }

    @ConfigItem(
            keyName = "bossKillCountInterval",
            name = "Boss Kill Count Interval",
            description = "Configure the interval at which boss kill count is logged",
            position = 7,
            section = "events"
    )
    default bossKillCountIntervals bossKillCountInterval() {
        return bossKillCountIntervals.INTERVAL_100;
    }

    @ConfigItem(
            keyName = "bossPersonalBest",
            name = "Boss Personal Best",
            description = "Log new personal bests on boss kills",
            position = 8,
            section = "events"
    )
    default boolean bossPersonalBest() {
        return true;
    }

    @ConfigItem(
            keyName = "loot",
            name = "Loot",
            description = "Log valuable loot",
            position = 8,
            section = "events"
    )
    default boolean loot() {
        return true;
    }

    @ConfigItem(
            keyName = "lootThreshold",
            name = "Loot Threshold",
            description = "Minimum value of drops to log",
            position = 9,
            section = "events"
    )
    default int lootThreshold() {
        return 1000000;
    }

    @ConfigItem(
            keyName = "untradeables",
            name = "Untradeables",
            description = "Log untradeable drops",
            position = 10,
            section = "events"
    )
    default boolean untradeables() {
        return false;
    }

    @ConfigItem(
            keyName = "collectionLog",
            name = "Collection Log",
            description = "Log new collections",
            position = 11,
            section = "events"
    )
    default boolean collectionLog() {
        return true;
    }

    @ConfigItem(
            keyName = "combatAchievements",
            name = "Combat Achievements",
            description = "Log combat achievement completions",
            position = 12,
            section = "events"
    )
    default boolean combatAchievements() {
        return true;
    }

    @ConfigItem(
            keyName = "quests",
            name = "Quests",
            description = "Log quest completions",
            position = 13,
            section = "events"
    )
    default boolean quests() {
        return true;
    }

    @ConfigItem(
            keyName = "achievementDiary",
            name = "Achievement Diary",
            description = "Log achievement diary completions",
            position = 14,
            section = "events"
    )
    default boolean achievementDiary() {
        return true;
    }

    @ConfigItem(
            keyName = "clueScroll",
            name = "Clue Scroll",
            description = "Log clue scroll completions",
            position = 15,
            section = "events"
    )
    default boolean clueScroll() {
        return true;
    }

    @ConfigItem(
            keyName = "clueScrollInterval",
            name = "Clue Scroll Interval",
            description = "",
            position = 16,
            section = "events"
    )
    default clueScrollIntervals clueScrollInterval() {
        return clueScrollIntervals.INTERVAL_1;
    }

    @ConfigItem(
            keyName = "activeDate",
            name = "Active Date",
            description = "The currently selected date",
            hidden = true
    )
    default String activeDate()
    {
        return LocalDate.now().toString();
    }

    @ConfigItem(
            keyName = "activeDate",
            name = "",
            description = "",
            hidden = true
    )
    void setActiveDate(String newDate);

    public static enum bossKillCountIntervals {
        INTERVAL_10("10", 10),
        INTERVAL_50("50", 50),
        INTERVAL_100("100", 100),
        INTERVAL_250("250", 250),
        INTERVAL_500("500", 500);

        private final String name;
        private final int value;

        public String toString() {
            return this.name;
        }

        private bossKillCountIntervals(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    public static enum levelIntervals {
        INTERVAL_1("1", 1),
        INTERVAL_5("5", 5),
        INTERVAL_10("10", 10);

        private final String name;
        private final int value;

        public String toString() {
            return this.name;
        }

        private levelIntervals(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    public static enum clueScrollIntervals {
        INTERVAL_1("1", 1),
        INTERVAL_5("5", 5),
        INTERVAL_10("10", 10);

        private final String name;
        private final int value;

        public String toString() {
            return this.name;
        }

        private clueScrollIntervals(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }
}