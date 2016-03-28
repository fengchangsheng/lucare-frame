package com.lucare.common.utils;

import java.math.BigDecimal;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class MathUtils {

    public static BigDecimal ZERO_DECIMAL = new BigDecimal(0);

    public MathUtils() {
    }

    public static BigDecimal add(BigDecimal one, BigDecimal other) {
        return one == null && other == null?ZERO_DECIMAL:(one == null?other:(other == null?one:one.add(other)));
    }

    public static double add(double... args) {
        double sum = 0.0D;
        if(args != null) {
            for(int i = 0; i < args.length; ++i) {
                sum += args[i];
            }
        }

        return roundHalfUp(sum);
    }

    public static double roundHalfUp(BigDecimal decimal) {
        return roundHalfUp(decimal, 2);
    }

    public static double roundHalfUp(BigDecimal decimal, int scale) {
        return decimal.setScale(scale, 4).doubleValue();
    }

    public static double roundHalfUp(double value) {
        return roundHalfUp(value, 2);
    }

    public static double roundHalfUp(double value, int scale) {
        BigDecimal decimal = new BigDecimal(value);
        return decimal.setScale(scale, 4).doubleValue();
    }

    public static double roundDown(BigDecimal decimal) {
        return roundDown(decimal, 2);
    }

    public static double roundDown(BigDecimal decimal, int scale) {
        return decimal.setScale(scale, 1).doubleValue();
    }

    public static double roundDown(double value) {
        return roundDown(value, 2);
    }

    public static double roundDown(double value, int scale) {
        BigDecimal decimal = new BigDecimal(value);
        return decimal.setScale(scale, 1).doubleValue();
    }

    public static BigDecimal roundHalfUpForDecimal(double value) {
        BigDecimal decimal = new BigDecimal(value);
        return decimal.setScale(2, 4);
    }

    public static double score(double child, double mother) {
        return Math.floor(child * 100.0D / mother);
    }

    public static void main(String[] args) {
        System.out.println(roundHalfUp(10.0D));
    }
}
