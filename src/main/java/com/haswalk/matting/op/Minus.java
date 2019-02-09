package com.haswalk.matting.op;

public class Minus {

    public static double[] minus(double[] ar1, double[] ar2) {
        double[] result = new double[ar1.length];
        for (int i = 0; i < ar1.length; i++) {
            result[i] = ar1[i] - ar2[i];
        }
        return result;
    }

    public static double[][] apply(double[][] m1, double[][] m2) {
        double[][] result = new double[m1.length][m1[0].length];
        for (int i = 0; i < m1.length; i++) {
            for (int j = 0; j < m1[0].length; j++) {
                result[i][j] = m1[i][j] - m2[i][j];
            }
        }
        return result;
    }

    public static int[][] apply(int[][] m1, int[][] m2) {
        int[][] result = new int[m1.length][m1[0].length];
        for (int i = 0; i < m1.length; i++) {
            for (int j = 0; j < m1[0].length; j++) {
                result[i][j] = m1[i][j] - m2[i][j];
            }
        }
        return result;
    }

}
