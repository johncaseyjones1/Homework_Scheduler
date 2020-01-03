package com.example.homework_scheduler.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.util.LocaleData;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.homework_scheduler.R;
import com.example.homework_scheduler.calendar_helpers.Local_Data;
import com.example.homework_scheduler.model.cEvent;
import com.example.homework_scheduler.view.questions.Failure_Screen;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.model.*;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

/**
 * An asynchronous task that handles the Google Calendar API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */

/**
 * Created by miguel on 5/29/15.
 */

public class ApiAsyncTask extends AsyncTask<Void, Void, Void> {
    private MainActivity mActivity;
    Local_Data ld = Local_Data.getInstance();

    /**
     * Constructor.
     * @param activity MainActivity that spawned this task.
     */
    ApiAsyncTask(MainActivity activity) {
        this.mActivity = activity;
    }

    /**
     * Background task to call Google Calendar API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        Vector<String> keysToAddToLocalList = new Vector<>();
        if(ld.deleteEvent == 1)
        {
            try {
                mActivity.mService.events().delete("primary", ld.eventIDToDelete).execute();
            } catch (IOException ioe) {

            }
        }

        if (ld.addEvent == 1) {
            ld.notEnoughTime = false;
            //This is what will happen if we are adding an event and not fetching the events
            createcEvents();

            if (ld.notEnoughTime == true)
            {
                return null;
            }

            //take the array of cEvents and add them to the google calendar
            for (cEvent cevent : ld.assignmentcEvents) {
                Event event = new Event()
                        .setSummary(cevent.title);

                System.out.println("adding event with starttime: " + cevent.startLDT + "and end time " + cevent.endLDT);
                DateTime startDateTime = new DateTime(cevent.startLDT.toString() + ":00-07:00");
                EventDateTime start = new EventDateTime()
                        .setDateTime(startDateTime)
                        .setTimeZone("America/Los_Angeles");
                event.setStart(start);

                DateTime endDateTime = new DateTime(cevent.endLDT.toString() + ":00-07:00");
                EventDateTime end = new EventDateTime()
                        .setDateTime(endDateTime)
                        .setTimeZone("America/Los_Angeles");
                event.setEnd(end);

                String calendarId = "primary";

                String currentID;
                UUID uuid = UUID.randomUUID();
                currentID = uuid.toString();

                System.out.println(currentID);

                currentID = currentID.replace('-','v');

                cevent.eventID = currentID;

                event.setId(currentID);
                try {
                    event = mActivity.mService.events().insert(calendarId, event).execute();
                    keysToAddToLocalList.add(currentID.toString());
                } catch (Exception e) {
                    System.out.println("FAILED TO ADD EVENT! " + e.getMessage());
                }
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            //get all ids already in the shared preferences and add to list that will be added
            System.out.println("keys");
            String json = prefs.getString(ld.list_ids_id, null);
            if (json != null) {
                try {
                    JSONArray a = new JSONArray(json);
                    System.out.println("a is of length: " + a.length());
                    for (int i = 0; i < a.length(); i++)
                    {
                        String id = a.optString(i);
                        keysToAddToLocalList.add(id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //put full list in shared preferences
            JSONArray a = new JSONArray();
            for (int i = 0; i < keysToAddToLocalList.size(); i++) {
                a.put(keysToAddToLocalList.elementAt(i));
            }
            if (!keysToAddToLocalList.isEmpty()) {
                editor.putString(ld.list_ids_id, a.toString());
            } else {
                editor.putString(ld.list_ids_id, null);
            }
            editor.apply();


            //Reset addEvent to 0 and clear assignmentcEvents.
            ld.assignmentcEvents.clear();
            //ld.addEvent = 0;
        } else {
            try {
                mActivity.clearResultsText();
                mActivity.updateResultsText(getDataFromApi());

            } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
                mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                        availabilityException.getConnectionStatusCode());

            } catch (UserRecoverableAuthIOException userRecoverableException) {
                mActivity.startActivityForResult(
                        userRecoverableException.getIntent(),
                        MainActivity.REQUEST_AUTHORIZATION);

            } catch (IOException e) {
                mActivity.updateStatus("The following error occurred: " +
                        e.getMessage());
            }
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void input) {
        if (ld.deleteEvent == 1)
        {
            ld.deleteEvent = 0;
            FragmentManager fm = mActivity.getSupportFragmentManager();
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            return;
        }

        mActivity.lb.setAlpha(0.0f);
        ld.firstTime = 0;
        //mActivity.fab.setAlpha(1.0f);
        mActivity.optionsButton.setAlpha(1.0f);
        if (ld.cEventsForHomeScreen.size() == 0)
        {
            mActivity.ohno.setAlpha(1.0f);
            mActivity.topline.setAlpha(0.0f);
        }
        else {
            //mActivity.topline.setAlpha(1.0f);
            mActivity.ohno.setAlpha(0.0f);
        }
        if (ld.addEvent == 1 && ld.notEnoughTime == false) {
            ld.addEvent = 0;
            FragmentManager fm = mActivity.getSupportFragmentManager();
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            mActivity.refreshResults();
            Fragment frag = new Success_Screen(mActivity);
            FragmentTransaction fragTran = fm.beginTransaction();
            //fragTran.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            fragTran.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
            fragTran.replace(R.id.success, frag);
            fragTran.addToBackStack(null);
            fragTran.commit();
        }
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
        // List the next 10 events from the primary calendar.
        Vector<Event> allEvents = new Vector<>();
        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime sixMonths = new DateTime(LocalDateTime.now().plusMonths(6).toString());
        List<String> eventStrings = new ArrayList<String>();
        Events events = mActivity.mService.events().list("primary")
                .setMaxResults(1000)
                .setTimeMax(sixMonths)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        ld.calendarEvents.clear();
        ld.allcEvents.clear();
        allEvents.clear();
        for (Event event : items) {
            allEvents.add(event);
            DateTime start = event.getStart().getDateTime();
            DateTime end = event.getEnd().getDateTime();
            if (start == null) {
                // All-day events don't have start times, so just use
                // the start date.
                start = event.getStart().getDate();
            }

            ld.allcEvents.add(calendarEventTocEvent(String.format("%s (%s) - (%s)", event.getSummary(), start, end), event.getId()));
            ld.calendarEvents.add(
                    String.format("%s (%s) - (%s)", event.getSummary(), start, end));
            eventStrings.add(
                    String.format("%s (%s) - (%s)", event.getSummary(), start, end));
        }

        mActivity.yeet.clear();
        ld.cEventsForHomeScreen.clear();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext());
        String json = prefs.getString(ld.list_ids_id, null);
        Vector<String> eventIDs = new Vector<>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++)
                {
                    String id = a.optString(i);
                    eventIDs.add(id);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < allEvents.size(); ++i)
        {
            for (int j = 0; j < eventIDs.size(); ++j)
            {
                //System.out.println("we are comparing " + eventIDs.elementAt(j) + " and " + allEvents.elementAt(i).getId());
                if (eventIDs.elementAt(j).equals(allEvents.elementAt(i).getId()))
                {
                    //System.out.println("the match was " + eventIDs.elementAt(j) + " and " + allEvents.elementAt(i).getId());
                    //System.out.println("but the allEvent title was " + allEvents.elementAt(i).getSummary());
                    //System.out.println("we are adding " + ld.allcEvents.elementAt(i).title + " to homescreen");
                    ld.cEventsForHomeScreen.add(ld.allcEvents.elementAt(i));
                }
            }
        }
        mActivity.yeet.clear();
        System.out.println(ld.cEventsForHomeScreen.size());
        mActivity.yeet.addAll(ld.cEventsForHomeScreen);
        //ld.addEvent = 0;
        return eventStrings;
    }

    private cEvent calendarEventTocEvent(String eventString, String eventID)
    {
        //System.out.println(eventString);
        cEvent theEvent = new cEvent();
        Integer titleLength = 0;
        Integer startHere = 0;
        boolean pastTitle = false;
        String startYear = "";
        String startMonth = "";
        String startDay = "";
        String startHour = "";
        String startMinutes = "";
        String endYear = "";
        String endMonth = "";
        String endDay = "";
        String endHour = "";
        String endMinutes = "";
        for (int i = 0; i < eventString.length(); ++i) {
            if (eventString.charAt(i) == '(' && eventString.charAt(i + 1) == '2')
            {
                pastTitle = true;
                break;
            }
            theEvent.title += eventString.charAt(i);
            titleLength++;
        }

        //parse start year
        for (int i = 0; i < 4; ++i)
        {
            startYear += eventString.charAt(titleLength + 1 + i);
        }
        theEvent.startYear = Integer.parseInt(startYear);
        startHere = titleLength + 6;

        //parse start month
        for (int i = 0; i < 2; ++i)
        {
            startMonth += eventString.charAt(startHere + i);
        }
        //System.out.println(startMonth);
        theEvent.startMonth = Integer.parseInt(startMonth);
        startHere = startHere + 3;

        //parse start day
        for (int i = 0; i < 2; ++i)
        {
            startDay += eventString.charAt(startHere + i);
        }
        //System.out.println(startDay);
        theEvent.startDay = Integer.parseInt(startDay);
        startHere = startHere + 3;

        //parse start hour
        for (int i = 0; i < 2; ++i)
        {
            startHour += eventString.charAt(startHere + i);
        }
        theEvent.startHour = Integer.parseInt(startHour);
       // System.out.println("hour " + startHour);
        startHere = startHere + 3;

        //parse start minutes
        for (int i = 0; i < 2; ++i)
        {
            startMinutes += eventString.charAt(startHere + i);
        }
        theEvent.startMinutes = Integer.parseInt(startMinutes);
        //System.out.println("minutes " + startMinutes);
        //startHere = startHere + 20;

        //System.out.println("start here is at " + startHere);

        String startTime = "";
        startTime = startHour + startMinutes;
        theEvent.startTime = Integer.parseInt(startTime);

        //System.out.println("Hopefully 20: " + eventString.charAt(startHere) + eventString.charAt(startHere + 1));

        boolean nextYearFound = false;
        while (!nextYearFound)
        {
            if (eventString.charAt(startHere) == '2' && eventString.charAt(startHere - 1) == '(')
                nextYearFound = true;
            else
                startHere++;
        }

        //parse end year
        for (int i = 0; i < 4; ++i)
        {
            endYear += eventString.charAt(startHere + i);
        }
        theEvent.endYear = Integer.parseInt(endYear);
        startHere = startHere + 5;

        //parse end month
        for (int i = 0; i < 2; ++i)
        {
            endMonth += eventString.charAt(startHere + i);
        }
        //System.out.println(endMonth);
        theEvent.endMonth = Integer.parseInt(endMonth);
        startHere = startHere + 3;

        //parse end day
        for (int i = 0; i < 2; ++i)
        {
            endDay += eventString.charAt(startHere + i);
        }
        //System.out.println(endDay);
        theEvent.endDay = Integer.parseInt(endDay);
        startHere = startHere + 3;

        //parse end hour
        for (int i = 0; i < 2; ++i)
        {
            endHour += eventString.charAt(startHere + i);
        }
        theEvent.endHour = Integer.parseInt(endHour);
        //System.out.println("hour " + endHour);
        startHere = startHere + 3;

        //parse end minutes
        for (int i = 0; i < 2; ++i)
        {
            endMinutes += eventString.charAt(startHere + i);
        }
        theEvent.endMinutes = Integer.parseInt(endMinutes);
        //System.out.println("minutes " + endMinutes);
        startHere = startHere + 20;

        String endTime = "";
        endTime = endHour + endMinutes;
        theEvent.endTime = Integer.parseInt(endTime);

        theEvent.startLDT = LocalDateTime.of(theEvent.startYear, theEvent.startMonth, theEvent.startDay, theEvent.startHour, theEvent.startMinutes);
        theEvent.endLDT = LocalDateTime.of(theEvent.endYear, theEvent.endMonth, theEvent.endDay, theEvent.endHour, theEvent.endMinutes);

        theEvent.eventID = eventID;
        /*System.out.println(theEvent.title);
        System.out.println(theEvent.startYear);
        System.out.println(theEvent.startMonth);
        System.out.println(theEvent.startDay);
        System.out.println(theEvent.startTime);
        System.out.println(theEvent.endYear);
        System.out.println(theEvent.endMonth);
        System.out.println(theEvent.endDay);
        System.out.println(theEvent.endTime);*/

