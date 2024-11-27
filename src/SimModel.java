import core.Process;
import core.*;
import core.type.Distribution;
import core.type.Routing;

import java.util.ArrayList;


public class SimModel {

    public static void main(String[] args) {

        ArrayList<Element> list = new ArrayList<>();
        Create create = new Create("Create", 0);

        Process incomingRequests = new Process("Надходження запитів", 10, Distribution.NORM, 3);


        Process comp1Processing = new Process("Обробка на 1 комп'ютері", 2, Distribution.UNKNOWN);


//        comp1Processing.setMaxqueue(1);
        Process comp1Search = new Process("Пошук на 1 комп'ютері", 18, Distribution.NORM, 2);
//        comp1Search.setMaxqueue(1);

        Process transmission = new Process("Передача по каналу зв'язку", 2);
//        transmission.setMaxqueue(1);
        Process comp2Processing = new Process("Обробка на 2 комп'ютері", 2);
//        comp2Processing.setMaxqueue(1);
        Process comp2Search = new Process("Пошук на 2 комп'ютері", 18, Distribution.NORM, 2);
//        comp2Search.setMaxqueue(1);
        Dispose dispose = new Dispose("Запит оброблено");


        incomingRequests.setNextRoute(comp1Processing);
        create.setNextElement(comp1Processing);
        comp1Processing.setNextElement(comp1Search);
        comp1Search.setRouting(Routing.BY_PROBABILITY);
        comp1Search.addRoutes(
                new Route(transmission, 0.5),
                new Route(dispose, 0.5)
        );
        transmission.setNextElement(comp2Processing);
        comp2Processing.setNextElement(comp2Search);
        comp2Search.setNextElement(dispose);


        list.add(create);
        list.add(incomingRequests);
        list.add(comp1Processing);
        list.add(comp1Search);
        list.add(transmission);
        list.add(comp2Processing);
        list.add(comp2Search);
        list.add(dispose);


        Model model = new Model(list);
        model.simulate(1000.0);

    }
}