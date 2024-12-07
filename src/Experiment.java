import core.Element;
import core.Model;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Experiment {

    public static void main(String[] args) {
        getNumberOfRunsNeeded();
        getMeanTimeInSystemStatistics();
        getListTimeInSystem();
        meanAndStdevTimeAnalyse();
        factorAnalysis();
    }

    private static void getListTimeInSystem() {
        Element.setNextId(0);
        Model model = Main.createModelForTask(10_000);
        model.simulate();

        try (FileWriter writer = new FileWriter("timeInSystem.csv")) {
            writer.append("TimeInSystem\n");
            for (Double entry : model.getTimeInSystemForEachProcess()) {
                writer.append(entry.toString())
                        .append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getMeanTimeInSystemStatistics() {
        for (int i = 0; i < 5; i++) {
            Element.setNextId(0);
            Model model = Main.createModelForTask(10_000);
            model.simulate();
            saveDataToCSV("meanTimeInSystem" + i + ".csv", model.getMeanTimeInSystemStatistics());

            Model model2 = Main.createModel(10_000, 15);
            model2.simulate();
            saveDataToCSV("meanTimeInSystem" + i + ".csv", model2.getMeanTimeInSystemStatistics());

        }

    }

    private static void getNumberOfRunsNeeded() {
        ArrayList<Double> timeInSystem = new ArrayList<>();
        for (int i = 0; i <= 50; i++) {
            Element.setNextId(0);
            double timeInSystemModel = Main.model(10_000);
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
        System.out.println("\n\n-------NUMBER OF RUNS NEEDED RESULT-------");
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

    private static void meanAndStdevTimeAnalyse() {
        NumberFormat formatter = new DecimalFormat("#0.0000");
        Element.setNextId(0);
        Model model = Main.createModelForTask(50_000);
        model.simulate();
        List<Double> timeInSystemForEachRequest = model.getTimeInSystemForEachProcess();

        double sum = timeInSystemForEachRequest.stream().mapToDouble(Double::doubleValue).sum();
        double mean = sum / timeInSystemForEachRequest.size();
        double variance = 0.0;
        double standartDeviation = 0.0;

        for (Double aDouble : timeInSystemForEachRequest) {
            variance += Math.pow(aDouble - mean, 2);
        }
        variance = variance / timeInSystemForEachRequest.size();
        standartDeviation = Math.sqrt(variance);
        System.out.println("\n\n_______TIME IN SYSTEM CALCULATIONS_______");
        System.out.println("Mean: " + formatter.format(mean));
        System.out.println("Standart Deviation: " + formatter.format(standartDeviation));

    }


    private static void factorAnalysis() {
        List<Double> timeInSystemFor7 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Element.setNextId(0);
            Model model = Main.createModel(50_000, 7);
            model.simulate();
            final double meanTimeInSystem = model.getMeanTimeInSystem();
            timeInSystemFor7.add(meanTimeInSystem);
        }
        double meanFor7 = timeInSystemFor7.stream().mapToDouble(Double::doubleValue).sum() / timeInSystemFor7.size();
        List<Double> timeInSystemFor13 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Element.setNextId(0);
            Model model = Main.createModel(50_000, 13);
            model.simulate();
            final double meanTimeInSystem = model.getMeanTimeInSystem();
            timeInSystemFor13.add(meanTimeInSystem);
        }
        double meanFor13 = timeInSystemFor13.stream().mapToDouble(Double::doubleValue).sum() / timeInSystemFor13.size();


        double y1 = 0.0;
        for (Double aDouble : timeInSystemFor7) {
            y1 += aDouble;
        }
        y1 = 0.1 * y1;

        double y2 = 0.0;
        for (Double aDouble : timeInSystemFor13) {
            y2 += aDouble;
        }
        y2 = 0.1 * y2;

        double y = 0.5 * (y1 + y2);

        double Sfact = 10 * (Math.pow(y1 - y, 2) + Math.pow(y2 - y, 2));
        double Sleft = 0.0;

        for (Double aDouble : timeInSystemFor7) {
            Sleft += Math.pow(aDouble - y1, 2);
        }
        for (Double aDouble : timeInSystemFor13) {
            Sleft += Math.pow(aDouble - y2, 2);
        }

        double dfact = Sfact;
        double dleft = Sleft / (2 * 9);

        double Fisher = dfact / dleft;
        double FisherCritical = 4.41;


        System.out.println("\n\n_______FACTOR ANALYSIS_______");
        System.out.println("Time for request intensity 7: " + meanFor7);
        System.out.println("Time for request intensity 13: " + meanFor13);
        System.out.println("Y1: " + y1);
        System.out.println("Y2: " + y2);
        System.out.println("Y: " + y);
        System.out.println("Sfact: " + Sfact);
        System.out.println("Sleft: " + Sleft);
        System.out.println("Dfact: " + dfact);
        System.out.println("Dleft: " + dleft);
        System.out.println("Fisher: " + Fisher);
        System.out.println("FisherCritical: " + FisherCritical);

        if (Fisher > FisherCritical) {
            System.out.println("Fisher > FisherCritical, so the factor has effect on the result");
        } else {
            System.out.println("Fisher < FisherCritical, so the factor has NO effect on the result");
        }
    }
}