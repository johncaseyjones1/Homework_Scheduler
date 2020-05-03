package com.example.homework_scheduler.view;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.homework_scheduler.R;
import com.example.homework_scheduler.model.Local_Data;
import com.example.homework_scheduler.calendar_helpers.grab_future_events;

import java.util.Vector;


public class Start_Screen extends Fragment {
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.start_screen, container, false);
        System.out.println("WE ARE DOING IT");

        return view;
    }

    public class Grab_Events extends AsyncTask<Void,Void, Vector<String>> {
        private Context context;
        private Exception localExcpetion = null;
        Vector<String> events = new Vector<>();

        protected Vector<String> doInBackground(Void... nothing) {
            grab_future_events gfe = new grab_future_events();
            Local_Data localData = Local_Data.getInstance();
            try {
                grab_future_events.main("");
            } catch (Exception e) {
                events.add("error");
            }
            return events;
        }

        protected void onPostExecute(Vector<String> input) {
            // TODO: check this.exception

                Toast regReqError = Toast.makeText(view.getContext(),events.elementAt(0),
                        Toast.LENGTH_LONG);
                regReqError.show();
        }
    }
}
