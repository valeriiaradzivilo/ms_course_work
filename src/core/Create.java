package core;

public class Create extends Element {
    private int failures = 0;
    
    public Create(String name, double delay, double delayDev, double initialTNext) {
        super(name, delay, delayDev);
        super.setTnext(initialTNext);
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setTnext(super.getTcurr() + super.getDelay());
        var createdJob = createJob();
        var nextRoute = super.getNextRoute(createdJob);
        if (nextRoute.getElement() == null) {
            failures++;
        } else {
            nextRoute.getElement().inAct(createdJob);
        }
    }

    protected Job createJob() {
        return new Job(super.getTcurr());
    }
}