package com.haswalk.matting.utils;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class Output {

    public static void print(double[][] m) {
        for (double[] aM : m) {
            for (int j = 0; j < m[0].length; j++) {
                System.out.print(aM[j] + " ");
            }
            System.out.println();
        }
    }
    public static void apply(double[][] m, PrintWriter writer, DecimalFormat formatter) {
        String collect = Stream.of(m)
                .map(arr ->
                        DoubleStream.of(arr)
                                .mapToObj(formatter::format)
                                .collect(Collectors.joining(", "))
                )
                .collect(Collectors.joining("\r\n"));
        writer.print(collect);
        writer.flush();
    }
    public static void apply(double[][] m, PrintWriter writer) {
        String collect = Stream.of(m)
                .map(arr ->
                        DoubleStream.of(arr)
                                .mapToObj(d -> d + "")
                                .collect(Collectors.joining(", "))
                )
                .collect(Collectors.joining("\r\n"));
        writer.print(collect);
        writer.flush();
    }

}
