package com.haswalk.matting.utils;

import java.io.PrintWriter;

public class Inverse {

    public static double[][] apply2(double[][] m) {
        double demon = m[0][0] * m[1][1] - m[0][1] * m[1][0];
        return new double[][]{
                {m[1][1] / demon, -m[0][1] / demon},
                {-m[1][0] / demon, m[0][0] / demon}
        };
    }

    public static double[][] apply3(double[][] m) {
        double demon = (-m[0][2]*m[1][1]*m[2][0]+
                m[0][1]*m[1][2]*m[2][0]+m[0][2]*m[1][0]*m[2][1]-
                m[0][0]*m[1][2]*m[2][1]-m[0][1]*m[1][0]*m[2][2]+
                m[0][0]*m[1][1]*m[2][2]);

        return new double[][]{
                {(-m[1][2]*m[2][1]+m[1][1]*m[2][2]) / demon, (m[0][2]*m[2][1]- m[0][1]*m[2][2]) / demon, (-m[0][2]*m[1][1] + m[0][1]*m[1][2]) / demon},
                {(m[1][2]*m[2][0]- m[1][0]*m[2][2]) / demon, (-m[0][2]*m[2][0]+ m[0][0]*m[2][2])/ demon, (m[0][2]*m[1][0]- m[0][0]*m[1][2]) / demon},
                {(-m[1][1]*m[2][0] + m[1][0]*m[2][1])/ demon, (m[0][1]*m[2][0] - m[0][0]*m[2][1]) / demon, (-m[0][1]*m[1][0]+ m[0][0]*m[1][1]) / demon}};
    }
}
