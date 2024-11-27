package core;

import java.util.Comparator;
import java.util.List;


public class Model {
    private List<Element> elements;
    private double timeNext = 0;
    private double timeCurrent = 0;
    private int currentElementId;
    private double modelingTime = 0;

    public Model(List<Element> elements) {
        this.elements = elements;
    }

    public List<Element> getElements() {
        return elements;
    }

    public double getTnext() {
        return timeNext;
    }

    public double getTimeCurrent() {
        return timeCurrent;
    }

    public int getCurrentElementId() {
        return currentElementId;
    }

    public double getModelingTime() {
        return modelingTime;
    }

    public void simulate(double modelingTime) {
        boolean firstIteration = true;
        timeNext = 0;
        timeCurrent = 0;
        this.modelingTime = modelingTime;

        while (timeNext < modelingTime) {
            timeNext = Double.POSITIVE_INFINITY;

            boolean finalFirstIteration = firstIteration;
            Element minElementByTime = elements.stream()
                    .filter(element -> timeCurrent < element.getTnext() || finalFirstIteration)
                    .min(Comparator.comparingDouble(Element::getTnext))
                    .orElse(null);

            if (minElementByTime != null && minElementByTime.getTnext() < timeNext) {
                timeNext = minElementByTime.getTnext();
            }

            countModelStatistic(timeNext - timeCurrent);

            for (Element element : elements) {
                if (element.getTnext() <= timeCurrent) {
                    element.setTnext(timeNext);
                }

                if (element instanceof Process) {
                    ((Process) element).doStatistics(timeNext - timeCurrent);
                }
            }

            timeCurrent = timeNext;
            if (timeCurrent > modelingTime) {
                break;
            }

            for (Element element : elements) {
                element.setTcurr(timeCurrent);
            }

            for (Element element : elements) {
                if (Math.abs(element.getTnext() - timeCurrent) < 0.000001) {
                    currentElementId = element.getId();
                    element.outAct();
                }
            }

            firstIteration = false;
        }

        printResults();
    }

    public void printResults() {
        System.out.println("\n----------------------------RESULTS----------------------------");
        int totalFailures = 0;

        for (Element element : elements) {
            element.printResult();

            if (element instanceof Process) {
                Process process = (Process) element;
                double processQueueMean = process.getMeanQueue() / timeCurrent;
                totalFailures += process.getFailure();
//                double failureProbability = process.t() + process.getFailure() > 0
//                        ? (double) process.getFailuresCount() / (process.getProcessedTimes() + process.getFailuresCount())
//                        : 0;
//                double avgProcessTime = process.getTotalProcessTime() / modelingTime;
//                double avgWorkerProcessTime = avgProcessTime / process.getChannels().size();

                System.out.println("Mean length of queue = " + Math.round(processQueueMean * 10000.0) / 10000.0);
                System.out.println("Failure count = " + process.getFailure());
//                System.out.println("Failures count = " + process.getFailuresCount());
//                System.out.println("Failure probability = " + Math.round(failureProbability * 10000.0) / 10000.0);
//                System.out.println("Average element process time = " + Math.round(avgProcessTime * 10000.0) / 10000.0);
//                System.out.println("Average worker process time = " + Math.round(avgWorkerProcessTime * 10000.0) / 10000.0);
            }

            System.out.println();
        }
        System.out.println("\nTotal failures = " + totalFailures + "\n");
    }

    protected void countModelStatistic(double deltaTime) {
        // Implement the method as needed
    }
}