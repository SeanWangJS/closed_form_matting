package com.haswalk.matting.utils;

public class Transpose {

    public static double[][] apply(double[][] m) {
        int row = m.length;
        int col = m[0].length;
        double[][] mt = new double[col][row];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                mt[j][i] = m[i][j];
            }
        }
        return mt;
    }
}
