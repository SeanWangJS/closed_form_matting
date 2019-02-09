package com.haswalk.matting.op;

public class Abs {

    public static int[][] apply(int[][] arr) {
        int[][] result = new int[arr.length][arr[0].length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                result[i][j] = Math.abs(arr[i][j]);
            }
        }
        return result;
    }
}
