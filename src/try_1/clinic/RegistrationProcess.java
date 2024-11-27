package try_1.clinic;

import try_1.core_1.Job;

import java.util.ArrayDeque;
import java.util.HashMap;

public class RegistrationProcess extends try_1.core_1.Process {
    private int prioritizedPatientType;
    private HashMap<Integer, Double> patientTypedDelays;

    public RegistrationProcess(String name, double delayMean, int channelsNum) {
        super(name, delayMean, channelsNum);
    }

    public void setPrioritizedPatientType(int type) {
        this.prioritizedPatientType = type;
    }

    public void setPatientTypedDelays(int[] types, double[] delays) {
        this.patientTypedDelays = new HashMap<>();
        for (int i = 0; i < types.length; i++) {
            this.patientTypedDelays.put(types[i], delays[i]);
        }
    }

    @Override
    public void inAct(Job job) {
        var freeChannel = getFreeChannel();
        if (freeChannel != null) {
            freeChannel.setCurrentJob(job);
            var originalDelayMean = getDelayMean();
            var patientType = ((Patient) job).getType();
            setDelayMean(patientTypedDelays.get(patientType));
            freeChannel.setTNext(super.getTCurr() + super.getDelay());
            setDelayMean(originalDelayMean);
        } else {
            if (queue.size() < getMaxQueueSize()) {
                queue.add(job);
            } else {
                failures++;
            }
        }
    }

    @Override
    protected void startNextJobs() {
        var originalDelay = getDelayMean();
        var freeChannel = getFreeChannel();
        sortQueueByPatientPriority();
        while (!queue.isEmpty() && freeChannel != null) {
            var patient = (Patient) queue.poll();
            var type = patient.getType();
            setDelayMean(patientTypedDelays.get(type));
            freeChannel.setCurrentJob(patient);
            freeChannel.setTNext(super.getTCurr() + super.getDelay());
            freeChannel = getFreeChannel();
        }
        setDelayMean(originalDelay);
    }

    private void sortQueueByPatientPriority() {
        var prioritizedPatients = new ArrayDeque<Patient>();
        var otherPatients = new ArrayDeque<Patient>();
        while (!queue.isEmpty()) {
            var patient = (Patient) queue.poll();
            if (patient.getType() == prioritizedPatientType) {
                prioritizedPatients.add(patient);
            } else {
                otherPatients.add(patient);
            }
        }
        queue.addAll(prioritizedPatients);
        queue.addAll(otherPatients);
    }
}