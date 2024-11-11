package com.accountdiary;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateManager {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter YEAR_MONTH_DAY = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter YEAR_MONTH_DAY_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Inject
    private AccountDiaryConfig config;

    public String getTodayDate() {
        return LocalDate.now().format(YEAR_MONTH_DAY);
    }

    public String getActiveDate(int days) {
        LocalDate activeDate = LocalDate.parse(config.activeDate(), YEAR_MONTH_DAY);
        String newDate = activeDate.plusDays(days).format(YEAR_MONTH_DAY);
        config.setActiveDate(newDate);
        return newDate;
    }

    public void setActiveDate(String newDate) {
        config.setActiveDate(newDate);
    }

    public String getTime() {
        return LocalDateTime.now().format(TIME);
    }
}
