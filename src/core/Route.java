package core;


public class Route {
    private final Element element;
    private int priority = 0;
    private double probability = 1.0;

    public Route(Element element) {
        this.element = element;
    }


    public Route(Element element, double probability) {
        this.element = element;
        this.probability = probability;
    }

    public Route(Element element, double probability, int priority) {
        this.element = element;
        this.probability = probability;
        this.priority = priority;
    }


    public Element getElement() {
        return element;
    }

    public int getPriority() {
        return priority;
    }

    public double getProbability() {
        return probability;
    }


}