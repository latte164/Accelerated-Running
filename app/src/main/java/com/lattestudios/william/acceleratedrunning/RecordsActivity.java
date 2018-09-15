package com.lattestudios.william.acceleratedrunning;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import org.w3c.dom.Text;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RecordsActivity extends AppCompatActivity {

    //menu button variables
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private EditText distanceEditText, hoursEditText, minutesEditText, secondsEditText;
    private Integer hours, minutes, seconds;
    private String measurementUnits, time, distance;
    private Spinner measurementSpinner, deleteSpinner;
    private ImageButton addButton, deleteButton;
    private TextView distanceListTextView, timeListTextView;
    private ArrayList<String> recordsDistanceListEntries = new ArrayList<>();
    private ArrayList<String> recordsTimeListEntries= new ArrayList<>();
    public SharedPreferences recordsPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        final DecimalFormat twoDig = new DecimalFormat();
        twoDig.applyPattern("00");

        //menu setup
        mDrawerLayout = (DrawerLayout) findViewById(R.id.recordsLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //variable declaration
        distanceEditText = (EditText) findViewById(R.id.recordsDistanceEditText);
        hoursEditText = (EditText) findViewById(R.id.recordsHoursEditText);
        minutesEditText = (EditText) findViewById(R.id.recordsMinutesEditText);
        secondsEditText = (EditText) findViewById(R.id.recordsSecondsEditText);
        measurementSpinner = (Spinner) findViewById(R.id.recordsMeasurementsSpinner);
        addButton = (ImageButton) findViewById(R.id.recordsAddImageButton);
        distanceListTextView = (TextView) findViewById(R.id.recordsDistanceListTextView);
        timeListTextView = (TextView) findViewById(R.id.recordsTimeListTextView);

        recordsPrefs = getSharedPreferences("records", MODE_PRIVATE);

        Log.d("before arraylist ntsplt", recordsPrefs.getString("recordsDistance", ""));
        recordsDistanceListEntries = new ArrayList<>(Arrays.asList(recordsPrefs.getString("recordsDistance", "").replace("&", "&\n").split("&")));
        recordsTimeListEntries = new ArrayList<>(Arrays.asList(recordsPrefs.getString("recordsTime", "").replace("&", "&\n").split("&")));
        Log.d("after arraylist", recordsDistanceListEntries.toString());
        distanceListTextView.setText(recordsDistanceListEntries.toString().replace("[", "").replace("]", "").replace(", ", "").replace("&", ""));
        timeListTextView.setText(recordsTimeListEntries.toString().replace("[", "").replace("]", "").replace(", ", "").replace("&", ""));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                measurementUnits = measurementSpinner.getSelectedItem().toString();

                //set blank fields equal to 0
                Object[][] times = {{minutes,minutesEditText.getText()}, {hours, hoursEditText.getText()}, {seconds,secondsEditText.getText()}};
                for (Object[] i: times) {
                    try{
                        i[0] = Integer.parseInt((i[1]).toString());

                    } catch(NumberFormatException e) {
                        i[0] = 0;
                    }
                }
                minutes = (int) times[0][0];
                hours = (int) times[1][0];
                seconds = (int) times[2][0];

                distance = distanceEditText.getText().toString();

                time = twoDig.format(hours) + ":" + twoDig.format(minutes) + ":" + twoDig.format(seconds);

                Log.d("records prefs before", recordsPrefs.getString("recordsDistance", ""));
                if(!recordsDistanceListEntries.toString().equals("")) {
                    recordsDistanceListEntries = new ArrayList<>(Arrays.asList(recordsPrefs.getString("recordsDistance", "").split("&")));
                    recordsTimeListEntries = new ArrayList<>(Arrays.asList(recordsPrefs.getString("recordsTime", "").split("&")));
                }
                Log.d("recs entries after splt", Arrays.asList(recordsPrefs.getString("recordsDistance", "").split("&")).toString());
                Log.d("records lst entries aft", recordsDistanceListEntries.toString());

                if(measurementUnits.equals("Kilometers")){
                        recordsDistanceListEntries.add(distance + " Km");}
                if(measurementUnits.equals("Miles")){
                        recordsDistanceListEntries.add(distance + " Mi");}
                recordsTimeListEntries.add(time);
                Log.d("dist. arraylst aftr add", recordsDistanceListEntries.toString());

                distanceListTextView.setText("");
                timeListTextView.setText("");
                for(String s : recordsDistanceListEntries) {
                    distanceListTextView.append(s + "\n");
                    Log.d("text view append", s + "\n");
                }
                for(String s : recordsTimeListEntries){
                    timeListTextView.append(s + "\n");
                }

                StringBuilder distanceSB = new StringBuilder();
                StringBuilder timeSB = new StringBuilder();
                for(String s : recordsDistanceListEntries){
                    String temp = s.replace("&", "").replace("\n", "");
                    if(!s.equals("")){distanceSB.append(temp).append("&");}
                    Log.d("strng bldr distance", distanceSB.toString());
                }
                for(String s : recordsTimeListEntries){
                    String temp = s.replace("&", "").replace("\n", "");
                    if(!s.equals("")){timeSB.append(temp).append("&");}
                }
                SharedPreferences.Editor editor = recordsPrefs.edit();
                editor.putString("recordsDistance", distanceSB.toString());
                editor.putString("recordsTime", timeSB.toString());
                editor.apply();
                Log.d("dist after to shrdpref", recordsPrefs.getString("recordsDistance", ""));
            }
        });

        //navigate to new activity on click in menu
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigationViewStopwatch);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch(menuItem.getItemId()){
                    case(R.id.nav_calculator):
                        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainActivity);
                        break;
                    case R.id.nav_stopwatch:
                        Intent stopwatchActivity = new Intent(getApplicationContext(), StopwatchActivity.class);
                        startActivity(stopwatchActivity);
                        break;
                    case(R.id.nav_settings):
                        Intent settingsActivity = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(settingsActivity);
                        break;
                }

                return true;
            }
        });
    }

    //open menu when top left button is clicked function
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if (mToggle.onOptionsItemSelected(item)) {
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
