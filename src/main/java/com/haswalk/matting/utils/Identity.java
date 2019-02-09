package com.haswalk.matting.utils;

public class Identity {

    public static double[][] matrix(double value, int n) {
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = (i == j)? value: 0;
            }
        }
        return result;
    }

}
