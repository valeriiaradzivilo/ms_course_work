package core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Element {
    private static int nextId = 0;
    private final ArrayList<Route> routes = new ArrayList<>();
    private final int id;
    private final String name;
    private Routing routing = Routing.BY_PRIORITY;
    private Distribution distribution;
    private double tNext;
    private double tCurr;
    private double delayMean;
    private double delayDev;
    private int quantity = 0;
    private int state = 0;

    public Element(String name) {
        this.name = name;
        tNext = Double.MAX_VALUE;
        tCurr = tNext;
        delayMean = 1.0;
        distribution = Distribution.NONE;
        id = nextId;
        nextId++;
    }

    public Element(String name, double delayMean) {
        this.name = name;
        tNext = 0.0;
        tCurr = tNext;
        this.delayMean = delayMean;
        distribution = Distribution.EXPONENTIAL;
        id = nextId;
        nextId++;
    }

    public Element(String name, double delayMean, double delayDev) {
        this.name = name;
        tNext = 0.0;
        tCurr = tNext;
        this.delayMean = delayMean;
        this.delayDev = delayDev;
        distribution = Distribution.NORMAL;
        id = nextId;
        nextId++;
    }

    public Element(String name, double delayMean, Distribution distribution) {
        this.name = name;
        tNext = 0.0;
        tCurr = tNext;
        this.delayMean = delayMean;
        this.delayDev = delayDev;
        this.distribution = distribution;
        id = nextId;
        nextId++;
    }

    private static ArrayList<Route> getUnblockedRoutes(ArrayList<Route> routes, Job routedJob) {
        var unblockedRoutes = new ArrayList<Route>();
        for (var route : routes) {
            if (!route.isBlocked(routedJob)) {
                unblockedRoutes.add(route);
            }
        }
        return unblockedRoutes;
    }

    private static double[] getScaledProbabilities(ArrayList<Route> routes) {
        var probabilities = new double[routes.size()];
        for (int i = 0; i < routes.size(); i++) {
            probabilities[i] = routes.get(i).getProbability() + (i == 0 ? 0 : probabilities[i - 1]);
        }
        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] *= 1 / (probabilities[probabilities.length - 1]);
        }
        return probabilities;
    }

    public void setRouting(Routing routing) {
        this.routing = routing;
    }

    public double getDelay() {
        return switch (distribution) {
            case EXPONENTIAL -> FunRand.Exponential(delayMean);
            case UNIFORM -> FunRand.Uniform(delayMean, delayDev);
            case NORMAL -> FunRand.Normal(delayMean, delayDev);
            case ERLANG -> FunRand.Erlang(delayMean, delayDev);
            default -> delayMean;
        };
    }

    public double getDelayMean() {
        return delayMean;
    }

    public void setDelayMean(double delayMean) {
        this.delayMean = delayMean;
    }

    public double getDelayDev() {
        return delayDev;
    }

    public void setDelayDev(double delayDev) {
        this.delayDev = delayDev;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void changeQuantity(int delta) {
        this.quantity += delta;
    }

    public Route getNextRoute(Job routedJob) {
        if (routes.size() == 0) {
            return new Route(null);
        }
        return switch (routing) {
            case BY_PROBABILITY -> getNextRouteByProbability(routedJob);
            case BY_PRIORITY -> getNextRouteByPriority(routedJob);
            case COMBINED -> getNextRouteCombined(routedJob);
        };
    }

    private Route getNextRouteByProbability(Job routedJob) {
        var unblockedRoutes = getUnblockedRoutes(routes, routedJob);
        if (unblockedRoutes.size() == 0) {
            return routes.get(0);
        }
        var probability = Math.random();
        var scaledProbabilities = getScaledProbabilities(unblockedRoutes);
        for (int i = 0; i < scaledProbabilities.length; i++) {
            if (probability < scaledProbabilities[i]) {
                return unblockedRoutes.get(i);
            }
        }
        return unblockedRoutes.get(unblockedRoutes.size() - 1);
    }

    private Route getNextRouteByPriority(Job routedJob) {
        var unblockedRoutes = getUnblockedRoutes(routes, routedJob);
        if (unblockedRoutes.size() == 0) {
            return routes.get(0);
        }
        return unblockedRoutes.get(0);
    }

    private Route getNextRouteCombined(Job routedJob) {
        Route selectedRoute = null;
        for (var route : routes) {
            if (!route.isBlocked(routedJob)) {
                selectedRoute = route;
                break;
            }
        }
        if (selectedRoute == null) {
            return routes.get(0);
        }

        var samePriorityRoutes = findRoutesByPriority(selectedRoute.getPriority());
        var probability = Math.random();
        var scaledProbabilities = getScaledProbabilities(samePriorityRoutes);
        for (int i = 0; i < scaledProbabilities.length; i++) {
            if (probability < scaledProbabilities[i]) {
                selectedRoute = samePriorityRoutes.get(i);
                break;
            }
        }
        return selectedRoute;
    }

    private ArrayList<Route> findRoutesByPriority(int priority) {
        var routesByPriority = new ArrayList<Route>();
        for (var route : routes) {
            if (route.getPriority() == priority) {
                routesByPriority.add(route);
            }
        }
        return routesByPriority;
    }

    public void addRoutes(Route... routes) {
        this.routes.addAll(List.of(routes));
        this.routes.sort(Comparator.comparingInt(Route::getPriority).reversed());
    }

    public void inAct(Job job) {
    }

    public void outAct() {
        quantity++;
    }

    public double getTNext() {
        return tNext;
    }

    public void setTNext(double tNext) {
        this.tNext = tNext;
    }

    public double getTCurr() {
        return tCurr;
    }

    public void setTCurr(double tCurr) {
        this.tCurr = tCurr;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void printInfo() {
        System.out.println(name + " state = " + getState() + " quantity = " + getQuantity() + " tnext = " + getTNext());
    }

    public void printResult() {
        System.out.println(name + " quantity = " + getQuantity());
    }

    public int getId() {
        return id;
    }

    public void doStatistics(double delta) {
    }

    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
    }
}