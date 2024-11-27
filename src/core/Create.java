package core;

import core.type.Distribution;

public class Create extends Element {

    public Create(double delay) {
        super(delay);
    }

    public Create(double delay, Distribution distribution, double deviation) {
        super(delay, distribution, deviation);
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setTnext(super.getTcurr() + super.getDelay());
        super.getNextElement().inAct();
    }

}