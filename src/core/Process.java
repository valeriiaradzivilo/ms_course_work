package core;


import core.type.ChannelState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Process extends Element {
    protected List<Job> jobs = new ArrayList<>();
    private double timeCurrent;
    private int queueMax = Integer.MAX_VALUE;
    private int channelsMax = 1;
    private double meanQueueAmount = 0;
    private List<Channel> channels;
    private double totalProcessTime = 0;
    private double totalOccupiedTime = 0;
    private double previousReleaseTime = 0;

    public Process() {
        channels = new ArrayList<>();
        for (int i = 0; i < channelsMax; i++) {
            channels.add(new Channel());
        }
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public int getQueueMax() {
        return queueMax;
    }

    public int getChannelsMax() {
        return channelsMax;
    }

    public double getMeanQueueAmount() {
        return meanQueueAmount;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public double getTotalProcessTime() {
        return totalProcessTime;
    }

    public double getTotalOccupiedTime() {
        return totalOccupiedTime;
    }

    public double getPreviousReleaseTime() {
        return previousReleaseTime;
    }

    public boolean isBusy() {
        return channels.stream().anyMatch(channel -> channel.getState() == ChannelState.BUSY);
    }

    public int getBusyChannelsCount() {
        return (int) channels.stream().filter(channel -> channel.getState() == ChannelState.BUSY).count();
    }

    @Override
    public double getTimeCurrent() {
        return timeCurrent;
    }

    @Override
    public void setTimeCurrent(double timeCurrent) {
        this.timeCurrent = timeCurrent;
        for (Channel channel : channels) {
            channel.setTimeCurrent(timeCurrent);
        }
    }

    public void seedProcess(int jobsInProgress, int jobsInQueue, String type) {
        jobsInProgress = Math.min(jobsInProgress, channels.size());
        jobsInQueue = Math.min(jobsInQueue, queueMax);

        for (int i = 0; i < jobsInProgress; i++) {
            Job newJob = new Job(0);
            channels.get(i).run(timeCurrent + getDelay(newJob), newJob);
        }

        for (int i = 0; i < jobsInQueue; i++) {
            jobs.add(new Job());
        }

        timeNext = channels.stream().min(Comparator.comparingDouble(Channel::getTimeNext)).get().getTimeNext();
    }

    @Override
    public void inAct(Job job) {
        Channel firstFreeChannel = channels.stream().filter(channel -> channel.getState() == ChannelState.FREE).findFirst().orElse(null);

        if (firstFreeChannel != null) {
            double delay = getDelay(job);
            firstFreeChannel.run(timeCurrent + delay, job);
            totalProcessTime += delay;
            timeNext = firstFreeChannel.getTimeNext();
        } else if (jobs.size() < queueMax) {
            jobs.add(job);
        } else {
            failuresCount++;
        }
    }

    @Override
    public void outAct() {
        timeNext = channels.stream().min(Comparator.comparingDouble(Channel::getTimeNext)).get().getTimeNext();

        List<Channel> channelsWithMinTimeNext = channels.stream()
                .filter(channel -> channel.getState() == ChannelState.BUSY && Math.abs(channel.getTimeNext() - timeNext) < 0.000001)
                .collect(Collectors.toList());

        pushCompletedJobsToNextElement(channelsWithMinTimeNext);
        loadJobsFromQueue(channelsWithMinTimeNext);
    }

    public List<Job> getUnprocessedJobs() {
        List<Job> jobs = new ArrayList<>();
        List<Job> jobsInChannels = channels.stream().map(Channel::getCurrentJob).filter(job -> job != null).collect(Collectors.toList());
        jobs.addAll(jobsInChannels);
        jobs.addAll(this.jobs);
        jobs.forEach(job -> job.setTimeOut(timeCurrent));
        return jobs;
    }

    public void updateStatistic(double timeDelta) {
        meanQueueAmount += jobs.size() * timeDelta;
    }

    @Override
    public void printStatus() {
        System.out.println("Element type \"" + this.getClass().getSimpleName() + "\" with name " + getName() +
                " | processed=" + getProcessedTimes() +
                " | current queue length=" + jobs.size() +
                " | time next=" + getTimeNext());
    }

    @Override
    public void printResult() {
        System.out.println("Process name: " + getName() + " processed = " + getProcessedTimes() +
                " | failures=" + getFailuresCount() +
                " | average workload=" + Math.round(totalProcessTime / timeCurrent * 1000.0) / 1000.0 +
                " | average queue length=" + Math.round(meanQueueAmount / timeCurrent * 1000.0) / 1000.0 +
                " | average leave interval=" + Math.round(totalOccupiedTime / getProcessedTimes() * 1000.0) / 1000.0);
    }

    protected void pushCompletedJobsToNextElement(List<Channel> channelsWithMinTimeNext) {
        for (Channel channel : channelsWithMinTimeNext) {
            Job currentJob = channel.getCurrentJob();
            IRoute route = getNextRoute(currentJob);

            if (route == null) {
                continue;
            }

            channel.release();
            processedTimes++;
            route.getElement().inAct(currentJob);
            totalOccupiedTime += timeCurrent - previousReleaseTime;
            previousReleaseTime = timeCurrent;
        }
    }

    protected void loadJobsFromQueue(List<Channel> channelsWithMinTimeNext) {
        if (jobs.size() > 0) {
            int nextTaskPortion = Math.min(jobs.size(), channelsWithMinTimeNext.size());

            for (int i = 0; i < nextTaskPortion; i++) {
                Job job = jobs.remove(0);
                double delay = getDelay(job);
                channelsWithMinTimeNext.get(i).run(timeCurrent + delay, job);
                totalProcessTime += delay;
            }

            timeNext = channels.stream().min(Comparator.comparingDouble(Channel::getTimeNext)).get().getTimeNext();
        }
    }
}