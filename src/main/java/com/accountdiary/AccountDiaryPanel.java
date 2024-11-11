package com.accountdiary;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Properties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.FontType;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import org.jdatepicker.*;

@Slf4j
class AccountDiaryPanel extends PluginPanel
{
    private static final ImageIcon ARROW_RIGHT_ICON = new ImageIcon(ImageUtil.loadImageResource(AccountDiaryPlugin.class, "arrow_right.png"));
    private static final ImageIcon ARROW_LEFT_ICON = new ImageIcon(ImageUtil.loadImageResource(AccountDiaryPlugin.class, "arrow_left.png"));
    private static final ImageIcon ARROW_RIGHT_HOVER_ICON = new ImageIcon(ImageUtil.loadImageResource(AccountDiaryPlugin.class, "arrow_right_hover.png"));
    private static final ImageIcon ARROW_LEFT_HOVER_ICON = new ImageIcon(ImageUtil.loadImageResource(AccountDiaryPlugin.class, "arrow_left_hover.png"));

    private final JPanel headerPanel;
    private final JDatePicker datePicker;
    private final JLabel leftArrow = new JLabel(ARROW_LEFT_ICON);
    private final JLabel rightArrow = new JLabel(ARROW_RIGHT_ICON);
    private final DiaryContentPanel diaryContentPanel;

    private final DateManager dateManager;

    AccountDiaryPanel(DiaryManager diaryManager, DateManager dateManager)
    {
        super(false);
        this.dateManager = dateManager;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        headerPanel.setFont(FontType.SMALL.getFont());

        // header
        ComponentColorDefaults.getInstance().setColor(ComponentColorDefaults.Key.FG_MONTH_SELECTOR, Color.WHITE);
        ComponentColorDefaults.getInstance().setColor(ComponentColorDefaults.Key.BG_MONTH_SELECTOR, ColorScheme.MEDIUM_GRAY_COLOR);

        // previous, next month buttons
        ComponentIconDefaults.getInstance().setNextMonthIconEnabled(ARROW_RIGHT_ICON);
        ComponentIconDefaults.getInstance().setPreviousMonthIconEnabled(ARROW_LEFT_ICON);

        // day of week labels
        ComponentColorDefaults.getInstance().setColor(ComponentColorDefaults.Key.BG_GRID_HEADER, ColorScheme.LIGHT_GRAY_COLOR);

        // grid
        ComponentColorDefaults.getInstance().setColor(ComponentColorDefaults.Key.BG_GRID, ColorScheme.DARK_GRAY_COLOR);
        ComponentColorDefaults.getInstance().setColor(ComponentColorDefaults.Key.FG_GRID_THIS_MONTH, Color.WHITE);
        ComponentColorDefaults.getInstance().setColor(ComponentColorDefaults.Key.FG_GRID_TODAY, Color.WHITE);

        // selected date
        ComponentColorDefaults.getInstance().setColor(ComponentColorDefaults.Key.BG_GRID_SELECTED, ColorScheme.BRAND_ORANGE);
        ComponentColorDefaults.getInstance().setColor(ComponentColorDefaults.Key.FG_GRID_SELECTED, Color.BLACK);
        ComponentColorDefaults.getInstance().setColor(ComponentColorDefaults.Key.FG_GRID_TODAY_SELECTED, Color.BLACK);
        ComponentColorDefaults.getInstance().setColor(ComponentColorDefaults.Key.BG_GRID_TODAY_SELECTED, ColorScheme.BRAND_ORANGE);

        // footer
        ComponentColorDefaults.getInstance().setColor(ComponentColorDefaults.Key.BG_TODAY_SELECTOR, ColorScheme.MEDIUM_GRAY_COLOR);
        ComponentColorDefaults.getInstance().setColor(ComponentColorDefaults.Key.FG_TODAY_SELECTOR_ENABLED, Color.WHITE);

        UtilDateModel model = new UtilDateModel();
        datePicker = new JDatePicker(model, "yyyy-MM-dd");
        datePicker.getModel().setSelected(true);
        datePicker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newDate = datePicker.getFormattedTextField().getText();
                diaryContentPanel.setDate(newDate);
                dateManager.setActiveDate(newDate);
            }
        });
        datePicker.getFormattedTextField().setHorizontalAlignment(SwingConstants.CENTER);
        datePicker.getFormattedTextField().setFont(FontManager.getRunescapeSmallFont());
        datePicker.getFormattedTextField().setBackground(ColorScheme.DARKER_GRAY_COLOR);
        datePicker.getFormattedTextField().setEnabled(false);
        setDatePickerDate(dateManager.getActiveDate(0).split("-"));

        leftArrow.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                leftArrow.setIcon(ARROW_LEFT_HOVER_ICON);
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                String newDate = dateManager.getActiveDate(-1);
                setDatePickerDate(newDate.split("-"));
                diaryContentPanel.setDate(newDate);
                leftArrow.setIcon(ARROW_LEFT_ICON);
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                leftArrow.setIcon(ARROW_LEFT_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                leftArrow.setIcon(ARROW_LEFT_ICON);
            }
        });

        rightArrow.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                rightArrow.setIcon(ARROW_RIGHT_HOVER_ICON);
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                String newDate = dateManager.getActiveDate(1);
                setDatePickerDate(newDate.split("-"));
                diaryContentPanel.setDate(newDate);
                rightArrow.setIcon(ARROW_RIGHT_ICON);
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                rightArrow.setIcon(ARROW_RIGHT_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                rightArrow.setIcon(ARROW_RIGHT_ICON);
            }
        });

        diaryContentPanel = new DiaryContentPanel(diaryManager, dateManager.getActiveDate(0));

        JPanel wrapped = new JPanel(new BorderLayout());
        wrapped.add(diaryContentPanel, BorderLayout.NORTH);
        wrapped.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JScrollPane scroller = new JScrollPane(wrapped);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.getVerticalScrollBar().setPreferredSize(new Dimension(16, 0));
        scroller.getVerticalScrollBar().setBorder(new EmptyBorder(0, 9, 0, 0));
        scroller.setBackground(ColorScheme.BRAND_ORANGE);
        scroller.setBorder(new EmptyBorder(0, 10, 0, 10));

        leftArrow.setBorder(new EmptyBorder(0, 30, 0, 10));
        rightArrow.setBorder(new EmptyBorder(0, 10, 0, 30));

        headerPanel.add(leftArrow);
        headerPanel.add(datePicker);
        headerPanel.add(rightArrow);

        add(headerPanel, BorderLayout.NORTH);
        add(scroller, BorderLayout.CENTER);
    }

    // [y, m, d]
    private void setDatePickerDate(String[] dateSplit) {
        datePicker.getModel().setDate(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]) - 1, Integer.parseInt(dateSplit[2]));
    }

    public void refresh() {
        diaryContentPanel.refreshPanel();
    }
}