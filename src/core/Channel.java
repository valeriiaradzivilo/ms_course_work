package core;


import core.type.ChannelState;

public class Channel {
    private Job currentJob;
    private ChannelState state = ChannelState.FREE;
    private double timeCurrent = Double.POSITIVE_INFINITY;
    private double timeNext = Double.POSITIVE_INFINITY;


    public Job getCurrentJob() {
        return currentJob;
    }


    public ChannelState getState() {
        return state;
    }


    public double getTimeCurrent() {
        return timeCurrent;
    }


    public void setTimeCurrent(double timeCurrent) {
        this.timeCurrent = timeCurrent;
    }


    public double getTimeNext() {
        return timeNext;
    }


    public void run(double newTimeNext, Job job) {
        currentJob = job;
        state = ChannelState.BUSY;
        timeNext = newTimeNext;
    }


    public Job release() {
        currentJob.setTimeOut(timeCurrent);
        state = ChannelState.FREE;
        timeNext = Double.POSITIVE_INFINITY;

        Job processedJob = currentJob;
        currentJob = null;

        return processedJob;
    }
}