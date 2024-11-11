package com.accountdiary;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class DiaryItem {
    private DiaryEvent event;
    private String eventDetails;
    private String time;
    private String screenshotFile;

    public DiaryItem(DiaryEvent event, String eventDetails, String time, String screenshotFile) {
        this.event = event;
        this.eventDetails = eventDetails;
        this.time = time;
        this.screenshotFile = screenshotFile;
    }

}
