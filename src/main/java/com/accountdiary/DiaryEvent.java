package com.accountdiary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;

@RequiredArgsConstructor
@Getter
public enum DiaryEvent {
    ACHIEVEMENT_DIARY("Achievement Diary", ImageUtil.loadImageResource(AccountDiaryPlugin.class, "Task_Master_icon.png")),
    LEVEL("Level Up", ImageUtil.loadImageResource(AccountDiaryPlugin.class, "Skills_icon.png")),
    EXPERIENCE("Experience Milestone", ImageUtil.loadImageResource(AccountDiaryPlugin.class, "Skills_icon.png")),
    CLUE_SCROLL("Clue Scroll", ImageUtil.loadImageResource(AccountDiaryPlugin.class, "watch.png")),
    PET("Pet", ImageUtil.loadImageResource(AccountDiaryPlugin.class, "Pet_shop_icon.png")),
    BOSS_KILL("Boss Kill", ImageUtil.loadImageResource(AccountDiaryPlugin.class, "Sword_shop_icon.png")),
    PERSONAL_BEST("Personal Best", ImageUtil.loadImageResource(AccountDiaryPlugin.class, "Speedrunning_shop_icon.png")),
    LOOT("Valuable Loot", ImageUtil.loadImageResource(AccountDiaryPlugin.class, "Grand_Exchange_icon.png")),
    UNTRADEABLE("Untradeable Loot", ImageUtil.loadImageResource(AccountDiaryPlugin.class, "Minigame_map_icon.png")),
    COLLECTION_LOG("Collection Log", ImageUtil.loadImageResource(AccountDiaryPlugin.class, "Collection_log.png")),
    COMBAT_ACHIEVEMENT("Combat Achievement", ImageUtil.loadImageResource(AccountDiaryPlugin.class, "Combat_Achievements_icon.png")),
    QUEST("Quest Completed", ImageUtil.loadImageResource(AccountDiaryPlugin.class, "Quest_start_icon.png"));

    private final String name;
    private final BufferedImage icon;
}
