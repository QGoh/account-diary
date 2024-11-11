//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.accountdiary;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.swing.*;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneScapeProfileType;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
        name = "Account Diary"
)
public class AccountDiaryPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(AccountDiaryPlugin.class);
    private static final String COLLECTION_LOG_TEXT = "New item added to your collection log: ";
    private static final Map<Integer, String> CHEST_LOOT_EVENTS = ImmutableMap.of(12127, "The Gauntlet");
    private static final int GAUNTLET_REGION = 7512;
    private static final int CORRUPTED_GAUNTLET_REGION = 7768;
    private static final Pattern NUMBER_PATTERN = Pattern.compile("([,0-9]+)");
    private static final Pattern BOSSKILL_MESSAGE_PATTERN = Pattern.compile("Your (?<boss>.+)\\s(?<type>kill|chest|completion|kill count|count)\\s? is: (?<count>[\\d,]+)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern BOSSKILL_MESSAGE_PATTERN_2 = Pattern.compile("");
    private static final Pattern PERSONAL_BEST = Pattern.compile(":(.*)\\(new personal best\\)");
    private static final Pattern UNTRADEABLE_DROP_PATTERN = Pattern.compile(".*Untradeable drop: ([^<>]+)(?:</col>)?");
    private static final Pattern QUEST_PATTERN_1 = Pattern.compile(".+?ve\\.*? (?<verb>been|rebuilt|.+?ed)? ?(?:the )?'?(?<quest>.+?)'?(?: [Qq]uest)?[!.]?$");
    private static final Pattern QUEST_PATTERN_2 = Pattern.compile("'?(?<quest>.+?)'?(?: [Qq]uest)? (?<verb>[a-z]\\w+?ed)?(?: f.*?)?[!.]?$");
    private static final Pattern COMBAT_ACHIEVEMENTS_PATTERN = Pattern.compile("Congratulations, you've completed an? (?<tier>\\w+) combat task: <col=[0-9a-f]+>(?<task>(.+))</col>");
    private static final Pattern ACHIEVEMENT_DIARY_PATTERN = Pattern.compile("Congratulations! You have completed all of the (?<difficulty>.+) tasks in the (?<area>.+) area");
    private static final ImmutableList<String> RFD_TAGS = ImmutableList.of("Another Cook", "freed", "defeated", "saved");
    private static final ImmutableList<String> WORD_QUEST_IN_NAME_TAGS = ImmutableList.of("Another Cook", "Doric", "Heroes", "Legends", "Observatory", "Olaf", "Waterfall");
    private static final ImmutableList<String> PET_MESSAGES = ImmutableList.of("You have a funny feeling like you're being followed", "You feel something weird sneaking into your backpack", "You have a funny feeling like you would have been followed");

    @Inject
    private Client client;

    @Inject
    private AccountDiaryConfig config;

    @Inject
    private DrawManager drawManager;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private ItemManager itemManager;

    @Getter(AccessLevel.PACKAGE)
    @Inject
    private ChatboxPanelManager chatboxPanelManager;

    @Inject
    private ClientToolbar toolbar;

    @Inject
    private DiaryManager diaryManager;

    @Inject
    private DateManager dateManager;

    private List<String> lootNpcs;
    private Hashtable<String, Integer> currentXP;
    private String clueType;
    private Integer clueNumber;
    private String bossName = null;
    private AccountDiaryPanel panel;
    private NavigationButton navButton;

    private FakeChatBoxMessage fake;

    @Provides
    AccountDiaryConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AccountDiaryConfig.class);
    }

    protected void startUp() throws Exception {
        lootNpcs = Collections.emptyList();
        currentXP = new Hashtable<String, Integer>();

        diaryManager.updateFilePaths(getPlayerName());
        diaryManager.initDiary();

        panel = new AccountDiaryPanel(diaryManager, dateManager);
        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "watch.png");
        navButton = NavigationButton.builder()
                .tooltip("Account Diary")
                .icon(icon)
                .panel(panel)
                .priority(4)
                .build();
        panel.refresh();

        toolbar.addNavigation(navButton);
    }

    protected void shutDown() throws Exception {

    }

    private void spoof() {
        String message = "Congratulations! You have completed all of the elite tasks in the Ardougne area. Speak to Two-pints at the Flying Horse Inn in Ardougne to claim your reward.";
        fake = new FakeChatBoxMessage(message);
        chatboxPanelManager.openInput(fake);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (fake != null) {
            fake.closeIfTriggered();
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (fake != null)
        {
            fake.triggerClose();
        }
    }

    @Subscribe
    public void onPlayerSpawned(PlayerSpawned player) {
        String playerName = getPlayerName();
        if (Objects.equals(player.getPlayer().getName(), playerName))
        {
            diaryManager.updateFilePaths(getPlayerName());
            diaryManager.initDiary();
            panel.refresh();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (client.getGameState() == GameState.LOGIN_SCREEN
                || client.getGameState() == GameState.CONNECTION_LOST)
        {
            //diaryItems.clear();
            // update panel
        }

    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged)
    {
        if (!config.levels())
        {
            return;
        }

        String skillName = statChanged.getSkill().getName();
        int newXP = statChanged.getXp();

        this.handleXpChange(skillName, newXP);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() != ChatMessageType.GAMEMESSAGE
                && event.getType() != ChatMessageType.SPAM
                && event.getType() != ChatMessageType.TRADE
                && event.getType() != ChatMessageType.FRIENDSCHATNOTIFICATION
                && event.getType() != ChatMessageType.MESBOX)
        {
            return;
        }

        String chatMessage = event.getMessage();

        if (config.achievementDiary()) this.handleAchievementDiary(chatMessage);

        if (config.clueScroll()) {
            if (chatMessage.contains("You have completed") && chatMessage.contains("Treasure"))
            {
                Matcher m = NUMBER_PATTERN.matcher(Text.removeTags(chatMessage));
                if (m.find())
                {
                    clueNumber = Integer.valueOf(m.group().replace(",", ""));
                    clueType = chatMessage.substring(chatMessage.lastIndexOf(m.group()) + m.group().length() + 1, chatMessage.indexOf("Treasure") - 1);
                }
            }
        }

        if (config.pet()) this.handlePet(chatMessage);

        if (config.bossKillCount()) this.handleBossKill(chatMessage);

        if (config.bossPersonalBest()) this.handlePersonalBest(chatMessage);

        if (config.untradeables() && !isInsideGauntlet()) this.handleUntradeable(chatMessage);

        if (config.collectionLog()) this.handleCollectionLog(chatMessage);

        if (config.combatAchievements()) this.handleCombatAchievement(chatMessage);
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        int groupId = event.getGroupId();
        if (groupId == 119) {
            String text = client.getWidget(WidgetInfo.DIARY_QUEST_WIDGET_TITLE).getText();
            Matcher mat = Pattern.compile(">(.*)<").matcher(text);
            String out = "";
            if (mat.find()) {
                out = mat.group(1);
                String details = out;
                diaryManager.addDiaryItem(DiaryEvent.QUEST, details, panel::refresh);
            }
        }

        if (config.quests()) this.handleQuest(groupId);

        if (config.clueScroll()) this.handleClueScroll(groupId);
    }

    @Subscribe
    public void onNpcLootReceived(NpcLootReceived npcLootReceived) {
        NPC npc = npcLootReceived.getNpc();
        Collection<ItemStack> items = npcLootReceived.getItems();

        if (!this.lootNpcs.isEmpty()) {
            Iterator var4 = this.lootNpcs.iterator();

            while(var4.hasNext()) {
                String npcName = (String)var4.next();
                if (WildcardMatcher.matches(npcName, npc.getName())) {
                    this.handleLoot(npc.getName(), items);
                    return;
                }
            }
        } else {
            this.handleLoot(npc.getName(), items);
        }
    }

    private void handlePersonalBest(String chatMessage) {
        Matcher m = PERSONAL_BEST.matcher(chatMessage);
        if (m.find()) {
            String personalBest = m.group(1).trim();
            String details = bossName + " in " + personalBest;
            bossName = null;
            diaryManager.addDiaryItem(DiaryEvent.PERSONAL_BEST, details, panel::refresh);
        }
    }

    private void handleAchievementDiary(String chatMessage) {
        Matcher m = ACHIEVEMENT_DIARY_PATTERN.matcher(chatMessage);
        if (m.find()) {
            String difficulty = m.group("difficulty");
            String area = m.group("area");
            String details = "Completed all " + difficulty + " diary tasks in " + area;
            diaryManager.addDiaryItem(DiaryEvent.ACHIEVEMENT_DIARY, details, panel::refresh);
        }
    }

    private void handleClueScroll(int groupId) {
        if (groupId == InterfaceID.CLUESCROLL_REWARD) {
            if (clueType == null || clueNumber == null) return;
            if (clueNumber % Integer.parseInt(config.clueScrollInterval().toString()) == 0) return;

            String details = "Completed " + clueType + " clue scroll #" + clueNumber;
            clueType = null;
            clueNumber = null;
            diaryManager.addDiaryItem(DiaryEvent.CLUE_SCROLL, details, panel::refresh);
        }
    }

    private void handlePet(String chatMessage) {
        if (PET_MESSAGES.stream().anyMatch(chatMessage::contains)) {
            String details = "Received a pet";
            diaryManager.addDiaryItem(DiaryEvent.PET, details, panel::refresh);
        }
    }

    private void handleBossKill(String chatMessage) {
        Matcher m1 = BOSSKILL_MESSAGE_PATTERN.matcher(chatMessage);
        Matcher m2 = BOSSKILL_MESSAGE_PATTERN_2.matcher(chatMessage);
        Matcher m = m1.find() ? m1 :
                (m2.find() ? m2 :
                        null);
        if (m != null)
        {
            bossName = m.group("boss");;
            String count = m.group("count").replace(",", "");
            if (Integer.parseInt(count) % Integer.parseInt(config.bossKillCountInterval().toString()) == 0) {
                String details = bossName + " kill count - " + count;
                diaryManager.addDiaryItem(DiaryEvent.BOSS_KILL, details, panel::refresh);
            }
        }
    }

    private void handleUntradeable(String chatMessage) {
        Matcher m = UNTRADEABLE_DROP_PATTERN.matcher(chatMessage);
        if (m.matches())
        {
            String untradeableDropName = m.group(1);
            diaryManager.addDiaryItem(DiaryEvent.LOOT, untradeableDropName, panel::refresh);
        }
    }

    private void handleCollectionLog(String chatMessage) {
        if (chatMessage.startsWith(COLLECTION_LOG_TEXT) && client.getVarbitValue(Varbits.COLLECTION_LOG_NOTIFICATION) == 1) {
            String entry = Text.removeTags(chatMessage).substring(COLLECTION_LOG_TEXT.length());
            String details = "New item - " + entry;
            diaryManager.addDiaryItem(DiaryEvent.COLLECTION_LOG, details, panel::refresh);
        }
    }

    private void handleCombatAchievement(String chatMessage) {
        if (chatMessage.contains("combat task") && client.getVarbitValue(Varbits.COMBAT_ACHIEVEMENTS_POPUP) == 1) {
            String details = parseCombatAchievementWidget(chatMessage);
            if (!details.isEmpty()) {
                diaryManager.addDiaryItem(DiaryEvent.COMBAT_ACHIEVEMENT, details, panel::refresh);
            }
        }
    }

    private void handleQuest(int groupId) {
        if (groupId == InterfaceID.QUEST_COMPLETED) {
            String text = client.getWidget(WidgetInfo.QUEST_COMPLETED_NAME_TEXT).getText();
            String questName = parseQuestCompletedWidget(text);
            diaryManager.addDiaryItem(DiaryEvent.QUEST, questName, panel::refresh);
        }
    }

    private void handleLoot(String name, Collection<ItemStack> items) {
        StringBuilder loot = new StringBuilder();
        int targetValue = this.config.lootThreshold();
        Iterator itemStack = stack(items).iterator();

        while(itemStack.hasNext()) {
            ItemStack item = (ItemStack)itemStack.next();
            int itemId = item.getId();
            int qty = item.getQuantity();
            int price = this.itemManager.getItemPrice(itemId);
            long total = (long)price * (long)qty;
            if (total >= (long)targetValue) {
                ItemComposition itemComposition = this.itemManager.getItemComposition(itemId);
                loot.append(qty).append("x ").append(itemComposition.getName());
                loot.append(" (").append(QuantityFormatter.quantityToStackSize(total)).append("gp)");
                if (itemStack.hasNext()) {
                    loot.append("<br>");
                }
            }
        }

        diaryManager.addDiaryItem(DiaryEvent.LOOT, loot.toString(), panel::refresh);
    }

    private void handleXpChange(String skillName, int newXP) {
        Integer previousXP = currentXP.get(skillName);
        if (previousXP == null || previousXP == 0)
        {
            currentXP.put(skillName, newXP);
            return;
        }

        int previousLevel = Experience.getLevelForXp(previousXP);
        int newLevel = Experience.getLevelForXp(newXP);

        if (previousLevel != newLevel)
        {
            currentXP.put(skillName, newXP);

            // Certain activities can multilevel, check if any of the levels are valid for the message.
            for (int level = previousLevel + 1; level <= newLevel; level++)
            {
                if (newLevel % Integer.parseInt(config.levelInterval().toString()) == 0
                        || newLevel == 99)
                {
                    String details = "Levelled up " + skillName + " to " + newLevel;
                    diaryManager.addDiaryItem(DiaryEvent.LEVEL, details, panel::refresh);
                }
            }
        }

        if (config.expInterval() > 0) {
            int expInterval = config.expInterval() * 1000000;
            int oldMilestone = previousXP / expInterval;
            int newMilestone = newXP / expInterval;
            if (oldMilestone < newMilestone) {
                String details = "Achieved " + newMilestone + " million experience in " + skillName;
                diaryManager.addDiaryItem(DiaryEvent.EXPERIENCE, details, panel::refresh);
            }
        }
    }

    static String parseCombatAchievementWidget(final String text)
    {
        final Matcher m = COMBAT_ACHIEVEMENTS_PATTERN.matcher(text);
        if (m.find())
        {
            return m.group("task").replaceAll("[:?]", "");
        }
        return "";
    }

    static String parseQuestCompletedWidget(final String text)
    {
        // "You have completed The Corsair Curse!"
        final Matcher questMatch1 = QUEST_PATTERN_1.matcher(text);
        // "'One Small Favour' completed!"
        final Matcher questMatch2 = QUEST_PATTERN_2.matcher(text);
        final Matcher questMatchFinal = questMatch1.matches() ? questMatch1 : questMatch2;
        if (!questMatchFinal.matches())
        {
            return "Quest not found";
        }

        String quest = questMatchFinal.group("quest");
        String verb = questMatchFinal.group("verb") != null ? questMatchFinal.group("verb") : "";

        if (verb.contains("kind of"))
        {
            quest += " partial completion";
        }
        else if (verb.contains("completely"))
        {
            quest += " II";
        }

        if (RFD_TAGS.stream().anyMatch((quest + verb)::contains))
        {
            quest = "Recipe for Disaster - " + quest;
        }

        if (WORD_QUEST_IN_NAME_TAGS.stream().anyMatch(quest::contains))
        {
            quest += " Quest";
        }

        return quest;
    }

    private String getPlayerName() {
        String name = null;
        if (client.getLocalPlayer() != null) {
            name = client.getLocalPlayer().getName();
            RuneScapeProfileType profileType = RuneScapeProfileType.getCurrent(client);
            if (profileType != RuneScapeProfileType.STANDARD) {
                name = name + "-" + Text.titleCase(profileType);
            }
        }
        return name;
    }

    private boolean validInterval(String value, String interval) {
        return Integer.parseInt(value) % Integer.parseInt(interval) == 0;
    }

    private static Collection<ItemStack> stack(Collection<ItemStack> items) {
        List<ItemStack> list = new ArrayList();
        Iterator var2 = items.iterator();

        while(var2.hasNext()) {
            ItemStack item = (ItemStack)var2.next();
            int quantity = 0;
            Iterator var5 = list.iterator();

            while(var5.hasNext()) {
                ItemStack i = (ItemStack)var5.next();
                if (i.getId() == item.getId()) {
                    quantity = i.getQuantity();
                    list.remove(i);
                    break;
                }
            }

            if (quantity > 0) {
                list.add(new ItemStack(item.getId(), item.getQuantity() + quantity, item.getLocation()));
            } else {
                list.add(item);
            }
        }

        return list;
    }

    int getClueNumber()
    {
        return clueNumber;
    }

    String getClueType()
    {
        return clueType;
    }

    private boolean isInsideGauntlet()
    {
        return this.client.isInInstancedRegion()
                && this.client.getMapRegions().length > 0
                && (this.client.getMapRegions()[0] == GAUNTLET_REGION
                || this.client.getMapRegions()[0] == CORRUPTED_GAUNTLET_REGION);
    }
}