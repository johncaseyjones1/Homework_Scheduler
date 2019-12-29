package com.example.homework_scheduler.view;

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

public class Assignment_Screen extends Fragment {
    private View view;
    Local_Data ld = Local_Data.getInstance();
    MainActivity mainActivity;

    public Assignment_Screen(MainActivity activity){
        this.mainActivity = activity;
        mainActivity.optionsButton.setClickable(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.question_1, container, false);

        final Button back_button = view.findViewById(R.id.back_button_from_assignment);
        final TextView assignment_title = view.findViewById(R.id.assignment_title);

        //assignment_title.setText();

        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < getFragmentManager().getBackStackEntryCount(); ++i) {
                    getFragmentManager().popBackStack();
                }
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
