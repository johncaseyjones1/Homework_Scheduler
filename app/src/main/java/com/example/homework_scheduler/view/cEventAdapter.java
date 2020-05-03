package com.example.homework_scheduler.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.homework_scheduler.R;
import com.example.homework_scheduler.model.cEvent;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class cEventAdapter extends ArrayAdapter<cEvent> {
    private Context mContext;
    private List<cEvent> ceventList = new ArrayList<>();

    public cEventAdapter(@NonNull Context context, Vector<cEvent> list) {
        super(context, 0, list);
        mContext = context;
        ceventList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.single_assignment,parent,false);

        cEvent currentcEvent = ceventList.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.assignment_name);
        name.setText(currentcEvent.title);

        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        TextView times = (TextView) listItem.findViewById(R.id.assignment_times);
        String prettyStartTime = dtf.format(currentcEvent.startLDT);
        String prettyEndTime = dtf.format(currentcEvent.endLDT);
        prettyEndTime = prettyEndTime.replaceAll(".*\\, ", "");
        String timesString = prettyStartTime + " to " + prettyEndTime;
        timesString = timesString.replaceAll("\\, [0-9][0-9][0-9][0-9]\\,", " from");
        timesString = timesString.replaceAll("\\:[0-9][0-9] ", " ");
        times.setText(timesString);

        return listItem;
    }
}
