package core;

import java.util.Random;

public class FunRand {
    private static final Random random = new Random();

    public static double Normal(double mean, double stddev) {
        return mean + stddev * random.nextGaussian();
    }
}