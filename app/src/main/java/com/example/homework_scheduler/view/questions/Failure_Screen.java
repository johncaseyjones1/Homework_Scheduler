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
import com.example.homework_scheduler.view.MainActivity;
import com.example.homework_scheduler.view.questions.Question_1_Fragment;

public class Failure_Screen extends Fragment {
    private View view;

    MainActivity mainActivity;

    public Failure_Screen(MainActivity activity){
        this.mainActivity = activity;
        mainActivity.fab.setClickable(false);
        mainActivity.fab.setAlpha(0.0f);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.not_enough_time_screen, container, false);

        //final Button anotherOneButton = view.findViewById(R.id.another_assignment);
        final Button finishedButton = view.findViewById(R.id.quit);

        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*for (Fragment fragment : getFragmentManager().getFragments()) {
                    getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right).remove(fragment).commit();
                }
                 */
                for(int i = 0; i < getFragmentManager().getBackStackEntryCount(); ++i) {
                    getFragmentManager().popBackStack();
                }
                ((MainActivity)getActivity()).refreshResults();
            }
        });



        /*anotherOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send content of editText to some "calendarEvent" model
                System.out.print("Hiii 1");

                //go to question 2
                FragmentManager fm = getFragmentManager();
                Fragment frag = new Question_1_Fragment();
                FragmentTransaction fragTran = fm.beginTransaction();
                fragTran.setCustomAnimations(R.anim.slide_in_right, 0);
                fragTran.replace(R.id.question_1, frag);
                //fragTran.remove();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                fragTran.commit();
            }
        });*/
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mainActivity.fab.setAlpha(1.0f);
        mainActivity.fab.setClickable(true);
        mainActivity.optionsButton.setClickable(true);
        mainActivity.optionsButton.setClickable(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        mainActivity.fab.setAlpha(1.0f);
        mainActivity.fab.setClickable(true);
        mainActivity.optionsButton.setClickable(true);
        mainActivity.optionsButton.setClickable(true);
    }
}
