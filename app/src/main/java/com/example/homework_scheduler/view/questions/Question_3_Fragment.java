package com.example.homework_scheduler.view.questions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.homework_scheduler.R;

public class Question_3_Fragment extends Fragment {
    private View view;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.not_enough_time_screen, container, false);

        final Button nextQButton = view.findViewById(R.id.next_question);

        nextQButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send content of editText to some "calendarEvent" model
                //go to question 4

                FragmentManager fm = getFragmentManager();
                Fragment frag = new Question_4_Fragment();
                FragmentTransaction fragTran = fm.beginTransaction();
                fragTran.replace(R.id.question_4, frag);
                fragTran.addToBackStack(null);
                fragTran.commit();
            }
        });
        return view;
    }
}
