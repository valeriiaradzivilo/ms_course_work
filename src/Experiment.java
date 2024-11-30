import core.Element;
import core.Model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Experiment {


    public static void main(String[] args) {
//        getMeanStandDevVariance(50);
        getMeanTimeInSystemStatistics();
    }

    private static void getMeanTimeInSystemStatistics() {
        for (int i = 0; i < 3; i++) {
            Element.setNextId(0);
            Model model = Main.createModel(100_000);
            model.simulate();
            saveDataToCSV("meanTimeInSystem" + i + ".csv", model.getMeanTimeInSystemStatistics());
        }

    }

    private static void getMeanStandDevVariance(int N) {
        ArrayList<Double> timeInSystem = new ArrayList<>();
        for (int i = 0; i <= N; i++) {
            Element.setNextId(0);
            double timeInSystemModel = Main.model(10000);
            timeInSystem.add(timeInSystemModel);
        }

        double mean = 0.0;
        double sum = 0.0;
        double variance = 0.0;
        double standartDeviation = 0.0;
        for (Double aDouble : timeInSystem) {
            sum += aDouble;
        }
        mean = sum / timeInSystem.size();
        for (Double aDouble : timeInSystem) {
            variance += Math.pow(aDouble - mean, 2);
        }
        variance = variance / timeInSystem.size();
        standartDeviation = Math.sqrt(variance);
        System.out.println("\n\n_______TIME IN SYSTEM CALCULATIONS_______");
        System.out.println("Mean: " + mean);
        System.out.println("Variance: " + variance);
        System.out.println("Standart Deviation: " + standartDeviation);
        double count = Math.pow(standartDeviation, 2.0) / (Math.pow(mean * 0.05, 2.0) * (1 - 0.95));
        System.out.println("Number of runs needed : " + count);
    }


    private static void saveDataToCSV(String filename, Map<Double, Double> meanTimeInSystem) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.append("Time,MeanTimeInSystem\n");
            for (Map.Entry entry : meanTimeInSystem.entrySet()) {
                writer.append(entry.getKey().toString())
                        .append(",")
                        .append(entry.getValue().toString())
                        .append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}