package com.haswalk.matting.utils;

import java.util.List;
import java.util.stream.DoubleStream;

public class Mean {

    public static double[] exec(double[][] arr, int... rows) {
        double[] result = new double[rows.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = DoubleStream.of(arr[rows[i]]).summaryStatistics().getAverage();
        }
        return result;
    }

    public static double exec(double[] arr) {
        return DoubleStream.of(arr).summaryStatistics().getAverage();
    }

    public static double[] apply(List<double[]> list) {
        double[] result = new double[list.get(0).length];
        for (double[] ds : list) {
            for (int i = 0; i < ds.length; i++) {
                result[i] += ds[i];
            }
        }
        for (int i = 0; i < result.length; i++) {
            result[i] /= list.size();
        }
        return result;
    }

}
