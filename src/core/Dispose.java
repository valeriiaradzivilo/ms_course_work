package core;

import java.util.ArrayList;

public class Dispose extends Element {
    ArrayList<Job> processedJobs = new ArrayList<>();

    public Dispose(String name) {
        super(name);
    }

    @Override
    public void inAct(Job job) {
        super.inAct(job);
        processedJobs.add(job);
        super.outAct();
    }

    @Override
    public void printInfo() {
        System.out.println(getName() + " quantity = " + getQuantity());
    }

    public ArrayList<Job> getProcessedJobs() {
        return processedJobs;
    }
 }
