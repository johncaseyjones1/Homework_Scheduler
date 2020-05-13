package com.example.homework_scheduler.view;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.homework_scheduler.R;
import com.example.homework_scheduler.model.Local_Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class Confirmation_Screen  extends Fragment {
    private View view;
    Local_Data ld = Local_Data.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.confirmation_screen, container, false);



        final Button nextQButton = view.findViewById(R.id.next_question);

        final Spinner startSpinner = (Spinner) view.findViewById(R.id.start_time);
        final Spinner endSpinner = (Spinner) view.findViewById(R.id.end_time);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.just_hours, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        startSpinner.setAdapter(timeAdapter);
        startSpinner.setSelection(ld.startTimeInt);

        endSpinner.setAdapter(timeAdapter);
        endSpinner.setSelection(ld.endTimeInt);

        final Spinner maxSpinner = (Spinner) view.findViewById(R.id.max_time);
        ArrayAdapter<CharSequence> maxAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.hour_options, android.R.layout.simple_spinner_item);

        maxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxSpinner.setAdapter(maxAdapter);

        maxSpinner.setSelection(ld.maxTimeInt);


        final Spinner daysSpinner = (Spinner) view.findViewById(R.id.days_before);
        ArrayAdapter<CharSequence> daysAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.days_before, android.R.layout.simple_spinner_item);

        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setAdapter(daysAdapter);

        daysSpinner.setSelection(getIndex(daysSpinner, Integer.toString(ld.daysEarly)));

        endSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (position < startSpinner.getSelectedItemPosition())
                {
                    Toast.makeText(getContext(),"Your end time must come after your start time.",Toast.LENGTH_LONG).show();
                    endSpinner.setSelection(ld.endTimeInt);
                } else {
                    ld.endTimeInt = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        startSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (position > endSpinner.getSelectedItemPosition())
                {
                    Toast.makeText(getContext(),"Your start time must come before your end time",Toast.LENGTH_LONG).show();
                    startSpinner.setSelection(ld.startTimeInt);
                } else {
                    ld.startTimeInt = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        nextQButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send content of editText to some "calendarEvent" model
                //go to completion screen

                //Toast.makeText(getContext(),ld.dueDate.toString(), Toast.LENGTH_SHORT).show();

                //save settings in prefs and ld
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                //####################################PREFS###############################
                //start time
                editor.putString(ld.startTime_id,startSpinner.getSelectedItem().toString());
                editor.putInt(ld.startTimePosit_id,startSpinner.getSelectedItemPosition());

                //end time
                editor.putString(ld.endTime_id,endSpinner.getSelectedItem().toString());
                editor.putInt(ld.endTimePosit_id,endSpinner.getSelectedItemPosition());

                //max time
                System.out.println("max time: " + maxSpinner.getSelectedItem().toString());
                editor.putFloat(ld.maxTime_id,Float.parseFloat(maxSpinner.getSelectedItem().toString()));
                editor.putInt(ld.maxTimePosit_id,maxSpinner.getSelectedItemPosition());

                //days before
                editor.putInt(ld.daysBefore_id,Integer.parseInt(daysSpinner.getSelectedItem().toString()));

                editor.apply();

                //####################################LD##################################
                //start time
                ld.startHomeworkTime = stringToTime(startSpinner.getSelectedItem().toString());
                ld.startTimeInt = startSpinner.getSelectedItemPosition();

                //end time
                ld.endHomeworkTime = stringToTime(endSpinner.getSelectedItem().toString());
                ld.endTimeInt = endSpinner.getSelectedItemPosition();

                //max time
                ld.maxTimeOneSitting = Float.parseFloat(maxSpinner.getSelectedItem().toString());
                ld.maxTimeInt = maxSpinner.getSelectedItemPosition();

                //days before
                ld.daysEarly = Integer.parseInt(daysSpinner.getSelectedItem().toString());

                ld.addEvent = 1;
                FragmentManager fm = getFragmentManager();
                Fragment frag = new Waiting_Screen();
                FragmentTransaction fragTran = fm.beginTransaction();
                //fragTran.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                fragTran.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                fragTran.replace(R.id.waiting_screen, frag);
                fragTran.addToBackStack(null);
                fragTran.commit();
                ((MainActivity)getActivity()).refreshResults();


            }
        });

        return view;
    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    LocalTime stringToTime(String timeString) {
        String hourStr = "";
        int hour = 0;
        String minStr = "";
        int min;

        if (timeString.charAt(2) == ':') {
            hourStr = hourStr + timeString.charAt(0) + timeString.charAt(1);
            hour = Integer.parseInt(hourStr);

            minStr = minStr + timeString.charAt(3) + timeString.charAt(4);
            min = Integer.parseInt(minStr);
        } else {
            hourStr = hourStr + timeString.charAt(0);
            hour = Integer.parseInt(hourStr);

            minStr = minStr + timeString.charAt(2) + timeString.charAt(3);
            min = Integer.parseInt(minStr);
        }
        hourStr = hourStr + timeString.charAt(0) + timeString.charAt(1);

        if (timeString.charAt(timeString.length() - 2) == 'P' && hour != 12)
        {
            hour = hour + 12;
        }

        return LocalTime.of(hour, min, 00);
    }

}
