package com.haswalk.matting.utils;

public class Reshape {

    public static double[][] apply(double[] arr, int row, int col) {
        double[][] result = new double[row][col];
        for (int i = 0; i < row; i++) {
            System.arraycopy(arr, i * col, result[i], 0, col);
        }
        return result;
    }
    public static int[][] apply(int[] arr, int row, int col) {
        int[][] result = new int[row][col];
        for (int i = 0; i < row; i++) {
            System.arraycopy(arr, i * col, result[i], 0, col);
        }
        return result;
    }
}
