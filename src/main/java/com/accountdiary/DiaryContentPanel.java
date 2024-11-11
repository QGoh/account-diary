package com.accountdiary;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;

import javax.inject.Inject;
import javax.swing.*;

@Slf4j
class DiaryContentPanel extends JPanel
{
    private static final Color HOVER_COLOR = ColorScheme.DARKER_GRAY_HOVER_COLOR;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Inject
    private AccountDiaryPlugin plugin;

    @Inject
    private DiaryManager diaryManager;

    private String date;

    private final ArrayList<DiaryItem> diaryItems;

    DiaryContentPanel(DiaryManager diaryManager, String date)
    {
        this.diaryManager = diaryManager;
        this.date = date;
        diaryItems = new ArrayList<>();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
    }

    public void refreshPanel() {
        try {
            diaryManager.getDiaryItems().get(date).forEach(
                    (diaryItem) -> {
                        if (!diaryItems.contains(diaryItem)) {
                            diaryItems.add(diaryItem);
                            add(new DiaryItemPanel(diaryManager, diaryItem, date));
                        }
                    }
            );
        } catch (NullPointerException ignored) {

        }
        repaint();
        revalidate();
    }

    public void setDate(String newDate) {
        date = newDate;
        removeAll();
        diaryItems.clear();
        refreshPanel();
    }
}