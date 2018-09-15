package com.lattestudios.william.acceleratedrunning;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    //add menu button
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Switch notificationSettingsSwitch;
    private SharedPreferences settings, recordsPrefs;
    private Button clearAllRecords, deleteOneRecordButton;
    private Spinner deleteOneRecord;
    private String[] spinnerArray, spinnerArrayTemp;
    private String spinnerArrayString;
    private Integer counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //variable declaration
        mDrawerLayout = (DrawerLayout) findViewById(R.id.settingsLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notificationSettingsSwitch = (Switch) findViewById(R.id.settingsNotifySplitSwitch);
        clearAllRecords = (Button) findViewById(R.id.settingsClearAllRecordsButton);
        deleteOneRecord = (Spinner) findViewById(R.id.settingsDeleteOneRecordSpinner);
        deleteOneRecordButton = (Button) findViewById(R.id.recordsDeleteOneRecordButton);

        settings = getSharedPreferences("settings", MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();
        recordsPrefs = getSharedPreferences("records", MODE_PRIVATE);
        final SharedPreferences.Editor recordsEditor = recordsPrefs.edit();

        notificationSettingsSwitch.setChecked(settings.getBoolean("notificationSoundSettings", true));

        spinnerArrayString = recordsPrefs.getString("recordsDistance", "").replace("[", "").replace("]", "").replace(", ", "");
        spinnerArray = spinnerArrayString.split("&");
        spinnerArrayTemp = spinnerArray;

        counter = 0;
        for(String s : spinnerArrayTemp) {

            for(int i = 0; i < counter; ++i) {

                if(s.equals(spinnerArrayTemp[i])) {

                    spinnerArrayTemp[counter] = spinnerArrayTemp[counter] + " (" + (counter) + ")";

                }

            }

            ++counter;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerArrayTemp);
        deleteOneRecord.setAdapter(adapter);

        //navigate to new activity on click in menu
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigationViewSettings);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch(menuItem.getItemId()){
                    case(R.id.nav_calculator):
                        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainActivity);
                        break;
                    case(R.id.nav_stopwatch):
                        Intent stopwatchActivity = new Intent(getApplicationContext(), StopwatchActivity.class);
                        startActivity(stopwatchActivity);
                        break;
                    case R.id.nav_records:
                        Intent recordsActivity = new Intent(getApplicationContext(), RecordsActivity.class);
                        startActivity(recordsActivity);
                        break;
                }

                return true;
            }
        });

        notificationSettingsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    editor.putBoolean("notificationSoundSettings", isChecked);
                    editor.apply();
                } else if (!isChecked) {
                    editor.putBoolean("notificationSoundSettings", isChecked);
                    editor.apply();
                    Log.d("set false", "false");
                }

            }
        });

        clearAllRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordsEditor.putString("recordsDistance", "");
                recordsEditor.putString("recordsTime", "");
                recordsEditor.apply();

                //throws error
                /*spinnerArrayString = recordsPrefs.getString("recordsDistance", "");
                spinnerArray = spinnerArrayString.split("&");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(new SettingsActivity(), android.R.layout.simple_list_item_1, spinnerArray);
                deleteOneRecord.setAdapter(adapter);*/
            }
        });

        deleteOneRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> tempRecords = new ArrayList<String>();
                List<String> tempTimes = new ArrayList<String>();
                int counterTemp = 0, i = 0;
                String[] times = recordsPrefs.getString("recordsTime", "").replace("[", "").replace("]", "").replace(", ", "").split("&");
                StringBuilder distanceSB = new StringBuilder();
                StringBuilder timeSB = new StringBuilder();

                for(String s : spinnerArray) {
                    if(!s.equals(deleteOneRecord.getSelectedItem().toString())) {

                        tempRecords.add(s);

                    } else if (s.equals(deleteOneRecord.getSelectedItem().toString())) {

                        counterTemp = i;
                        Log.d("deleted", "Deleted " + deleteOneRecord.getSelectedItem().toString());
                    }

                    ++i;
                }

                for(int j = 0; j < times.length;) {
                    if(j != counterTemp) {

                        tempTimes.add(times[j]);
                        Log.d("adding", "adding " + times[j]);
                    }

                    ++j;
                }

                while(counter > 0 ){

                    for(int l = 0; l < tempRecords.size(); ++l) {

                        tempRecords.set(l, tempRecords.get(l).replace(" (" + counter + ")", ""));

                    }

                    --counter;
                }

                for(int k = 0; k < tempTimes.size(); ++k) {

                    if(!tempTimes.get(k).equals("") || !tempTimes.get(k).equals(" ")){
                        timeSB.append(tempTimes.get(k)).append("&");
                    }

                    if(!tempRecords.get(k).equals("") || !tempRecords.get(k).equals(" ")) {
                        distanceSB.append(tempRecords.get(k)).append("&");
                    }
                }

                Log.d("string builders: ", "Distance: " + distanceSB.toString() + " & Times: " + timeSB.toString());

                recordsEditor.putString("recordsDistance", distanceSB.toString());
                recordsEditor.putString("recordsTime", timeSB.toString());
                recordsEditor.apply();
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
