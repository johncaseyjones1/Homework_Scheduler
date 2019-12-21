package com.example.homework_scheduler.view.questions;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.homework_scheduler.R;
import com.example.homework_scheduler.calendar_helpers.Local_Data;
import com.example.homework_scheduler.view.MainActivity;

import org.mortbay.jetty.Main;

public class Question_1_Fragment extends Fragment {
    private View view;
    Local_Data ld = Local_Data.getInstance();
    MainActivity mainActivity;

    public Question_1_Fragment(MainActivity activity){
        this.mainActivity = activity;
        mainActivity.optionsButton.setClickable(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.question_1, container, false);

        final Button nextQButton = view.findViewById(R.id.next_question);
        final EditText theTitle = view.findViewById(R.id.assignment_title);

        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        nextQButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send content of editText to some "calendarEvent" model

                //add as new title
                ld.assignmentTitle = theTitle.getText().toString();

                //go to question 2
                //Toast.makeText(getContext(),ld.assignmentTitle,Toast.LENGTH_SHORT).show();
                System.out.print("title is " + ld.assignmentTitle);
                FragmentManager fm = getFragmentManager();
                Fragment frag = new Question_2_Fragment();
                FragmentTransaction fragTran = fm.beginTransaction();
                fragTran.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                fragTran.replace(R.id.question_2, frag);
                fragTran.addToBackStack(null);
                fragTran.commit();
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
