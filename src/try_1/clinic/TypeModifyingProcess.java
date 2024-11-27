package try_1.clinic;

import java.util.HashMap;

public class TypeModifyingProcess extends try_1.core_1.Process {

    private HashMap<Integer, Integer> typeModifyingMap;

    public TypeModifyingProcess(String name, double delayMean, int channelsNum) {
        super(name, delayMean, channelsNum);
    }

    public TypeModifyingProcess(String name, double delayMean, double delayDev, int channelsNum) {
        super(name, delayMean, delayDev, channelsNum);
    }

    public void setTypeModifyingMap(int[] types, int[] modifiedTypes) {
        this.typeModifyingMap = new HashMap<>();
        for (int i = 0; i < types.length; i++) {
            this.typeModifyingMap.put(types[i], modifiedTypes[i]);
        }
    }

    @Override
    protected void processCurrentJobs() {
        var channelsWithMinTNext = getChannelsWithMinTNext();
        for (var channel : channelsWithMinTNext) {
            var job = channel.getCurrentJob();
            var patient = (Patient) job;
            if (typeModifyingMap.get(patient.getType()) != null) {
                patient.setType(typeModifyingMap.get(patient.getType()));
            }
            var nextRoute = getNextRoute(job);
            if (nextRoute.isBlocked(job)) {
                continue;
            }
            if (nextRoute.getElement() != null) {
                job.setTimeOut(super.getTCurr());
                nextRoute.getElement().inAct(job);
            }
            channel.setCurrentJob(null);
            channel.setTNext(Double.MAX_VALUE);
            changeQuantity(1);
            totalLeaveTime += super.getTCurr() - previousLeaveTime;
            previousLeaveTime = super.getTCurr();
        }
    }
}