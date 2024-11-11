package com.accountdiary;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

@Slf4j
class DiaryItemPanel extends JPanel {
    private static final Color HOVER_COLOR = new Color(20, 20, 20);
    private static final Color BACKGROUND_COLOR = ColorScheme.DARKER_GRAY_COLOR;

    private final JLabel preview;
    private final JLabel eventIcon;
    private final JPanel textContainer;
    private final JPanel box;
    private final Desktop desktop;
    private boolean highlighted;

    DiaryItemPanel(DiaryManager diaryManager, DiaryItem diaryItem, String date) {

        desktop = Desktop.getDesktop();
        highlighted = false;

        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(0,8));
        setBorder(new CompoundBorder(new MatteBorder(5, 0, 5, 0, ColorScheme.DARK_GRAY_COLOR),
                new EmptyBorder(8, 8, 8, 8)));

        box = new JPanel();
        box.setLayout(new BorderLayout());
        box.setBackground(BACKGROUND_COLOR);

        textContainer = new JPanel();
        textContainer.setLayout(new BorderLayout());
        textContainer.setBorder(new EmptyBorder(3, 0, 0, 0));
        textContainer.setBackground(BACKGROUND_COLOR);

        JLabel eventLabel = new JLabel("<html>" + diaryItem.getEvent().getName() + " - " + diaryItem.getTime() + "</html>");
        eventLabel.setForeground(Color.GREEN);
        eventLabel.setFont(FontManager.getRunescapeSmallFont());

        JLabel eventDetailsLabel = new JLabel("<html>" + diaryItem.getEventDetails() + "</html>");
        eventDetailsLabel.setForeground(Color.WHITE);
        eventDetailsLabel.setFont(FontManager.getRunescapeSmallFont());

        JLabel timeLabel = new JLabel("<html>" + diaryItem.getTime() + "</html>");
        timeLabel.setForeground(Color.GRAY);
        timeLabel.setFont(FontManager.getRunescapeSmallFont());

        textContainer.add(eventLabel, BorderLayout.NORTH);
        textContainer.add(eventDetailsLabel, BorderLayout.CENTER);

        eventIcon = new JLabel(new ImageIcon(diaryItem.getEvent().getIcon().getScaledInstance(15, -1, Image.SCALE_DEFAULT)));
        eventIcon.setVerticalAlignment(SwingConstants.TOP);

        box.add(textContainer, BorderLayout.LINE_START);
        box.add(eventIcon, BorderLayout.LINE_END);

        add(box, BorderLayout.NORTH);

        Image screenshot = diaryManager.getScreenshot(date, diaryItem);
        if (screenshot == null) {
            preview = null;
        } else {
            preview = new JLabel(new ImageIcon(screenshot.getScaledInstance(185, -1, Image.SCALE_DEFAULT)));
            add(preview, BorderLayout.CENTER);
        }

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    File screenshotFile = diaryManager.getScreenshotFile(date, diaryItem);
                    if (screenshotFile == null) return;
                    desktop.open(diaryManager.getScreenshotFile(date, diaryItem));
                } catch (IOException err) {
                    log.info(err.getMessage());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                toggleHighlighted();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                toggleHighlighted();
            }
        });
    }

    private void toggleHighlighted()
    {
        setBackground(highlighted ? BACKGROUND_COLOR : HOVER_COLOR);
        textContainer.setBackground(highlighted ? BACKGROUND_COLOR : HOVER_COLOR);
        box.setBackground(highlighted ? BACKGROUND_COLOR : HOVER_COLOR);
        setCursor(new Cursor(highlighted ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
        highlighted = highlighted ? false : true;
    }
}