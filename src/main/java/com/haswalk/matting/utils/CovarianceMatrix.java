package com.haswalk.matting.utils;

import java.util.List;

public class CovarianceMatrix {

    public static double[][] exec(double[][] arr) {
        int row = arr.length;
        double[][] matrix = new double[row][row];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < row; j++) {
                matrix[i][j] = Covariance.exec(arr, i, j);
            }
        }
        return matrix;
    }

    public static double[][] apply(List<double[]> list) {
        double[][] arr = new double[list.get(0).length][list.size()];
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < arr.length; j++) {
                arr[j][i] = list.get(i)[j];
            }
        }
        double[][] result = new double[arr.length][arr.length];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result.length; j++) {
                result[i][j] = Covariance.exec(arr, i, j);
            }
        }
        return result;
    }

}
