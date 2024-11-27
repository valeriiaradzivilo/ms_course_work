import core.Create;
import core.Distribution;
import core.Process;


public class SimModel {

    public static void main(String[] args) {
//        Create c = new Create(2.0);
//        core.Process p = new Process(1.0);
//        c.setName("CREATOR");
//        p.setName("PROCESSOR");
//        p.setMaxqueue(5);
//        c.setDistribution(Distribution.EXP);
//        p.setDistribution(Distribution.EXP);
//
//        ArrayList<Element> list = new ArrayList<>();
//        list.add(c);
//        list.add(p);
//
//        Model model = new Model(list);
//        model.simulate(1000.0);


        Create create = new Create(10, Distribution.NORM, 3);
        Process p = new Process();

    }
}