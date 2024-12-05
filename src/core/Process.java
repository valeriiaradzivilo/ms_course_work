package core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class Process extends Element {
    protected final Deque<Job> queue = new ArrayDeque<>();
    protected final ArrayList<Channel> channels = new ArrayList<>();
    protected int failures = 0;
    protected int maxQueueSize = Integer.MAX_VALUE;
    protected double meanQueue = 0.0;
    protected double workTime = 0.0;
    protected double totalLeaveTime = 0.0;
    protected double previousLeaveTime = 0.0;


    public Process(String name, double delayMean, int channelsNum, Distribution distribution) {
        super(name, delayMean, distribution);
        for (int i = 0; i < channelsNum; i++) {
            channels.add(new Channel());
        }
    }

    public Process(String name, double delayMean, double delayDev, int channelsNum) {
        super(name, delayMean, delayDev);
        for (int i = 0; i < channelsNum; i++) {
            channels.add(new Channel());
        }
    }


    @Override
    public void inAct(Job job) {
        var freeChannel = getFreeChannel();
        if (freeChannel != null) {
            freeChannel.setCurrentJob(job);
            freeChannel.setTNext(super.getTcurr() + super.getDelay());
        } else {
            if (queue.size() < getMaxQueueSize()) {
                queue.add(job);
            } else {
                failures++;
            }
        }
    }

    @Override
    public void outAct() {
        processCurrentJobs();
        startNextJobs();
    }

    protected void processCurrentJobs() {
        var channelsWithMinTNext = getChannelsWithMinTNext();
        for (var channel : channelsWithMinTNext) {
            var job = channel.getCurrentJob();

            var nextRoute = getNextRoute(job);
            
            if (nextRoute.getElement() != null) {
                job.setTimeOut(super.getTcurr());
                nextRoute.getElement().inAct(job);
            }

            channel.setCurrentJob(null);
            channel.setTNext(Double.MAX_VALUE);
            changeQuantity(1);
            totalLeaveTime += super.getTcurr() - previousLeaveTime;
            previousLeaveTime = super.getTcurr();
        }
    }

    protected void startNextJobs() {
        var freeChannel = getFreeChannel();
        while (!queue.isEmpty() && freeChannel != null) {
            var job = queue.poll();
            freeChannel.setCurrentJob(job);
            freeChannel.setTNext(super.getTcurr() + super.getDelay());
            freeChannel = getFreeChannel();
        }
    }

    protected ArrayList<Channel> getChannelsWithMinTNext() {
        var channelsWithMinTNext = new ArrayList<Channel>();
        var minTNext = Double.MAX_VALUE;
        for (var channel : channels) {
            if (channel.getTNext() < minTNext) {
                minTNext = channel.getTNext();
            }
        }
        for (var channel : channels) {
            if (channel.getTNext() == minTNext) {
                channelsWithMinTNext.add(channel);
            }
        }
        return channelsWithMinTNext;
    }

    protected Channel getFreeChannel() {
        for (var channel : channels) {
            if (channel.getState() == 0) {
                return channel;
            }
        }
        return null;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    public int getFailures() {
        return failures;
    }

    public double getMeanQueue() {
        return meanQueue;
    }

    public double getWorkTime() {
        return workTime;
    }

    @Override
    public void doStatistics(double delta) {
        super.doStatistics(delta);
        meanQueue += queue.size() * delta;
        workTime += getState() * delta;
    }

    @Override
    public int getState() {
        int state = 0;
        for (Channel channel : channels) {
            state |= channel.getState();
        }
        return state;
    }

    @Override
    public double getTnext() {
        double tNext = Double.MAX_VALUE;
        for (Channel channel : channels) {
            if (channel.getTNext() < tNext) {
                tNext = channel.getTNext();
            }
        }
        return tNext;
    }

    @Override
    public void setTnext(double tNext) {
        double previousTNext = getTnext();
        for (Channel channel : channels) {
            if (channel.getTNext() == previousTNext) {
                channel.setTNext(tNext);
            }
        }
    }

    @Override
    public void printInfo() {
        System.out.println(getName() +
                " state = " + getState() +
                " quantity = " + getQuantity() +
                " tnext = " + getTnext() +
                " failures = " + failures +
                " queue size = " + queue.size()
        );
    }


    public int getQueueSize() {
        return queue.size();
    }

    protected static class Channel {
        private Job currentJob = null;
        private double tNext = Double.MAX_VALUE;

        public int getState() {
            return currentJob == null ? 0 : 1;
        }

        public Job getCurrentJob() {
            return currentJob;
        }

        public void setCurrentJob(Job currentJob) {
            this.currentJob = currentJob;
        }

        public double getTNext() {
            return tNext;
        }

        public void setTNext(double tNext) {
            this.tNext = tNext;
        }
    }

}