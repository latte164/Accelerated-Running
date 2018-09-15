package com.lattestudios.william.acceleratedrunning;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.text.DecimalFormat;
import static android.content.Context.MODE_PRIVATE;

public class StopwatchManager {

    private Stopwatch main = new Stopwatch();
    public StopwatchTimer counter = new StopwatchTimer();

    public TextView stop1;
    public TextView stop2;
    private final TextView distance;
    private final TextView pace;
    private final TextView time;
    private Button toggle;
    private Button reset;

    ColorStateList colors;

    private final StopwatchActivity MainAct;

    public StopwatchManager(TextView MainStopwatch, TextView RelativeStopwatch, final TextView distance, final TextView pace, final TextView time, Button Toggle, Button Reset, StopwatchActivity Main) {
        this.stop1 = MainStopwatch;
        this.stop2 = RelativeStopwatch;
        this.distance = distance;
        this.pace = pace;
        this.time = time;
        this.toggle = Toggle;
        this.reset = Reset;
        this.MainAct = Main;

        colors = stop2.getTextColors();

        stop1.setText("00:00:00");
        stop2.setText("00:00:00");
        toggle.setText("Start/Stop");
        reset.setText("Clear");

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(main.isRunning()) {
                    main.stop();
                    counter.stop();
                } else {
                    main.start();
                    counter.start();
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.reset();
                counter.reset();
                distance.setText("");
                pace.setText("");
                time.setText("");
                MainAct.incrementorReset();
                MainAct.stopCounterBool();
            }
        });

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        MainAct.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stop1.setText(secondsToString(main.getTime()));
                                stop2.setText(secondsToString(counter.getTime()));
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    public String secondsToString(int s) {
        return secondsToString(s, false);
    }

    public String secondsToString(int s, boolean hasSign) {

        int sec, min, hrs;
        String sgn;

        if(hasSign) {
            if(s > 0) {
                sgn = "+";
            } else if(s==0) {
                sgn = " ";
            } else {
                sgn = "-";
            }
        } else {
            sgn = "";
        }

        if(s < 0) {
            s = -s;
        }

        sec = s % 60;
        min = (int) Math.floor(((float) s) / 60.0) % 60;
        hrs = (int) Math.floor(((float) s) / 3600.0);

        DecimalFormat twoDig = new DecimalFormat("00");
        String output = sgn+twoDig.format(hrs)+":"+twoDig.format(min)+":"+twoDig.format(sec);


        return output;
    }
}