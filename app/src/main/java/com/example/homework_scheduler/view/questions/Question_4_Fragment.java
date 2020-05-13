package com.example.homework_scheduler.view.questions;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.homework_scheduler.R;
import com.example.homework_scheduler.model.Local_Data;
import com.example.homework_scheduler.view.Confirmation_Screen;
import com.example.homework_scheduler.view.MainActivity;
import com.example.homework_scheduler.view.Waiting_Screen;

import java.time.LocalDateTime;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class Question_4_Fragment extends Fragment {
    private View view;
    Local_Data ld = Local_Data.getInstance();

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.question_4, container, false);

        mDisplayDate = (TextView) view.findViewById(R.id.date_text);
        final Button nextQButton = view.findViewById(R.id.next_question);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        mDateSetListener,
                        year,month,day);
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                ld.dueDate = LocalDateTime.of(year, month, day, 23, 00);
                mDisplayDate.setText(date);
            }
        };

        nextQButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send content of editText to some "calendarEvent" model
                //go to completion screen

                //Toast.makeText(getContext(),ld.dueDate.toString(), Toast.LENGTH_SHORT).show();



                ld.addEvent = 1;
                FragmentManager fm = getFragmentManager();
                Fragment frag = new Confirmation_Screen();
                FragmentTransaction fragTran = fm.beginTransaction();
                //fragTran.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                fragTran.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                fragTran.replace(R.id.waiting_screen, frag);
                fragTran.addToBackStack(null);
                fragTran.commit();
                //((MainActivity)getActivity()).refreshResults();


            }
        });
        return view;
    }
}
