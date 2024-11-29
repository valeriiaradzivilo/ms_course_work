package core;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class Model {
    protected final ArrayList<Element> elements;
    protected double tcurr;
    protected double tnext;
    protected int nearestEvent;
    protected int modelingTime;

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

    public void simulate() {
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
            System.out.println("\nEvent in " + elements.get(nearestEvent).getName() + ", tnext = " + tnext);
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
        printResult();
    }

    public void printInfo() {
        for (var element : elements) {
            element.printInfo();
        }
    }

    public void printResult() {
        NumberFormat formatter = new DecimalFormat("#0.0000");
        System.out.println("\n-------------RESULTS-------------");
        for (var element : elements) {
            element.printResult();
            if (element instanceof Process p) {
                System.out.println("Mean Queue = " + formatter.format(p.getMeanQueue() / tcurr));
                System.out.println("Mean Workload = " + formatter.format(p.getWorkTime() / tcurr));
                System.out.println("Queue leftovers = " + p.getQueueSize());
                System.out.println("Failure Probability = " + formatter.format(p.getFailures() / (double) (p.getQuantity() + p.getFailures())));
                System.out.println("_______________________________");
            }
        }
    }

    private void updateBlockedElements() {
        for (var element : elements) {
            if (element.getTnext() <= tcurr) {
                element.setTnext(tnext);
            }
        }
    }


    public double calculateTransientPeriod(double epsilon) {
        double[] cumulativeTime = new double[elements.size()];
        double[] cumulativeResponse = new double[elements.size()];
        double[] meanResponse = new double[elements.size()];

        double totalTime = 0.0;
        double tnext;
        double tcurr = 0.0;

        while (tcurr < modelingTime) {
            tnext = Double.MAX_VALUE;


            for (Element element : elements) {
                if (element.getTnext() < tnext) {
                    tnext = element.getTnext();
                }
            }

            double delta = tnext - tcurr;
            tcurr = tnext;
            totalTime += delta;


            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                if (element instanceof Process process) {
                    cumulativeTime[i] += delta;
                    cumulativeResponse[i] += process.getQueueSize() * delta;
                    meanResponse[i] = cumulativeResponse[i] / cumulativeTime[i];
                }
            }


            for (int i = 0; i < meanResponse.length; i++) {
                for (int j = i + 1; j < meanResponse.length; j++) {
                    if (Math.abs(meanResponse[i] - meanResponse[j]) < epsilon) {
                        return tcurr;
                    }
                }
            }

            for (Element element : elements) {
                if (element.getTnext() == tcurr) {
                    element.outAct();
                }
            }
        }

        return -1;
    }

}