package core;

import core.type.Distribution;
import core.type.Routing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Element {
    private static int nextId = 0;
    private final ArrayList<Route> routes = new ArrayList<>();
    private String name;
    private double tnext;
    private double delayMean;
    private double delayDev;
    private Distribution distribution;
    private int quantity;
    private double tcurr;
    private int state;
    private Element nextElement;
    private int id;
    private Routing routing;

    public Element() {
        tnext = 0.0;
        delayMean = 1.0;
        distribution = Distribution.EXP;
        tcurr = tnext;
        state = 0;
        nextElement = null;
        id = nextId;
        nextId++;
        name = "element" + id;
    }

    public Element(double delay) {
        tnext = 0.0;
        delayMean = delay;
        distribution = Distribution.UNKNOWN;
        tcurr = tnext;
        state = 0;
        nextElement = null;
        id = nextId;
        nextId++;
        name = "element" + id;
    }

    public Element(String name) {
        this.name = name;
        tnext = 0.0;
        delayMean = 1.0;
        distribution = Distribution.UNKNOWN;
        tcurr = tnext;
        state = 0;
        nextElement = null;
        id = nextId;
        nextId++;
    }

    public Element(String nameOfElement, double delay) {
        name = nameOfElement;
        tnext = 0.0;
        delayMean = delay;
        distribution = Distribution.EXP;
        tcurr = tnext;
        state = 0;
        nextElement = null;
        id = nextId;
        nextId++;

    }

    public Element(double delay, Distribution distribution, double deviation) {
        tnext = 0.0;
        delayMean = delay;
        delayDev = deviation;
        this.distribution = distribution;
        tcurr = tnext;
        state = 0;
        nextElement = null;
        id = nextId;
        nextId++;
        name = "element" + id;
    }

    public Element(String name, double delay, Distribution distribution, double deviation) {
        this.name = name;
        tnext = 0.0;
        delayMean = delay;
        delayDev = deviation;
        this.distribution = distribution;
        tcurr = tnext;
        state = 0;
        nextElement = null;
        id = nextId;
        nextId++;

    }

    public Element(double delay, Distribution distribution) {
        tnext = 0.0;
        delayMean = delay;
        this.distribution = distribution;
        tcurr = tnext;
        state = 0;
        nextElement = null;
        id = nextId;
        nextId++;
        name = "element" + id;
    }

    public double getDelay() {
        double delay = getDelayMean();

        switch (getDistribution()) {
            case EXP -> delay = FunRand.Exp(getDelayMean());
            case NORM -> delay = FunRand.Norm(getDelayMean(), getDelayDev());
            case UNIF -> delay = FunRand.Unif(getDelayMean(), getDelayDev());
            case UNKNOWN -> delay = getDelayMean();
        }

        return delay;
    }


    public double getDelayDev() {
        return delayDev;
    }

    public void setDelayDev(double delayDev) {
        this.delayDev = delayDev;
    }

    public void setRouting(Routing routing) {
        this.routing = routing;
    }

    public Distribution getDistribution() {
        return distribution;
    }

    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTcurr() {
        return tcurr;
    }

    public void setTcurr(double tcurr) {
        this.tcurr = tcurr;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Element getNextElement() {
        return nextElement;
    }

    public void setNextElement(Element nextElement) {
        this.nextElement = nextElement;
    }

    public double getTnext() {
        return tnext;
    }

    public void setTnext(double tnext) {
        this.tnext = tnext;
    }

    public double getDelayMean() {
        return delayMean;
    }

    public void setDelayMean(double delayMean) {
        this.delayMean = delayMean;
    }


    public void inAct() {
    }

    public void inAct(Job job) {
    }

    public void outAct() {
        quantity++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void printResult() {
        System.out.println(getName() + "  quantity = " + quantity);
    }

    public void printInfo() {
        System.out.println(getName() + " state= " + state + " quantity = " + quantity +
                " tnext= " + tnext);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void doStatistics(double delta) {

    }

    public void setNextRoute(Element nextElement) {
        this.nextElement = nextElement;
    }

    public void setNextRoute(Element element, double probability) {
        if (FunRand.Unif(0, 1) < probability) {
            this.nextElement = element;
        }
    }

    public void addRoutes(Route... routes) {
        this.routes.addAll(List.of(routes));
        if (routing == Routing.BY_PRIORITY) {
            this.routes.sort(Comparator.comparingInt(Route::getPriority).reversed());
        } else {
            this.routes.sort(Comparator.comparingDouble(Route::getProbability).reversed());
        }

    }
}