import core.Process;
import core.*;
import core.type.Distribution;

import java.util.ArrayList;


public class SimModel {

    public static void main(String[] args) {

        ArrayList<Element> list = new ArrayList<>();
        Create create = new Create(10, Distribution.NORM, 3);

        Process comp1Processing = new Process(2); // я не розумію який тут розподіл і чи він є
        Process comp1Search = new Process(18, Distribution.NORM, 2);

        Process transmission = new Process(2); // я не розумію який тут розподіл і чи він є

        Process comp2Processing = new Process(2); // я не розумію який тут розподіл і чи він є
        Process comp2Search = new Process(18, Distribution.NORM, 2);

        Dispose dispose = new Dispose("Запит оброблено");

        create.setNextElement(comp1Processing);
        comp1Processing.setNextElement(comp1Search);
        comp1Search.addRoutes(
                new Route(transmission, 0.5),
                new Route(dispose, 0.5)
        );
        transmission.setNextElement(comp2Processing);
        comp2Processing.setNextElement(comp2Search);
        comp2Search.setNextElement(dispose);


        list.add(create);
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