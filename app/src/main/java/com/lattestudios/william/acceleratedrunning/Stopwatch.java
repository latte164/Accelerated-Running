package com.lattestudios.william.acceleratedrunning;

import java.util.Timer;

public class Stopwatch {

    private int startTime;
    private int timeShown;

    private boolean isRunning;

    public Stopwatch() {
        this.isRunning = false;
        this.timeShown = 0;
    }

    public void start() {
        startTime = (int)(System.currentTimeMillis()/1000.0);
        isRunning = true;
    }

    public void stop() {
        this.timeShown = getTime();
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getTime() {
        if(isRunning){
            return (int)Math.floor(System.currentTimeMillis()/1000.0)-this.startTime + this.timeShown;
        } else {
            return this.timeShown;
        }
    }

    public void reset() {
        this.stop();
        this.timeShown = 0;
        this.startTime = (int)System.currentTimeMillis()/1000;
    }
}