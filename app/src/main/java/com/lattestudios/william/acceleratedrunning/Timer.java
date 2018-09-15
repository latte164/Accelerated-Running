package com.lattestudios.william.acceleratedrunning;

import static java.lang.Integer.min;

public class Timer {

    private int startTime;
    private int timeShown;

    private double lastPause;
    private double pausedTime;

    private boolean isRunning;

    public Timer(int time) {
        this.startTime = time;
        this.timeShown = time;
        this.isRunning = false;

        this.lastPause = 0;
        this.pausedTime = 0;
    }
    public Timer() {
        this(0);
    }

    public void start() {
        isRunning = true;
        this.startTime = (int)Math.floor(System.currentTimeMillis()/1000);
        if(this.lastPause!=0) {
            this.pausedTime += Math.floor(System.currentTimeMillis()/1000.0) - this.lastPause;
        }
    }

    public void stop() {
        this.timeShown = this.getTime();
        this.isRunning = false;
        this.lastPause = (System.currentTimeMillis()/1000.0);
    }

    public void setTime(int time) {
        this.stop();
        this.pausedTime = 0;
        this.lastPause = 0;
        this.startTime = 0;
        this.timeShown = time;
    }

    public int getTime() {
        if(isRunning) {
            return Math.max(0, -(int) Math.floor(System.currentTimeMillis() / 1000.0) + this.startTime - (int) this.pausedTime + this.timeShown);
        } else {
            return this.timeShown;
        }
    }

    public void reset() {
        this.startTime = 0;
        this.timeShown = 0;
        this.isRunning = false;

        this.lastPause = 0;
        this.pausedTime = 0;
    }

}