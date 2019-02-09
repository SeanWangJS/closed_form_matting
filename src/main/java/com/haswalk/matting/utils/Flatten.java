package com.haswalk.matting.utils;

public class Flatten {

    public static int[] exec(int[][] arr) {
        int row = arr.length;
        int col = arr[0].length;
        int[] result = new int[row * col];
        for (int i = 0; i < row; i++) {
            System.arraycopy(arr[i], 0, result, i * col, col);
        }
        return result;
    }

    public static double[] apply(double[][] arr) {
        int row = arr.length;
        int col = arr[0].length;
        double[] result = new double[row * col];
        for (int i = 0; i < row; i++) {
            System.arraycopy(arr[i], 0, result, i * col, col);
        }
        return result;
    }
}
