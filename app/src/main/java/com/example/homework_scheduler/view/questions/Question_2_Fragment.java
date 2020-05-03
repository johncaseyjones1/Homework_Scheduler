package com.example.homework_scheduler.view.questions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.homework_scheduler.R;
import com.example.homework_scheduler.model.Local_Data;

public class Question_2_Fragment extends Fragment {
    private View view;
    Local_Data ld = Local_Data.getInstance();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.question_2, container, false);

        final Button nextQButton = view.findViewById(R.id.next_question);

        final Spinner spinner = (Spinner) view.findViewById(R.id.assignment_time);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.hour_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        nextQButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send content of editText to some "calendarEvent" model
                //go to question 3
                System.out.print("Hey");
                ld.assignmentLength = Float.parseFloat(spinner.getSelectedItem().toString());

                //Toast.makeText(getContext(),Integer.toString(ld.assignmentLength), Toast.LENGTH_SHORT).show();

                FragmentManager fm = getFragmentManager();
                Fragment frag = new Question_4_Fragment();
                FragmentTransaction fragTran = fm.beginTransaction();
                //fragTran.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                fragTran.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                fragTran.replace(R.id.question_4, frag);
                fragTran.addToBackStack(null);
                fragTran.commit();
            }
        });
        return view;
    }


}
