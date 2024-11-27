package core;

import java.util.Random;

public class FunRand {
    /**
     * Generates a random value according to an exponential
     * distribution
     *
     * @param timeMean mean value
     * @return a random value according to an exponential
     * distribution
     */
    public static double Exponential(double timeMean) {
        double a = 0;
        while (a == 0) {
            a = Math.random();
        }
        a = -timeMean * Math.log(a);
        return a;
    }

    /**
     * Generates a random value according to a uniform
     * distribution
     *
     * @param timeMin min value
     * @param timeMax max value
     * @return a random value according to a uniform distribution
     */
    public static double Uniform(double timeMin, double timeMax) {
        double a = 0;
        while (a == 0) {
            a = Math.random();
        }
        a = timeMin + a * (timeMax - timeMin);
        return a;
    }

    /**
     * Generates a random value according to a normal (Gauss)
     * distribution
     *
     * @param timeMean      mean value
     * @param timeDeviation standard deviation of normal distribution
     * @return a random value according to a normal (Gauss) distribution
     */
    public static double Normal(double timeMean, double timeDeviation) {
        double a;
        Random r = new Random();
        a = timeMean + timeDeviation * r.nextGaussian();
        return a;
    }

    /**
     * Generates a random value according to Erlang distribution
     *
     * @param timeMean mean value
     * @param shape    shape parameter (k) of the Erlang distribution
     * @return a random value according to Erlang distribution
     */
    public static double Erlang(double timeMean, double shape) {
        double a = 0;
        for (int i = 0; i < shape; i++) {
            a += Math.log(Math.random());
        }
        return (-1 / (timeMean / shape)) * a;
    }
}