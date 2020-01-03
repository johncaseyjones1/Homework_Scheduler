package com.example.homework_scheduler.view;

import com.example.homework_scheduler.R;
import com.example.homework_scheduler.cEventAdapter;
import com.example.homework_scheduler.calendar_helpers.Local_Data;
import com.example.homework_scheduler.model.cEvent;
import com.example.homework_scheduler.view.questions.Question_1_Fragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.calendar.CalendarScopes;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Vector;


/**
 * Created by miguel on 5/29/15.
 */

public class MainActivity extends AppCompatActivity {
    /**
     * A Google Calendar API service object used to access the API.
     * Note: Do not confuse this class with API library's model classes, which
     * represent specific data structures.
     */
    Local_Data ld = Local_Data.getInstance();
    public Vector<cEvent> yeet = new Vector<>();
    cEventAdapter ceventAdapter;

    com.google.api.services.calendar.Calendar mService;

    GoogleAccountCredential credential;
    private TextView mStatusText;
    private TextView mResultsText;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR };
    public TextView ohno;
    public ImageView lb;
    public FloatingActionButton fab;
    public TextView topline;
    public Button optionsButton;
    public Integer eventSelected;
    public ListView listView;


    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //getWindow().setStatusBarColor(Color.WHITE);
        super.onCreate(savedInstanceState);
        LinearLayout activityLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);
        activityLayout.setPadding(16, 16, 16, 16);

        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        ActionBar actionBar = getSupportActionBar();
        View abView = findViewById(R.id.the_action_bar);

        actionBar.setCustomView(abView);




        mStatusText = new TextView(this);
        mStatusText.setLayoutParams(tlp);
        mStatusText.setTypeface(null, Typeface.BOLD);
        mStatusText.setText("Retrieving data...");
        activityLayout.addView(mStatusText);

        mResultsText = new TextView(this);
        mResultsText.setLayoutParams(tlp);
        mResultsText.setPadding(16, 16, 16, 16);
        mResultsText.setVerticalScrollBarEnabled(true);
        mResultsText.setMovementMethod(new ScrollingMovementMethod());
        activityLayout.addView(mResultsText);

        //setContentView(activityLayout);

        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();



        setContentView(R.layout.activity_main);

        ohno = findViewById(R.id.ohno);
        lb = findViewById(R.id.logo_button);


        eventSelected = 0;

        listView = findViewById(R.id.elv);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = (Assignment_Screen) fm.findFragmentById(R.id.assignment_screen);
                if (fragment == null) {
                    fragment = createAssignmentFrag();
                    fm.beginTransaction()
                            .addToBackStack(null)
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                            .add(R.id.assignment_screen, fragment)
                            .commit();
                }
                //fab.setAlpha(0.0f);
                optionsButton.setClickable(false);
                fab.setClickable(false);
                //Object listItem = listView.getItemAtPosition(position);
                eventSelected = position;
            }
        });                 // new ArrayAdapter<cEvent>(this, android.R.layout.simple_list_item_1, yeet);
        //listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, yeet));
        ceventAdapter = new cEventAdapter(this, ld.cEventsForHomeScreen);

        listView.setAdapter(ceventAdapter);
        System.out.println("CALENDAR SIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!: " + ld.calendarEvents.size());



        //FABBBBBBB
        fab = findViewById(R.id.fab);
        //fab.setAlpha(1.0f);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < getFragmentManager().getBackStackEntryCount(); ++i) {
                    getFragmentManager().popBackStack();
                }
                System.out.println("First line\n");
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = (Question_1_Fragment) fm.findFragmentById(R.id.question_1);
                if (fragment == null) {
                    fragment = createQ1Fragment(getString(R.string.app_name));
                    fm.beginTransaction()
                            .addToBackStack(null)
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                            .add(R.id.question_1, fragment)
                            .commit();
                }
                //fab.setAlpha(0.0f);
                optionsButton.setClickable(false);
                fab.setClickable(false);

            }
        });

        optionsButton = findViewById(R.id.options_button);

        final View parentO = (View) optionsButton.getParent();
        parentO.post(new Runnable() {
            @Override
            public void run() {
                final Rect rect = new Rect();
                optionsButton.getHitRect(rect);
                rect.top -= 100;    // increase top hit area
                rect.left -= 100;   // increase left hit area
                rect.bottom += 100; // increase bottom hit area
                rect.right += 100;  // increase right hit area
                parentO.setTouchDelegate( new TouchDelegate( rect , optionsButton));
            }
        });

        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("First line\n");
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = (Settings) fm.findFragmentById(R.id.options);
                if (fragment == null) {
                    fragment = createOptionsFrag();
                    fm.beginTransaction()
                            .addToBackStack(null)
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                            .add(R.id.options, fragment)
                            .commit();
                }
                //ld.addEvent = 1;
                //refreshResults();
                //refreshResults();
            }
        });
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

    private Assignment_Screen createAssignmentFrag() {
        Assignment_Screen fragment = new Assignment_Screen(this);
        Bundle args = new Bundle();
        return fragment;
    }

    private Settings createOptionsFrag() {
        Settings fragment = new Settings(this);
        Bundle args = new Bundle();
        return fragment;
    }

    private Question_1_Fragment createQ1Fragment(String title) {
        Question_1_Fragment fragment = new Question_1_Fragment(this);
        Bundle args = new Bundle();
        return fragment;
    }


    /**
     * Called whenever this activity is pushed to the foreground, such as after
     * a call to onCreate().
     */
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        System.out.println("SHARED PREFS ARE\n start: " + prefs.getString(ld.startTime_id, null) + "\nend: " + prefs.getString(ld.endTime_id, null) + "\n max: " + prefs.getFloat(ld.maxTime_id, 0.0f) + "\n daysB4: " + prefs.getInt(ld.daysBefore_id, 0));

        if (prefs.getString(ld.startTime_id, null) != null) {
            System.out.println("DIDNT FAIL TO GET SAHRED PREF");
            ld.startHomeworkTime = stringToTime(prefs.getString(ld.startTime_id, null));
            ld.startTimeInt = prefs.getInt(ld.startTimePosit_id, 0);
            System.out.println(ld.startTimeInt);
        }
        if (prefs.getString(ld.endTime_id, null) != null) {
            ld.endHomeworkTime = stringToTime(prefs.getString(ld.endTime_id, null));
            ld.endTimeInt = prefs.getInt(ld.endTimePosit_id, 0);
        }

        ld.maxTimeOneSitting = prefs.getFloat(ld.maxTime_id, 0.0f);
        ld.maxTimeInt = prefs.getInt(ld.maxTimePosit_id, 0);

        if (prefs.getInt(ld.daysBefore_id, 0) != 0)
            ld.daysEarly = prefs.getInt(ld.daysBefore_id, 0);

        //turn all buttons on
        //fab.setClickable(true);
        //fab.setAlpha(1.0f);


        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            mStatusText.setText("Google Play Services required: " +
                    "after installing, close and relaunch this app.");
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == RESULT_OK) {
                    refreshResults();
                } else {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                        refreshResults();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    mStatusText.setText("Account unspecified.");
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    refreshResults();
                } else {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempt to get a set of data from the Google Calendar API to display. If the
     * email address isn't known yet, then call chooseAccount() method so the
     * user can pick an account.
     */
    public void refreshResults() {
        if (credential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (isDeviceOnline()) {
                new ApiAsyncTask(this).execute();
            } else {
                mStatusText.setText("No network connection available.");
            }
        }
    }

    /**
     * Clear any existing Google Calendar API data from the TextView and update
     * the header message; called from background threads and async tasks
     * that need to update the UI (in the UI thread).
     */
    public void clearResultsText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusText.setText("Retrieving data…");
                mResultsText.setText("");
            }
        });
    }

    /**
     * Fill the data TextView with the given List of Strings; called from
     * background threads and async tasks that need to update the UI (in the
     * UI thread).
     * @param dataStrings a List of Strings to populate the main TextView with.
     */
    public void updateResultsText(final List<String> dataStrings) {
        if (ld.firstTime == 1) {
            fab.setAlpha(1.0f);
            fab.setClickable(true);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dataStrings == null) {
                    mStatusText.setText("Error retrieving data!");
                } else if (dataStrings.size() == 0) {
                    mStatusText.setText("No data found.");
                } else {
                    mStatusText.setText("Data retrieved using" +
                            " the Google Calendar API:");
                    mResultsText.setText(TextUtils.join("\n\n", dataStrings));
                    yeet.clear();
                    yeet.addAll(ld.cEventsForHomeScreen);
                    for (cEvent ce : ld.cEventsForHomeScreen)
                    {
                        System.out.println(ce.title);
                    }
                    System.out.println("YEET SIZE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + yeet.size());
                    ceventAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    /**
     * Show a status message in the list header TextView; called from background
     * threads and async tasks that need to update the UI (in the UI thread).
     * @param message a String to display in the UI header TextView.
     */
    public void updateStatus(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusText.setText(message);
            }
        });
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(
                credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        MainActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

}

/*package com.example.homework_scheduler.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.homework_scheduler.R;
import com.example.homework_scheduler.view.questions.Question_1_Fragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("First line\n");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_old);
        FragmentManager fm = this.getSupportFragmentManager();
        Fragment fragment = (Start_Screen) fm.findFragmentById(R.id.start_screen);
        if (fragment == null) {
            fragment = createSSFragment(getString(R.string.app_name));
            fm.beginTransaction()
                    .add(R.id.start_screen, fragment)
                    .commit();
        }
    }

    private Start_Screen createSSFragment(String title) {
        Start_Screen fragment = new Start_Screen();
        Bundle args = new Bundle();
        return fragment;
    }
}*/
