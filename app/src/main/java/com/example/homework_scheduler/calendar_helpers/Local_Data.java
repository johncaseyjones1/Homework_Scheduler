package com.example.homework_scheduler.calendar_helpers;

import com.example.homework_scheduler.model.cEvent;
import com.google.api.client.util.DateTime;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Vector;

public class Local_Data {
    // static variable single_instance of type Singleton
    private static Local_Data single_instance = null;

    // variable of type String
    public Vector<String> calendarEvents;

    public Vector<cEvent> allcEvents = new Vector<>();
    public Vector<cEvent> assignmentcEvents = new Vector<>();

    public Vector<cEvent> cEventsForHomeScreen = new Vector<>();

    public String eventIDToDelete;

    public Integer addEvent = 0;
    public Integer deleteEvent = 0;
    public LocalTime startHomeworkTime = LocalTime.of(16,00,00,00);
    public LocalTime endHomeworkTime = LocalTime.of(21,00,00,00);
    public Float maxTimeOneSitting = new Float(2);
    public Integer daysEarly = 1;
    public Integer startTimeInt = 16;
    public Integer endTimeInt = 20;
    public Integer maxTimeInt = 3;
    // private constructor restricted to this class itself
    private Local_Data()
    {
        calendarEvents = new Vector<>();
    }


    //data for a new assignment
    public Float assignmentLength = new Float(11.0);
    public String assignmentTitle = "English Paper";
    public LocalDateTime dueDate = LocalDateTime.of(2020, Month.FEBRUARY, 16, 23, 00); // ("2020-02-17T23:00:00-07:00");


    public String list_ids_id = "com.example.hws.list_ids_id";
    public String startTime_id = "com.example.start_time_id";
    public String startTimePosit_id = "com.example.start_time_posit_id";
    public String endTime_id = "com.example.end_time_id";
    public String endTimePosit_id = "com.example.end_time_posit_id";
    public String maxTime_id = "com.example.max_time_id";
    public String maxTimePosit_id = "com.example.max_time_posit_id";
    public String daysBefore_id = "com.example.days_before_id";

    public boolean notEnoughTime = false;


    // static method to create instance of Singleton class
    public static Local_Data getInstance()
    {
        if (single_instance == null)
            single_instance = new Local_Data();

        return single_instance;
    }
}
