package com.lattestudios.william.acceleratedrunning;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //add menu button
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    //variables
    private Button calculate_button;
    private EditText Distance, Hours, Minutes, Seconds;
    private TextView distanceTextView, paceTextView, timeTextView;
    private Spinner measurements;
    private String measurementsUnits;
    private int hours, minutes, seconds, minuteTimeValue;
    private double distance, wholeSplit, time, fourMeterSplit, twoMeterSplit, wholeSplitSec, fourMeterSplitSec, twoMeterSplitSec, secondTimeValue, decWholeSplit;
    public String distanceCalcData, paceCalcData, timeCalcData;

    //shared preferences definitions
    public SharedPreferences calcValues;

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            //menu button stuff
            mDrawerLayout = (DrawerLayout) findViewById(R.id.window);
            mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
            mDrawerLayout.addDrawerListener(mToggle);
            mToggle.syncState();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //navigate to new activity on click in menu
            NavigationView navigationView = (NavigationView)findViewById(R.id.navigationViewMain);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {

                    switch(menuItem.getItemId()){
                        case R.id.nav_stopwatch:
                            Intent stopwatchActivity = new Intent(getApplicationContext(), StopwatchActivity.class);
                            startActivity(stopwatchActivity);
                            break;
                        case R.id.nav_records:
                            Intent recordsActivity = new Intent(getApplicationContext(), RecordsActivity.class);
                            startActivity(recordsActivity);
                            break;
                        case R.id.nav_settings:
                            Intent settingsActivity = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(settingsActivity);
                            break;
                    }

                    return true;
                }
            });

            //variable declaration
            calculate_button = (Button) findViewById(R.id.calculate_button);
            calculate_button.setOnClickListener(this);

            Distance = (EditText) findViewById(R.id.distance_input);
            Hours = (EditText) findViewById(R.id.hour_input);
            Minutes = (EditText) findViewById(R.id.minute_input);
            Seconds = (EditText) findViewById(R.id.second_input);

            distanceTextView = (TextView) findViewById(R.id.distanceTextView);
            paceTextView = (TextView) findViewById(R.id.paceTextView);
            timeTextView = (TextView) findViewById(R.id.timeTextView);

            measurements = (Spinner) findViewById(R.id.measurements_spinner);

            //shared preferences tools
            calcValues = getSharedPreferences("savedCalc", MODE_PRIVATE);

            String distanceCalcData = calcValues.getString("distanceSavedCalc", "");
            String paceCalcData = calcValues.getString("paceSavedCalc", "");
            String timeCalcData = calcValues.getString("timeSavedCalc", "");

            distanceTextView.append(distanceCalcData);
            paceTextView.append(paceCalcData);
            timeTextView.append(timeCalcData);
        }

        @Override
        public void onClick(View view) {

            //get unit of measurements

            try {

            measurementsUnits = measurements.getSelectedItem().toString();

            //apply 00 number format
            DecimalFormat twoDig = new DecimalFormat();
            twoDig.applyPattern("00");

            //set blank fields equal to 0
                Object[][] times = {{minutes,Minutes.getText()}, {hours, Hours.getText()}, {seconds,Seconds.getText()}};
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

                //turn distance string to double
                try {
                    distance = Double.parseDouble(Distance.getText().toString());
                } catch(NumberFormatException e) {
                    distance = 0.0;

                    Snackbar errorSnackbar = Snackbar.make(findViewById(R.id.mainConstraintLayout), "Distance Number Error", Snackbar.LENGTH_SHORT);
                    errorSnackbar.show();

                }

                //calculate time in minutes
                time = Double.valueOf((hours*60) + (minutes) + (seconds/60.0));

                //begin main functions
                if(time <= 0 || distance <=0) {
                    //error
                    timeTextView.setText("Enter a time and distance greater than 0.");
                    paceTextView.setText("Enter a time and distance greater than 0.");
                    distanceTextView.setText("Enter a time and distance greater than 0.");

                } else if(measurementsUnits.equals("Miles")) {

                    //total time w/ second decimals
                    wholeSplit = time/distance;
                    fourMeterSplit = wholeSplit/4;
                    twoMeterSplit = wholeSplit/8;

                    //Second Values to ints
                    wholeSplitSec = (((wholeSplit)%((int) wholeSplit))*60);
                    wholeSplitSec = Math.floor(wholeSplitSec);
                    if (fourMeterSplit >= 1) {
                        fourMeterSplitSec = ((((wholeSplit)/4)%((int) fourMeterSplit))*60);
                        fourMeterSplitSec = Math.floor(fourMeterSplitSec);
                    } else {
                        fourMeterSplitSec = (((wholeSplit)/4)*60);
                        fourMeterSplitSec = Math.floor(fourMeterSplitSec);
                    }

                    if (twoMeterSplit >= 1) {
                        twoMeterSplitSec = ((((wholeSplit)/8)%((int) twoMeterSplit))*60);
                        twoMeterSplitSec = Math.floor(twoMeterSplitSec);
                    } else {
                        twoMeterSplitSec = (((wholeSplit)/8)*60);
                        twoMeterSplitSec = Math.floor(twoMeterSplitSec);
                    }

                    //output
                    distanceTextView.setText("");
                    paceTextView.setText("");
                    timeTextView.setText("");

                    distanceTextView.setText("200m\n400m\n1 Mi\n");

                    //add pace column
                    if (((int) twoMeterSplit) >= 60) {
                        paceTextView.append((int)Math.floor(((int) twoMeterSplit)/60) + ":" + twoDig.format((((int) twoMeterSplit)%60)) + ":" + twoDig.format((int) twoMeterSplitSec) + "\n");
                    } else {
                        paceTextView.append(((int) twoMeterSplit) + ":" + twoDig.format((int) twoMeterSplitSec) + "\n");
                    }
                    if (((int) fourMeterSplit) >= 60) {
                        paceTextView.append((int)Math.floor(((int) fourMeterSplit)/60) + ":" + twoDig.format((((int) fourMeterSplit)%60)) + ":" + twoDig.format((int) fourMeterSplitSec) + "\n");
                    } else {
                        paceTextView.append(((int) fourMeterSplit) + ":" + twoDig.format((int) fourMeterSplitSec) + "\n");
                    }
                    if (((int) wholeSplit) >= 60) {
                        paceTextView.append((int)Math.floor(((int) wholeSplit)/60) + ":" + twoDig.format((((int) wholeSplit)%60)) + ":" + twoDig.format((int) Math.ceil(wholeSplitSec)) + "\n");
                    } else {
                        paceTextView.append(((int) wholeSplit) + ":" + twoDig.format((int) Math.ceil(wholeSplitSec)) + "\n");
                    }

                    //add time column
                    if (((int) twoMeterSplit) >= 60) {
                        timeTextView.append((int)Math.floor(((int) twoMeterSplit)/60) + ":" + twoDig.format((((int) twoMeterSplit)%60)) + ":" + twoDig.format((int) twoMeterSplitSec) + "\n");
                    } else {
                        timeTextView.append(((int) twoMeterSplit) + ":" + twoDig.format((int) twoMeterSplitSec) + "\n");
                    }
                    if (((int) fourMeterSplit) >= 60) {
                        timeTextView.append((int)Math.floor(((int) fourMeterSplit)/60) + ":" + twoDig.format((((int) fourMeterSplit)%60)) + ":" + twoDig.format((int) fourMeterSplitSec) + "\n");
                    } else {
                        timeTextView.append(((int) fourMeterSplit) + ":" + twoDig.format((int) fourMeterSplitSec) + "\n");
                    }
                    if (((int) wholeSplit) >= 60) {
                        timeTextView.append((int)Math.floor(((int) wholeSplit)/60) + ":" + twoDig.format((((int) wholeSplit)%60)) + ":" + twoDig.format((int) Math.ceil(wholeSplitSec)) + "\n");
                    } else {
                        timeTextView.append(((int) wholeSplit) + ":" + twoDig.format((int) Math.ceil(wholeSplitSec)) + "\n");
                    }

                    //mile 2 through (int)distance outputs
                    for (int i = 2; i <= (int) Math.floor(distance); i++) {
                        distanceTextView.append(i + " Mi" + "\n");

                        //pace output
                        if (((int) wholeSplit) >= 60) {
                            paceTextView.append((int)Math.floor(((int) wholeSplit)/60) + ":" + twoDig.format((((int) wholeSplit)%60)) + ":" + twoDig.format((int) Math.ceil(wholeSplitSec)) + "\n");
                        } else {
                            paceTextView.append(((int) wholeSplit) + ":" + twoDig.format((int) Math.ceil(wholeSplitSec)) + "\n");
                        }

                        //second and minute time calculations for time outputs
                        secondTimeValue = ((((wholeSplit) % ((int) wholeSplit)) * 60)*i);
                        minuteTimeValue = (((int) wholeSplit)*i);
                        if(secondTimeValue >= 60){
                            minuteTimeValue += Math.floor(secondTimeValue/60);
                            secondTimeValue = (int) (secondTimeValue%60);
                        }

                        //time outputs
                        if (minuteTimeValue >= 60) {
                            timeTextView.append((int)Math.floor(minuteTimeValue/60) + ":" + twoDig.format((minuteTimeValue%60)) + ":" + twoDig.format(secondTimeValue) + "\n");
                        } else {
                            timeTextView.append(minuteTimeValue + ":" + twoDig.format(secondTimeValue) + "\n");
                            }
                        }

                    //decimal distance outputs
                    if (distance > Math.floor(distance)) {
                        distanceTextView.append(distance + " Mi\n");

                        decWholeSplit = wholeSplit*(distance - (Math.floor(distance)));
                        secondTimeValue = (((decWholeSplit) * 60));
                        minuteTimeValue = (((int) decWholeSplit));
                        if(secondTimeValue >= 60){
                            minuteTimeValue += Math.floor(secondTimeValue/60);
                            secondTimeValue = (int) (secondTimeValue%60);
                        }

                        //pace output
                        if (minuteTimeValue >= 60) {
                            paceTextView.append((int)Math.floor(minuteTimeValue/60) + ":" + twoDig.format((minuteTimeValue%60)) + ":" + twoDig.format(secondTimeValue) + "\n");
                        } else {
                            paceTextView.append(minuteTimeValue + ":" + twoDig.format(secondTimeValue) + "\n");
                        }

                        //time output
                        if(hours > 0){
                            timeTextView.append(hours + ":" + twoDig.format(minutes) + ":" + twoDig.format(seconds) + "\n");
                        } else {
                            timeTextView.append(twoDig.format(minutes) + ":" + twoDig.format(seconds) + "\n");
                        }
                    }

                    //saved calc data string definition
                    distanceCalcData = distanceTextView.getText().toString();
                    paceCalcData = paceTextView.getText().toString();
                    timeCalcData = timeTextView.getText().toString();

                    //shared preferences tools
                    calcValues = getSharedPreferences("savedCalc", MODE_PRIVATE);
                    SharedPreferences.Editor editor = calcValues.edit();

                    editor.putString("distanceSavedCalc", distanceCalcData);
                    editor.putString("paceSavedCalc", paceCalcData);
                    editor.putString("timeSavedCalc", timeCalcData);
                    editor.putInt("wholeSplitSecSavedCalc", (int)(((hours*3600) + (minutes*60) + (seconds))/distance));
                    editor.putInt("200MeterSplitSecSavedCalc", (int)(((((hours*3600) + (minutes*60) + (seconds)))/distance)/8));
                    editor.putInt("400MeterSplitSecSavedCalc", (int)(((((hours*3600) + (minutes*60) + (seconds)))/distance)/4));
                    editor.putFloat("distanceIntSavedCalc", (float)distance);
                    if(distance>Math.floor(distance)) {editor.putInt("decWholeSplitSecSavedCalc", (int)((((hours*3600) + (minutes*60) + (seconds))/distance)*(distance - (Math.floor(distance)))));}
                    editor.apply();

                } else if(measurementsUnits.equals("Kilometers")) {

                    //total time w/ second decimals
                    wholeSplit = time/distance;
                    fourMeterSplit = wholeSplit/2.5;
                    twoMeterSplit = wholeSplit/5;

                    //Second Values to ints
                    wholeSplitSec = (((wholeSplit)%((int) wholeSplit))*60);
                    wholeSplitSec = Math.floor(wholeSplitSec);
                    if (fourMeterSplit >= 1) {
                        fourMeterSplitSec = ((((wholeSplit)/2.5)%((int) fourMeterSplit))*60);
                        fourMeterSplitSec = Math.floor(fourMeterSplitSec);
                    } else {
                        fourMeterSplitSec = (((wholeSplit)/2.5)*60);
                        fourMeterSplitSec = Math.floor(fourMeterSplitSec);
                    }
                    if (twoMeterSplit >= 1) {
                        twoMeterSplitSec = ((((wholeSplit)/5)%((int) twoMeterSplit))*60);
                        twoMeterSplitSec = Math.floor(twoMeterSplitSec);
                    } else {
                        twoMeterSplitSec = (((wholeSplit)/5)*60);
                        twoMeterSplitSec = Math.floor(twoMeterSplitSec);
                    }

                    //output
                    distanceTextView.setText("");
                    paceTextView.setText("");
                    timeTextView.setText("");

                    distanceTextView.setText("200m\n400m\n1 Km\n");

                    //add pace column
                    if (((int) twoMeterSplit) >= 60) {
                        paceTextView.append((int)Math.floor(((int) twoMeterSplit)/60) + ":" + twoDig.format((((int) twoMeterSplit)%60)) + ":" + twoDig.format((int) twoMeterSplitSec) + "\n");
                    } else {
                        paceTextView.append(((int) twoMeterSplit) + ":" + twoDig.format((int) twoMeterSplitSec) + "\n");
                    }
                    if (((int) fourMeterSplit) >= 60) {
                        paceTextView.append((int)Math.floor(((int) fourMeterSplit)/60) + ":" + twoDig.format((((int) fourMeterSplit)%60)) + ":" + twoDig.format((int) fourMeterSplitSec) + "\n");
                    } else {
                        paceTextView.append(((int) fourMeterSplit) + ":" + twoDig.format((int) fourMeterSplitSec) + "\n");
                    }
                    if (((int) wholeSplit) >= 60) {
                        paceTextView.append((int)Math.floor(((int) wholeSplit)/60) + ":" + twoDig.format((((int) wholeSplit)%60)) + ":" + twoDig.format((int) Math.ceil(wholeSplitSec)) + "\n");
                    } else {
                        paceTextView.append(((int) wholeSplit) + ":" + twoDig.format((int) Math.ceil(wholeSplitSec)) + "\n");
                    }

                    //add time column
                    if (((int) twoMeterSplit) >= 60) {
                        timeTextView.append((int)Math.floor(((int) twoMeterSplit)/60) + ":" + twoDig.format((((int) twoMeterSplit)%60)) + ":" + twoDig.format((int) twoMeterSplitSec) + "\n");
                    } else {
                        timeTextView.append(((int) twoMeterSplit) + ":" + twoDig.format((int) twoMeterSplitSec) + "\n");
                    }
                    if (((int) fourMeterSplit) >= 60) {
                        timeTextView.append((int)Math.floor(((int) fourMeterSplit)/60) + ":" + twoDig.format((((int) fourMeterSplit)%60)) + ":" + twoDig.format((int) fourMeterSplitSec) + "\n");
                    } else {
                        timeTextView.append(((int) fourMeterSplit) + ":" + twoDig.format((int) fourMeterSplitSec) + "\n");
                    }
                    if (((int) wholeSplit) >= 60) {
                        timeTextView.append((int)(Math.floor(((int) wholeSplit)/60)) + ":" + twoDig.format((((int) wholeSplit)%60)) + ":" + twoDig.format((int) Math.ceil(wholeSplitSec)) + "\n");
                    } else {
                        timeTextView.append(((int) wholeSplit) + ":" + twoDig.format((int) Math.ceil(wholeSplitSec)) + "\n");
                    }

                    //output for km 2 through (int)distance
                    for (int i = 2; i <= (int) Math.floor(distance); i++) {
                        distanceTextView.append(i + " Km" + "\n");

                        //pace outputs
                        if (((int) wholeSplit) >= 60) {
                            paceTextView.append((int) (Math.floor(((int) wholeSplit)/60)) + ":" + twoDig.format((((int) wholeSplit)%60)) + ":" + twoDig.format((int) Math.ceil(wholeSplitSec)) + "\n");
                        } else {
                            paceTextView.append(((int) wholeSplit) + ":" + twoDig.format((int) Math.ceil(wholeSplitSec)) + "\n");
                        }

                        //second and minute calculations for output
                        secondTimeValue = (((( time/ distance ) % ((int) wholeSplit)) * 60)*i);
                        minuteTimeValue = (((int) wholeSplit)*i);
                        if(secondTimeValue >= 60){
                            minuteTimeValue += Math.floor(secondTimeValue/60);
                            secondTimeValue = (int) ((secondTimeValue%60));
                        }

                        //time outputs
                        if (minuteTimeValue >= 60) {
                            timeTextView.append((int)(Math.floor(minuteTimeValue/60)) + ":" + twoDig.format((minuteTimeValue%60)) + ":" + twoDig.format(secondTimeValue) + "\n");
                        } else {
                            timeTextView.append(minuteTimeValue + ":" + twoDig.format(secondTimeValue) + "\n");
                        }
                    }

                    //decimal distance outputs
                    if (distance > Math.floor(distance)) {
                        distanceTextView.append(distance + " Km\n");

                        decWholeSplit = wholeSplit*(distance - (Math.floor(distance)));
                        secondTimeValue = (((decWholeSplit) * 60));
                        minuteTimeValue = (((int) decWholeSplit));
                        if(secondTimeValue >= 60){
                            minuteTimeValue += Math.floor(secondTimeValue/60);
                            secondTimeValue = (int) (secondTimeValue%60);
                        }

                        //pace output
                        if (minuteTimeValue >= 60) {
                            paceTextView.append((int)Math.floor(minuteTimeValue/60) + ":" + twoDig.format((minuteTimeValue%60)) + ":" + twoDig.format(secondTimeValue) + "\n");
                        } else {
                            paceTextView.append(minuteTimeValue + ":" + twoDig.format(secondTimeValue) + "\n");
                        }

                        //time output
                        if(hours > 0){
                            timeTextView.append(hours + ":" + twoDig.format(minutes) + ":" + twoDig.format(seconds) + "\n");
                        } else {
                            timeTextView.append(twoDig.format(minutes) + ":" + twoDig.format(seconds) + "\n");
                        }
                    }

                    //saved calc data string definition
                    distanceCalcData = distanceTextView.getText().toString();
                    paceCalcData = paceTextView.getText().toString();
                    timeCalcData = timeTextView.getText().toString();

                    //shared preferences tools
                    calcValues = getSharedPreferences("savedCalc", MODE_PRIVATE);
                    SharedPreferences.Editor editor = calcValues.edit();

                    editor.putString("distanceSavedCalc", distanceCalcData);
                    editor.putString("paceSavedCalc", paceCalcData);
                    editor.putString("timeSavedCalc", timeCalcData);
                    editor.putInt("wholeSplitSecSavedCalc", (int)((hours*3600) + (minutes*60) + (seconds)));
                    editor.putInt("200MeterSplitSecSavedCalc", (int)((((hours*3600) + (minutes*60) + (seconds)))/5));
                    editor.putInt("400MeterSplitSecSavedCalc", (int)((((hours*3600) + (minutes*60) + (seconds)))/2.5));
                    editor.putFloat("distanceIntSavedCalc", (float)distance);
                    if(distance>Math.floor(distance)) {editor.putInt("decWholeSplitSecSavedCalc", (int)(((hours*3600) + (minutes*60) + (seconds))*(distance - (Math.floor(distance)))));}
                    editor.apply();

                } else {
                    //error
                    distanceTextView.setText("Error.");
                    paceTextView.setText("Error.");
                    timeTextView.setText("Error.");
                }

            } catch(NumberFormatException e) {
                distanceTextView.setText("Error.");
                paceTextView.setText("Error.");
                timeTextView.setText("Error.");
            }
        }

    //open menu when top left button is clicked function
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
}