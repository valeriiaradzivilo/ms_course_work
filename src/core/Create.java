package core;

public class Create extends Element {
    private int failures = 0;

    public Create(String name, double delay) {
        super(name, delay);
        super.setTnext(0.0);
    }

    public Create(String name, double delay, double initialTNext) {
        super(name, delay);
        super.setTnext(initialTNext);
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setTnext(super.getTcurr() + super.getDelay());
        super.getNextElement().inAct();
    }


}