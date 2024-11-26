package core;

import java.util.Random;

public class FunRand {
    private static final Random random = new Random();

    public static double Exp(double mean) {
        return -mean * Math.log(random.nextDouble());
    }

    public static double Norm(double mean, double stddev) {
        return mean + stddev * random.nextGaussian();
    }

    public static double Unif(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}