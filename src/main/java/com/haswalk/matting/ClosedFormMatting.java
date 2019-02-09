package com.haswalk.matting;

import com.haswalk.matting.op.Div;

public class ClosedFormMatting {

    public static boolean run(String sourcePath, String trimapPath, String savePath) {
        Image image = Image.load(sourcePath);
        Image trimap = Image.load(trimapPath);

        double[][][] rgb = {
                Div.apply(image.getR(), 255),
                Div.apply(image.getG(), 255),
                Div.apply(image.getB(), 255)};
        double[][] gray = Div.apply(trimap.getR(), 255);
        double[][] solu;
        try {
            solu = new TrimapOptimizeLaplacian(image.getWidth(), image.getHeight(), rgb, gray)
                    .exec();
        }catch (Exception e) {
            return false;
        }

        int[][] a = new int[solu.length][solu[0].length];
        for (int i = 0; i < solu.length; i++) {
            for (int j = 0; j < solu[0].length; j++) {
                a[i][j] = (int)(solu[i][j] * 255);
            }
        }
        int[][] b = image.getB();
        int[][] r = image.getR();
        int[][] g = image.getG();
        new Image(image.getWidth(), image.getHeight(), r, g, b, a)
                .write(savePath, "png");
        return true;
    }

}
