package com.accountdiary;

import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.util.ImageUtil;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.RuneLite.RUNELITE_DIR;
import static net.runelite.http.api.RuneLiteAPI.GSON;

@Slf4j
@Singleton
public class DiaryManager {
    private static final File DIARY_DIR = new File(RUNELITE_DIR, "accountdiary");

    @Inject
    private DateManager dateManager;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private DrawManager drawManager;

    @Inject
    private Client client;

    @Inject
    private AccountDiaryConfig config;

    @Getter(AccessLevel.PACKAGE)
    private Hashtable<String, ArrayList<DiaryItem>> diaryItems = new Hashtable<>();

    private File playerDir = new File("C:\\Users\\qi\\.runelite\\accountdiary\\fletching267");
    private File playerJSON = new File("C:\\Users\\qi\\.runelite\\accountdiary\\fletching267\\diary.json");

    public void initDiary() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(playerJSON));
            Type type = new TypeToken<Hashtable<String, ArrayList<DiaryItem>>>() {}.getType();
            diaryItems = GSON.fromJson(reader, type);
            reader.close();
        } catch (NullPointerException|IOException e) {
            log.error("Failed to initialize diary: " + e.getMessage());
        }
    }

    public void updateFilePaths(String playerName) {
        if (playerName == null) return;
        playerDir = new File(DIARY_DIR, playerName);
        playerJSON = new File(playerDir, "diary.json");
    }

    public void addDiaryItem(DiaryEvent event, String details, Runnable runnable) {
        String today = dateManager.getTodayDate();
        String screenshotFile = null;
        screenshotFile = takeScreenshot(event.getName(), today);

        DiaryItem diaryItem = new DiaryItem(event, details, dateManager.getTime(), screenshotFile);
        if (!diaryItems.containsKey(today)) {
            diaryItems.put(today, new ArrayList<>());
        }
        diaryItems.get(today).add(diaryItem);

        try {
            FileWriter fw = new FileWriter(playerJSON, false);
            GSON.toJson(diaryItems, fw);
            fw.close();
        } catch(IOException e) {
            log.error("Error writing to diary: " + e.getMessage());
        }

        if (config.apiConnections()) {
            //apiHandler.postRequest(diaryItem);
        }

        // wait for screenshot
        executor.schedule(runnable, 3, TimeUnit.SECONDS);
    }

    private String takeScreenshot(String eventName, String date) {
        String fileName = null;
        int i = 0;
        while (new File(playerDir, date + File.separator + fileName).exists()) {
            fileName = eventName + String.format("(%d)", i++) + ".png";
        }

        File screenshotFile = new File(playerDir, date + File.separator + fileName);

        drawManager.requestNextFrameListener((img) -> {
            executor.submit(() -> {
                BufferedImage screenshot = ImageUtil.bufferedImageFromImage(img);
                this.saveScreenshot(screenshot, screenshotFile);
            });
        });

        return fileName;
    }

    private void saveScreenshot(BufferedImage screenshot, File screenshotFile) {
        if (this.client.getGameState() == GameState.LOGIN_SCREEN) {
            log.debug("Login screenshot prevented");
        } else {
            try {
                screenshotFile.getParentFile().mkdirs();
                ImageIO.write(screenshot, "PNG", screenshotFile);
            } catch (IOException e) {
                log.info("error writing screenshot", e);
            }
        }
    }

    public Image getScreenshot(String date, DiaryItem diaryItem) {
        if (!config.previewScreenshot()) return null;

        File screenshotFile = new File(playerDir, date + File.separator + diaryItem.getScreenshotFile());
        try {
            return ImageIO.read(screenshotFile);
        } catch (IOException e) {
            log.info("Failed to get screenshot " + diaryItem.getScreenshotFile() + ": " + e.getMessage());
            return ImageUtil.loadImageResource(AccountDiaryPlugin.class, "watch.png");
        }
    }

    public File getScreenshotFile(String date, DiaryItem diaryItem) {
        if (!config.openScreenshot()) return null;

        return new File(playerDir, date + File.separator + diaryItem.getScreenshotFile());
    }
}
