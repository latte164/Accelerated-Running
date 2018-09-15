package com.lattestudios.william.acceleratedrunning;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class StopwatchActivity extends AppCompatActivity {

    //add menu button
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    //variables
    StopwatchManager man;
    SettingsActivity settingsAct;

    TextView stop1, stop2, distanceTextView, paceTextView, timeTextView;
    Button toggle, reset;
    private Integer wholeSplitCalcData, twoMeterSplitCalcData, fourMeterSplitCalcData, decWholeSplitCalcData, fourMeterNotSubSplitCalcData;
    private double distanceDoubCalcData;
    ArrayList<Integer> times = new ArrayList<Integer>();

    Integer incrementorTimer = 0;
    SharedPreferences settingsPreferences;

    Boolean runCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        //variable declaration
        mDrawerLayout = (DrawerLayout) findViewById(R.id.stopwatchLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        stop1 = (TextView) findViewById(R.id.MainStopwatch);
        stop2 = (TextView) findViewById(R.id.RelativeStopwatch);
        toggle = (Button) findViewById(R.id.StopToggle);
        reset = (Button) findViewById(R.id.ResetButton);

        distanceTextView = (TextView) findViewById(R.id.distanceTextViewStopwatch);
        paceTextView = (TextView) findViewById(R.id.paceTextViewStopwatch);
        timeTextView = (TextView) findViewById(R.id.timeTextViewStopwatch);

        man = new StopwatchManager(stop1, stop2, distanceTextView, paceTextView, timeTextView, toggle, reset, this);

        final MediaPlayer notificationSound = MediaPlayer.create(this, R.raw.pacenotification);

        settingsAct = new SettingsActivity();
        settingsPreferences = getSharedPreferences("settings", MODE_PRIVATE);

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
                    case R.id.nav_records:
                            Intent recordsActivity = new Intent(getApplicationContext(), RecordsActivity.class);
                            startActivity(recordsActivity);
                        break;
                    case(R.id.nav_settings):
                        Intent settingsActivity = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(settingsActivity);
                        break;
                }

                return true;
            }
        });

        //imports times from calc
        Button import_button = (Button) findViewById(R.id.ImportButton);
        import_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                runCounter = true;

                //Calling shared preferences
                SharedPreferences calcValues = getSharedPreferences("savedCalc", MODE_PRIVATE);
                String distanceCalcData = calcValues.getString("distanceSavedCalc", "Error: No Saved Distance Values.");
                String paceCalcData = calcValues.getString("paceSavedCalc", "Error: No Saved Pace Values.");
                String timeCalcData = calcValues.getString("timeSavedCalc", "Error: No Saved Time Values.");
                wholeSplitCalcData = calcValues.getInt("wholeSplitSecSavedCalc", 0);
                twoMeterSplitCalcData = calcValues.getInt("200MeterSplitSecSavedCalc", 0);
                fourMeterSplitCalcData = calcValues.getInt("400MeterSplitSecSavedCalc", 0)-calcValues.getInt("200MeterSplitSecSavedCalc", 0);
                fourMeterNotSubSplitCalcData = calcValues.getInt("400MeterSplitSecSavedCalc", 0);
                distanceDoubCalcData = (double) calcValues.getFloat("distanceIntSavedCalc", 0);
                if(distanceDoubCalcData > Math.floor(distanceDoubCalcData)) {
                    decWholeSplitCalcData = calcValues.getInt("decWholeSplitSecSavedCalc", 0);
                }

                distanceTextView.append(distanceCalcData);
                paceTextView.append(paceCalcData);
                timeTextView.append(timeCalcData);

                //check and set times
                Thread t2 = new Thread() {
                    @Override
                    public void run() {

                            try {
                                times.add(twoMeterSplitCalcData);
                                times.add(fourMeterSplitCalcData);
                                times.add((fourMeterNotSubSplitCalcData * 3));
                                for (int i = 2; i <= Math.floor(distanceDoubCalcData); i++) {
                                    times.add(wholeSplitCalcData);
                                }
                                if (distanceDoubCalcData > Math.floor(distanceDoubCalcData)) {
                                    times.add(decWholeSplitCalcData);
                                }

                                while (!isInterrupted()) {
                                    Thread.sleep(1000);
                                    StopwatchActivity.this.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {

                                            if (man.counter.getTime() == 0 && runCounter) {

                                                Boolean startTimer = false;
                                                if (man.counter.isRunning()) {
                                                    startTimer = true;
                                                }

                                                if (times.size() > incrementorTimer) {
                                                    man.counter.setTime(times.get(incrementorTimer));
                                                    incrementorTimer++;
                                                }

                                                if (startTimer) {
                                                    man.counter.start();

                                                    if (settingsPreferences.getBoolean("notificationSoundSettings", true)) {
                                                        notificationSound.start();
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            } catch (InterruptedException e) {}
                        }
                };
                t2.start();

            }
        });
    }

    public void incrementorReset() {
        incrementorTimer = 0;
    }
    public void stopCounterBool() { runCounter = false;}

    //open menu when top left button is clicked function
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if (mToggle.onOptionsItemSelected(item)) {
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
