package core;

import java.util.ArrayList;
import java.util.List;

public class Dispose extends Element {
    ArrayList<Job> processedJobs = new ArrayList<>();

    public Dispose(String name) {
        super(name);
    }

    public double getAverageTimeInModel() {
        return processedJobs.stream().mapToDouble(Job::getTimeIn).average().orElse(0.0);
    }

    public double getMaxTimeInModel() {
        return processedJobs.stream().mapToDouble(Job::getTimeIn).max().orElse(0.0);
    }

    public double getMinTimeInModel() {
        return processedJobs.stream().mapToDouble(Job::getTimeIn).min().orElse(0.0);
    }

    @Override
    public void inAct(Job job) {
        job.setTimeOut(getTcurr());
        processedJobs.add(job);
    }

    @Override
    public void outAct() {
        throw new UnsupportedOperationException("OutAct is not supported for Dispose");
    }

    @Override
    public void printInfo() {
        System.out.println("Dispose: " + getName());
    }

    @Override
    public void printResult() {
        System.out.println(
                getName() + ": " +
                        "Quantity = " + getQuantity() +
                        ", Average time in the system: " + getAverageTimeInModel() +
                        ", Max time in the system: " + getMaxTimeInModel() +
                        ", Min time in the system: " + getMinTimeInModel()
        );
    }

    @Override
    public void doStatistics(double delta) {
    }

    @Override
    public int getQuantity() {
        return processedJobs.size();
    }


    public List<Job> getAllJobs() {
        return processedJobs;
    }
}