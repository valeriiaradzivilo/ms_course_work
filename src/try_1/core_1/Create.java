package try_1.core_1;

public class Create extends Element {
    private int failures = 0;

    public Create(String name, double delay) {
        super(name, delay);
        super.setTNext(0.0);
    }

    public Create(String name, double delay, double initialTNext) {
        super(name, delay);
        super.setTNext(initialTNext);
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setTNext(super.getTCurr() + super.getDelay());
        var createdJob = createJob();
        var nextRoute = super.getNextRoute(createdJob);
        if (nextRoute.getElement() == null || nextRoute.isBlocked(createdJob)) {
            failures++;
        } else {
            nextRoute.getElement().inAct(createdJob);
        }
    }

    protected Job createJob() {
        return new Job(super.getTCurr());
    }
}