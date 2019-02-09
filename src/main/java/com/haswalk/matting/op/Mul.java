package com.haswalk.matting.op;

public class Mul {

    public static double[][] exec(double[][] m1, double[][] m2) {
        int m = m1.length;
        int n = m2.length;
        int k = m2[0].length;
        double[][] result = new double[m][k];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < k; j++) {
                for (int l = 0; l < n; l++) {
                    result[i][j] += m1[i][l] * m2[l][j];
                }
            }
        }
        return result;
    }

    public static double[][] apply(double[][] m, double f) {
        double[][] result = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                result[i][j] = m[i][j] * f;
            }
        }
        return result;
    }

    public static double[] apply(double[] arr, double[][] m) {
        double[] result = new double[m[0].length];
        for (int i = 0; i < m[0].length; i++) {
            for (int j = 0; j < arr.length; j++) {
                result[i] += arr[j] * m[j][i];
            }
        }
        return result;
    }

    public static double apply(double[] ar1, double[] ar2) {
        double result = 0;
        for (int i = 0; i < ar1.length; i++) {
            result += (ar1[i] * ar2[i]);
        }
        return result;
    }

}
