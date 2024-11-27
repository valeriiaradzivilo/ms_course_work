import core.Process;
import core.*;

public class Main {
    public static void main(String[] args) {
        Create create = new Create("Request creator", 10, 3, 0);
        Process processingComp1 = new Process("Request processing on computer 1", 2, 1, Distribution.NONE);
        Process searchComp1 = new Process("Search on computer 1", 18, 2, 1);
        Process transportation = new Process("Transportation", 3, 1, Distribution.NONE);
        Process processingComp2 = new Process("Request processing on computer 2", 2, 1, Distribution.NONE);
        Process searchComp2 = new Process("Search on computer 2", 18, 2, 1);
        Dispose dispose = new Dispose("Request ended");

        create.setDistribution(Distribution.NORMAL);
        searchComp1.setDistribution(Distribution.NORMAL);
        searchComp2.setDistribution(Distribution.NORMAL);


        create.addRoutes(
                new Route(processingComp1)
        );
        processingComp1.addRoutes(
                new Route(searchComp1)
        );
        searchComp1.addRoutes(
                new Route(transportation, 0.5, 0),
                new Route(dispose, 0.5, 0)
        );
        searchComp1.setRouting(Routing.BY_PROBABILITY);
        transportation.addRoutes(
                new Route(processingComp2)
        );
        processingComp2.addRoutes(
                new Route(searchComp2)
        );
        searchComp2.addRoutes(
                new Route(dispose)
        );


        Model model = new Model(create,
                processingComp1,
                searchComp1,
                transportation,
                processingComp2,
                searchComp2,
                dispose);

        model.simulate(1000);


    }


}