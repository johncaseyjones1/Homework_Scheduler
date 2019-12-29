package com.example.homework_scheduler.model;

import java.time.LocalDateTime;

public class cEvent {
    public Integer startHour = 00;
    public Integer startMinutes = 00;
    public Integer startTime = 0000;
    public Integer startDay = 1;
    public Integer endDay = 1;
    public Integer startMonth = 1;
    public Integer endMonth = 1;
    public Integer startYear = 2000;
    public Integer endYear = 2000;
    public Integer endHour = 00;
    public Integer endMinutes = 00;
    public Integer endTime = 0000;
    public String title = "";
    public String subtitle = "";
    public LocalDateTime startLDT;
    public LocalDateTime endLDT;
    public String eventID;
}
