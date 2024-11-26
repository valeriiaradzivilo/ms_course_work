package additional;

import java.util.ArrayList;

public class Model {
    double tnext, tcurr;
    int event;
    private ArrayList<Element> list = new ArrayList<>();
    private int eventCount;

    public Model(ArrayList<Element> elements) {
        list = elements;
        tnext = 0.0;
        event = 0;
        tcurr = tnext;
        eventCount = 0;
    }

    public void simulate(double time) {
        while (tcurr < time) {
            tnext = Double.MAX_VALUE;
            for (Element e : list) {
                if (e.getTnext() < tnext) {
                    tnext = e.getTnext();
                    event = e.getId();
                }
            }
            for (Element e : list) {
                e.doStatistics(tnext - tcurr);
            }
            tcurr = tnext;
            for (Element e : list) {
                e.setTcurr(tcurr);
            }
            getElementFromList(event).outAct();
            for (Element e : list) {
                if (e.getTnext() == tcurr) {
                    e.outAct();
                }
            }
        }
        printResult();
        eventCount++;
//        System.out.println("Event count: " + eventCount);
        System.out.println("___________");
    }

    public void printInfo() {
        for (Element e : list) {
            e.printInfo();
        }
    }

    public Element getElementFromList(int event) {
        for (Element element : list) {
            if (element.getId() == event)
                return element;
        }
        return null;
    }

    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        for (Element e : list) {
            e.printResult();
            if (e instanceof Process p) {
                System.out.println("mean length of queue = " + p.getMeanQueue() / tcurr
                        + "\nfailure probability  = "
                        + p.getFailure() / (double) p.getQuantity());
            }
        }
    }
}