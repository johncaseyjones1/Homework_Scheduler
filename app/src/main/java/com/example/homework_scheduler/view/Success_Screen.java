package com.example.homework_scheduler.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.homework_scheduler.R;

public class Success_Screen extends Fragment {
    private View view;
    MainActivity mainActivity;

    public Success_Screen(MainActivity activity){
        this.mainActivity = activity;
        mainActivity.optionsButton.setClickable(false);
        mainActivity.fab.setClickable(false);
        mainActivity.fab.setAlpha(0.0f);
        mainActivity.listView.setClickable(false);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.success, container, false);
        mainActivity.fab.setClickable(false);
        mainActivity.fab.setAlpha(0.0f);
        mainActivity.optionsButton.setClickable(false);
        //mainActivity.optionsButton.setAlpha(0.5f);

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

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mainActivity.fab.setAlpha(1.0f);
        mainActivity.fab.setClickable(true);
        mainActivity.listView.setClickable(true);
        mainActivity.optionsButton.setClickable(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        mainActivity.fab.setAlpha(1.0f);
        mainActivity.fab.setClickable(true);
        mainActivity.listView.setClickable(true);
        mainActivity.optionsButton.setClickable(true);
    }
}
