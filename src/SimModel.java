import additional.Process;
import additional.*;

import java.util.ArrayList;


public class SimModel {

    public static void main(String[] args) {
        Create c = new Create(2.0);
        additional.Process p = new Process(1.0);
        c.setName("CREATOR");
        p.setName("PROCESSOR");
        p.setMaxqueue(5);
        c.setDistribution(Distribution.EXP);
        p.setDistribution(Distribution.EXP);

        ArrayList<Element> list = new ArrayList<>();
        list.add(c);
        list.add(p);

        Model model = new Model(list);
        model.simulate(1000.0);

    }
}