        return theEvent;
    }

    private List<String> addEventsToCalendar() throws IOException {
        //calculate when the event needs to go
        //mActivity.mService.events().insert("primary",);
        return new ArrayList<>();
    }

    boolean extraEvent = false;

    private Vector<cEvent> createcEvents() {
        Vector<cEvent> theEvents = new Vector<>();
        int numEvents = 0;
        Float eventTime = new Float(0);
        Float extraEventTime = new Float(0);
        if (ld.assignmentLength > ld.maxTimeOneSitting)
        {
            numEvents = ld.assignmentLength.intValue() / ld.maxTimeOneSitting.intValue() + ld.assignmentLength.intValue() % ld.maxTimeOneSitting.intValue();
            if (ld.assignmentLength.intValue() % ld.maxTimeOneSitting.intValue() > 0)
            {
                extraEventTime = ld.assignmentLength - ld.maxTimeOneSitting * (numEvents - 1);
                System.out.println("extra event time is " + extraEventTime);
                extraEvent = true;
            }
            System.out.println("numEvents is: " + numEvents);
            eventTime = ld.maxTimeOneSitting;
        } else {
            eventTime = ld.assignmentLength;
            numEvents = 1;
        }

        int extraEventTimeInMinutes = (int) Math.round(extraEventTime * 60);
        int eventTimeInMinutes = (int) Math.round(eventTime * 60);
        int assignmentLengthInMinutes = (int) Math.round(ld.assignmentLength * 60);

        boolean enoughEvents = false;
        int numEventsAdded = 0;
        int hwTimeAdded = 0;

        LocalDateTime mustBeBefore = ld.dueDate.minusDays(ld.daysEarly).plusDays(1);
        Vector<cEvent> eventsOnCorrectDay = new Vector<>();
        while(!enoughEvents) {
            //Create a cEvent from back to start
            //first only one assignment a day
            eventsOnCorrectDay.clear();
            //grab all events on day to maybe schedule
            for (cEvent cevent : ld.allcEvents)
            {
                if (mustBeBefore.getDayOfMonth() != 1) {
                    if (cevent.startLDT.getDayOfMonth() == mustBeBefore.minusDays(1).getDayOfMonth() && cevent.startLDT.getMonth() == mustBeBefore.getMonth()) {
                        eventsOnCorrectDay.add(cevent);
                    }
                } else {
                    if (cevent.startLDT.getDayOfMonth() == mustBeBefore.minusDays(1).getDayOfMonth() && cevent.startLDT.getMonth() == mustBeBefore.minusDays(1).getMonth())
                    {
                        eventsOnCorrectDay.add(cevent);
                    }
                }
            }
            //try to add it in during homework time and avoiding events
            int startHour = 0;
            LocalTime startTime;
            boolean cantAdd = false;
            cEvent cEventToAdd = new cEvent();
            cEventToAdd.startLDT = null;
            int highestTime = 0;

            for (int j = 30; j <= (int) Math.round(ld.maxTimeOneSitting * 60); j = j + 30)
            {
                if (assignmentLengthInMinutes - hwTimeAdded > 30 && j == 30)
                    j = 60;

                if (highestTime + hwTimeAdded >= assignmentLengthInMinutes)
                {
                    System.out.println("broke hurrrrrrrrrrrrrrrrrrrrrrrrr");
                    break;
                }
                for (LocalTime i = ld.startHomeworkTime; i.isBefore(ld.endHomeworkTime); i = i.plusMinutes(30))
                {
                    cantAdd = false;
                    startHour = i.getHour();
                    startTime = i;
                    cEvent eventWithDMY = null;
                    for (cEvent cevent: eventsOnCorrectDay)
                    {
                        if (startTime.equals(cevent.startLDT.toLocalTime()))
                            cantAdd = true;

                        if (startTime.isBefore(ld.startHomeworkTime) || startTime.plusMinutes(j).isAfter(ld.endHomeworkTime) || startTime.plusMinutes(j).isBefore(ld.startHomeworkTime) || startTime.isAfter(ld.endHomeworkTime))
                            cantAdd = true;

                        if (startTime.isBefore(cevent.startLDT.toLocalTime()) && !(startTime.plusMinutes(j).isBefore(cevent.startLDT.toLocalTime()) || startTime.plusMinutes(j).equals(cevent.startLDT.toLocalTime())))
                            cantAdd = true;

                        if(startTime.isAfter(cevent.startLDT.toLocalTime()) && !(startTime.isAfter(cevent.endLDT.toLocalTime())))
                            cantAdd = true;

                        eventWithDMY = cevent;
                    }
                    if (!cantAdd)
                    {
                        System.out.println("We can add it today!!!!");

                        //actually create a cEvent and add it to ld.assignmentEvents

                        cEventToAdd.startLDT = LocalDateTime.of(mustBeBefore.minusDays(1).getYear(), mustBeBefore.minusDays(1).getMonth(), mustBeBefore.minusDays(1).getDayOfMonth(), i.getHour(), i.getMinute(), i.getSecond());
                        cEventToAdd.endLDT = LocalDateTime.of(mustBeBefore.minusDays(1).getYear(), mustBeBefore.minusDays(1).getMonth(), mustBeBefore.minusDays(1).getDayOfMonth(), i.plusMinutes(j).getHour(), i.plusMinutes(j).getMinute(), i.plusMinutes(j).getSecond());
                        cEventToAdd.title = ld.assignmentTitle;//ld.assignmentcEvents.add(cEventToAdd);
                        //theEvents.add(cEventToAdd);
                        highestTime = j;
                        //numEventsAdded++;
                        break;
                    } else {
                        System.out.println("We cant add it today :((((((((((");
                    }
                }
            }
            if (cEventToAdd.startLDT != null)
            {
                ld.assignmentcEvents.add(cEventToAdd);
                theEvents.add(cEventToAdd);
                hwTimeAdded = hwTimeAdded + highestTime;
                numEventsAdded++;
            }

            //check if enough events have been made yet
            System.out.println("hwTimeAdded is " + hwTimeAdded + " and assignmentLengthInMinutes is " + assignmentLengthInMinutes);
            if (hwTimeAdded >= assignmentLengthInMinutes)
            {
                //added enough, break
                enoughEvents = true;
                //break;
            }
            //not enough, make sure we havent gone to the past yet
            if (mustBeBefore.isBefore(LocalDateTime.now().plusDays(1)))
            {
                System.out.println("TOO EARLY!!!!!!!!!");
                ld.assignmentcEvents.clear();
                ld.addEvent = 0;
                //go to fragment saying there's not enough time
                ld.notEnoughTime = true;
                FragmentManager fm = mActivity.getSupportFragmentManager();
                Fragment frag = new Failure_Screen(mActivity);
                FragmentTransaction fragTran = fm.beginTransaction();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                mActivity.refreshResults();
                //fragTran.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                fragTran.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                fragTran.replace(R.id.oh_no, frag);
                fragTran.addToBackStack(null);
                fragTran.commit();
                break;
            }


            mustBeBefore = mustBeBefore.minusDays(1);

            System.out.println("num of grabbed events " + eventsOnCorrectDay.size());
            for (cEvent ce : eventsOnCorrectDay)
            {
                System.out.println("Title is " + ce.title + " with day on " + ce.startDay + " and month " + ce.startMonth);
            }

            //if fails, double up but with breaks
            //if fails, cram them in
            //if fails, notify to either increase homework time or shorten how long it'll take
        }

        System.out.println("We added " + numEventsAdded + " events.");
        for (int i = 0; i < theEvents.size(); ++i)
        {
            System.out.println(theEvents.elementAt(i).title + " (" + theEvents.elementAt(i).startLDT.toString() + ") - (" + theEvents.elementAt(i).endLDT.toString() + ")");
        }
        return theEvents;
    }

}