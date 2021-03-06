package com.example.homework_scheduler.view;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.homework_scheduler.R;
import com.example.homework_scheduler.model.Local_Data;
import com.example.homework_scheduler.model.cEvent;
import com.example.homework_scheduler.view.MainActivity;
import com.example.homework_scheduler.view.Success_Screen;
import com.example.homework_scheduler.view.questions.Failure_Screen;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.model.*;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Console;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
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
            } catch (IOException ignored) {

            }
        }

        if (ld.addEvent == 1) {
            ld.notEnoughTime = false;
            //This is what will happen if we are adding an event and not fetching the events
            createcEvents();

            if (ld.notEnoughTime)
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
        mActivity.myToolbar.setAlpha(1.0f);
        ld.firstTime = 0;
        //mActivity.fab.setAlpha(1.0f);
        mActivity.optionsButton.setAlpha(1.0f);
        if (ld.cEventsForHomeScreen.size() == 0)
        {
            mActivity.ohno.setAlpha(1.0f);
            //mActivity.topline.setAlpha(0.0f);
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

            //ld.allcEvents.add(calendarEventTocEvent(String.format("%s (%s) - (%s)", event.getSummary(), start, end), event.getId()));
            ld.allcEvents.add(calendarEventTocEvent(event));
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

    private cEvent calendarEventTocEvent(Event event)
    {
        //System.out.println(eventString);
        cEvent theEvent = new cEvent();

        Date startDate = new Date(event.getStart().getDateTime().getValue());
        ZoneOffset startoffset = ZoneOffset.ofTotalSeconds(event.getStart().getDateTime().getTimeZoneShift() * 60);
        ZoneId startzone = ZoneId.ofOffset("UTC", startoffset);
        Instant startinstant = startDate.toInstant();
        LocalDateTime startLDT = LocalDateTime.ofInstant(startinstant, startzone);

        Date endDate = new Date(event.getEnd().getDateTime().getValue());
        ZoneOffset endoffset = ZoneOffset.ofTotalSeconds(event.getEnd().getDateTime().getTimeZoneShift() * 60);
        ZoneId endzone = ZoneId.ofOffset("UTC", endoffset);
        Instant endinstant = endDate.toInstant();
        LocalDateTime endLDT = LocalDateTime.ofInstant(endinstant, endzone);
        String title = event.getSummary();

        theEvent.startLDT = startLDT;
        theEvent.endLDT = endLDT;
        theEvent.title = title;
        theEvent.eventID = event.getId();

        return theEvent;
    }

    boolean extraEvent = false;

    private Vector<cEvent> createcEvents() {
        Vector<cEvent> theEvents = new Vector<>();
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
            LocalTime startTime;
            boolean cantAdd = false;
            cEvent cEventToAdd = new cEvent();
            cEventToAdd.startLDT = null;
            int highestTime = 0;

            for (int j = 30; j <= ld.maxTimeOneSitting * 60; j = j + 30)
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
                    startTime = i;
                    for (cEvent cevent: eventsOnCorrectDay)
                    {
                        if (startTime.equals(cevent.startLDT.toLocalTime())) {
                            System.out.println("error 1");
                            cantAdd = true;
                        }

                        if (startTime.isBefore(ld.startHomeworkTime) || startTime.plusMinutes(j).isAfter(ld.endHomeworkTime) || startTime.plusMinutes(j).isBefore(ld.startHomeworkTime) || startTime.isAfter(ld.endHomeworkTime)) {
                            System.out.println("error 2");
                            cantAdd = true;
                        }

                        if ((startTime.isBefore(cevent.startLDT.toLocalTime()) && !(startTime.plusMinutes(j).isBefore(cevent.startLDT.toLocalTime())) || startTime.plusMinutes(j).equals(cevent.startLDT.toLocalTime()))) {
                            System.out.println("error 3");
                            cantAdd = true;
                        }

                        if(startTime.isAfter(cevent.startLDT.toLocalTime()) && !(startTime.isAfter(cevent.endLDT.toLocalTime()))) {
                            System.out.println("error 4");
                            cantAdd = true;
                        }

                    }
                    if (!cantAdd)
                    {
                        System.out.println("We can add it today!!!!");

                        //actually create a cEvent and add it to ld.assignmentEvents

                        cEventToAdd.startLDT = LocalDateTime.of(mustBeBefore.minusDays(1).getYear(), mustBeBefore.minusDays(1).getMonth(), mustBeBefore.minusDays(1).getDayOfMonth(), i.getHour(), i.getMinute(), i.getSecond());
                        cEventToAdd.endLDT = LocalDateTime.of(mustBeBefore.minusDays(1).getYear(), mustBeBefore.minusDays(1).getMonth(), mustBeBefore.minusDays(1).getDayOfMonth(), i.plusMinutes(j).getHour(), i.plusMinutes(j).getMinute(), i.plusMinutes(j).getSecond());
                        cEventToAdd.title = ld.assignmentTitle; //ld.assignmentcEvents.add(cEventToAdd);
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