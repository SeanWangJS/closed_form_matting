package com.haswalk.matting.utils;

public class Covariance {

    public static double exec(double[][] arr, int r1, int r2) {
        int n = arr[0].length;
        double[] means = Mean.exec(arr, r1, r2);
        double[] prod = new double[n];
        for (int i = 0; i < n; i++) {
            prod[i] = arr[r1][i] * arr[r2][i];
        }
        return (Mean.exec(prod) - means[0] * means[1]);
    }

    public static double exec(double[] ar1, double[] ar2) {
        int n = ar1.length;
        double[] prod = new double[n];
        for (int i = 0; i < n; i++) {
            prod[i] = ar1[i] * ar2[i];
        }
        return (Mean.exec(prod) - Mean.exec(ar1) * Mean.exec(ar2))
                * n / (n - 1.0);
    }
}
