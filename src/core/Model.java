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

    public Model(Element... elements) {
        this.elements = new ArrayList<>(Arrays.asList(elements));
        tnext = 0.0;
        tcurr = tnext;
        nearestEvent = 0;
    }

    public void simulate(double time) {
        boolean isFirstIteration = true;

        while (tcurr < time) {
            tnext = Double.MAX_VALUE;
            for (var element : elements) {
                if ((tcurr < element.getTnext() || isFirstIteration) && element.getTnext() < tnext) {
                    tnext = element.getTnext();
                    nearestEvent = element.getId();
                }
            }
            updateBlockedElements();
            System.out.println("\nEvent in " + elements.get(nearestEvent).getName() + ", tNext = " + tnext);
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
                System.out.println("Failure Probability = " + formatter.format(p.getFailures() / (double) (p.getQuantity() + p.getFailures())));
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

}