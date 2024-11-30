import core.Process;
import core.*;

public class Main {
    public static void main(String[] args) {
        model(10_000);
    }


    public static double model(int modelingTime) {
        return createModel(modelingTime).simulate();
    }

    public static Model createModel(int modelingTime) {
        Create create = new Create("Request creator", 10, 3, 0);
        Process processingComp1 = new Process("Request processing on computer 1", 2, 1, Distribution.NONE);
        Process searchComp1 = new Process("Search on computer 1", 18, 2, 1);
        Process transportation = new Process("Transportation", 3, 1, Distribution.NONE);
        Process searchComp2 = new Process("Search on computer 2", 18, 2, 1);
        Process transportationBack = new Process("Transportation back", 3, 1, Distribution.NONE);
        Dispose dispose = new Dispose("Request ended");

        create.setDistribution(Distribution.NORMAL);
        searchComp1.setDistribution(Distribution.NORMAL);
        searchComp2.setDistribution(Distribution.NORMAL);

        create.addRoutes(new Route(processingComp1));
        processingComp1.addRoutes(new Route(searchComp1));
        searchComp1.addRoutes(
                new Route(transportation, 0.5, 0),
                new Route(dispose, 0.5, 0)
        );
        searchComp1.setRouting(Routing.BY_PROBABILITY);

        transportation.addRoutes(new Route(searchComp2));
        searchComp2.addRoutes(new Route(transportationBack));
        transportationBack.addRoutes(new Route(dispose));

        return new Model(modelingTime, create,
                processingComp1,
                searchComp1,
                transportation,
                searchComp2,
                transportationBack,
                dispose);
    }


    public static Model createModelWithoutRandom(int modelingTime) {
        Create create = new Create("Request creator", 10, 0);
        Process processingComp1 = new Process("Request processing on computer 1", 2, 1, Distribution.NONE);
        Process searchComp1 = new Process("Search on computer 1", 18, 1);
        Process transportation = new Process("Transportation", 3, 1, Distribution.NONE);
        Process searchComp2 = new Process("Search on computer 2", 18, 1);
        Process transportationBack = new Process("Transportation back", 3, 1, Distribution.NONE);
        Dispose dispose = new Dispose("Request ended");

        create.setDistribution(Distribution.NONE);
        searchComp1.setDistribution(Distribution.NONE);
        searchComp2.setDistribution(Distribution.NONE);
        processingComp1.setDistribution(Distribution.NONE);
        transportation.setDistribution(Distribution.NONE);
        transportationBack.setDistribution(Distribution.NONE);
        dispose.setDistribution(Distribution.NONE);

        create.addRoutes(new Route(processingComp1));
        processingComp1.addRoutes(new Route(searchComp1));
        searchComp1.addRoutes(
                new Route(transportation, 0.5, 0),
                new Route(dispose, 0.5, 0)
        );
        searchComp1.setRouting(Routing.BY_PROBABILITY);

        transportation.addRoutes(new Route(searchComp2));
        searchComp2.addRoutes(new Route(transportationBack));
        transportationBack.addRoutes(new Route(dispose));

        return new Model(modelingTime, create,
                processingComp1,
                searchComp1,
                transportation,
                searchComp2,
                transportationBack,
                dispose);
    }


}