package com.example.homework_scheduler.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.homework_scheduler.R;
import com.example.homework_scheduler.model.Local_Data;
import com.example.homework_scheduler.model.cEvent;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Assignment_Screen extends Fragment {
    private View view;
    Local_Data ld = Local_Data.getInstance();
    MainActivity mainActivity;

    public Assignment_Screen(MainActivity activity){
        this.mainActivity = activity;
        mainActivity.optionsButton.setClickable(false);
        mainActivity.fab.setAlpha(0.0f);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.assignment_screen, container, false);

        final cEvent theEvent;
        theEvent = ld.cEventsForHomeScreen.elementAt(mainActivity.eventSelected);

        //Toast.makeText(mainActivity, theEvent.eventID, Toast.LENGTH_SHORT).show();

        final Button delete_button = view.findViewById(R.id.delete_event);
        final TextView assignment_title = view.findViewById(R.id.assignment_title);
        assignment_title.setText(theEvent.title);

        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        TextView times = (TextView) view.findViewById(R.id.assignment_times);
        String prettyStartTime = dtf.format(theEvent.startLDT);
        String prettyEndTime = dtf.format(theEvent.endLDT);
        prettyEndTime = prettyEndTime.replaceAll(".*\\, ", "");
        String timesString = prettyStartTime + " to " + prettyEndTime;
        timesString = timesString.replaceAll("\\, [0-9][0-9][0-9][0-9]\\,", " from");
        timesString = timesString.replaceAll("\\:[0-9][0-9] ", " ");
        times.setText(timesString);
        //assignment_title.setText("HELP PLEASE");

        //assignment_title.setText();

        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        /*back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < getFragmentManager().getBackStackEntryCount(); ++i) {
                    getFragmentManager().popBackStack();
                }
                ((MainActivity)getActivity()).refreshResults();
            }
        });*/

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*try {
                    mainActivity.mService.events().delete("primary", theEvent.eventID).execute();
                } catch(IOException ioe) {

                }*/
                Toast.makeText(mainActivity,"Just a moment...",Toast.LENGTH_SHORT).show();
                ld.deleteEvent = 1;
                ld.eventIDToDelete = theEvent.eventID;
                mainActivity.refreshResults();
                //for(int i = 0; i < getFragmentManager().getBackStackEntryCount(); ++i) {
                //    getFragmentManager().popBackStack();
                //}
                ((MainActivity)getActivity()).refreshResults();
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mainActivity.fab.setAlpha(1.0f);
        mainActivity.fab.setClickable(true);
        mainActivity.optionsButton.setClickable(true);
    }
}
