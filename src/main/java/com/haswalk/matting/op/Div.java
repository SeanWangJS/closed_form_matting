package com.haswalk.matting.op;

public class Div {

    public static double[][] apply(int[][] arr, double f) {
        double[][] result = new double[arr.length][arr[0].length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                result[i][j] = arr[i][j] / f;
            }
        }
        return result;
    }
    public static double[][] apply(double[][] arr, double f) {
        double[][] result = new double[arr.length][arr[0].length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                result[i][j] = arr[i][j] / f;
            }
        }
        return result;
    }

    public static double[] apply(int[] arr, double f) {
        double[] result = new double[arr.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = arr[i] / f;
        }
        return result;
    }

}
