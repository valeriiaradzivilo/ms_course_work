package core;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Model {
    protected final ArrayList<Element> elements;
    protected double tcurr;
    protected double tnext;
    protected int nearestEvent;
    protected int modelingTime;


    protected Map<Double, Double> meanTimeInSystemStatistics = new HashMap<>();


    public Model(Element... elements) {
        this.elements = new ArrayList<>(Arrays.asList(elements));
        tnext = 0.0;
        tcurr = tnext;
        nearestEvent = 0;
        modelingTime = 0;
    }

    public Model(int modelingTime, Element... elements) {
        this.elements = new ArrayList<>(Arrays.asList(elements));
        tnext = 0.0;
        tcurr = tnext;
        nearestEvent = 0;
        this.modelingTime = modelingTime;
    }


    public double simulate() {
        boolean isFirstIteration = true;

        while (tcurr < modelingTime) {
            tnext = Double.MAX_VALUE;
            for (var element : elements) {
                if ((tcurr < element.getTnext() || isFirstIteration) && element.getTnext() < tnext) {
                    tnext = element.getTnext();
                    nearestEvent = element.getId();
                }
            }
            updateBlockedElements();
//            System.out.println("\nEvent in " + elements.get(nearestEvent).getName() + ", tnext = " + tnext);
            var delta = tnext - tcurr;
            for (Element element : elements) {
                element.doStatistics(delta);
            }
            tcurr = tnext;
            for (var element : elements) {
                element.setTcurr(tcurr);
            }
            elements.get(nearestEvent).outAct();
            for (var element : elements) {
                if (element.getTnext() == tcurr) {
                    element.outAct();
                }
            }
            isFirstIteration = false;
            printInfo();
        }
        return printResult();
    }

    public void printInfo() {

        for (var element : elements) {
            element.printInfo();
            if (element instanceof Dispose dispose) {
                if (Math.floor(tcurr) % 3 == 0) {
                    double meanTimeInSystem = dispose.getProcessedJobs().stream()
                            .mapToDouble(job -> job.getTimeOut() - job.getTimeIn())
                            .average()
                            .orElse(0.0);

                    meanTimeInSystemStatistics.put(tcurr, meanTimeInSystem);
                }
            }

        }
    }

    public double printResult() {
        NumberFormat formatter = new DecimalFormat("#0.0000");
        System.out.println("\n-------------RESULTS-------------");
        double meanTimeInSystem = 0;
        for (var element : elements) {
            element.printResult();
            if (element instanceof Process p) {
                System.out.println("Mean Queue = " + formatter.format(p.getMeanQueue() / tcurr));
                System.out.println("Mean Workload = " + formatter.format(p.getWorkTime() / tcurr));
                System.out.println("Queue leftovers = " + p.getQueueSize());
                System.out.println("Failure Probability = " + formatter.format(p.getFailures() / (double) (p.getQuantity() + p.getFailures())));
                System.out.println("_______________________________");
            }
            if (element instanceof Dispose dispose) {
                meanTimeInSystem = dispose.getProcessedJobs().stream()
                        .mapToDouble(job -> job.getTimeOut() - job.getTimeIn())
                        .average()
                        .orElse(0.0);
            }
        }
        return meanTimeInSystem;
    }

    private void updateBlockedElements() {
        for (var element : elements) {
            if (element.getTnext() <= tcurr) {
                element.setTnext(tnext);
            }
        }
    }

    public Map<Double, Double> getMeanTimeInSystemStatistics() {
        return meanTimeInSystemStatistics;
    }


}