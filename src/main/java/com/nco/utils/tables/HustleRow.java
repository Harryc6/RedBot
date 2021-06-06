package com.nco.utils.tables;

public class HustleRow {
    private String job;
    private int lowPayout;
    private int mediumPayout;
    private int highPayout;

    public HustleRow(String job, int lowPayout, int mediumPayout, int highPayout) {
        this.job = job;
        this.lowPayout = lowPayout;
        this.mediumPayout = mediumPayout;
        this.highPayout = highPayout;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getLowPayout() {
        return lowPayout;
    }

    public void setLowPayout(int lowPayout) {
        this.lowPayout = lowPayout;
    }

    public int getMediumPayout() {
        return mediumPayout;
    }

    public void setMediumPayout(int mediumPayout) {
        this.mediumPayout = mediumPayout;
    }

    public int getHighPayout() {
        return highPayout;
    }

    public void setHighPayout(int highPayout) {
        this.highPayout = highPayout;
    }
}
