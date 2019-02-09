package com.haswalk.matting;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Image {

    private int[][] r;
    private int[][] g;
    private int[][] b;
    private int[][] alpha;
    private int width;
    private int height;

    public Image(int witdth, int height, int[][] r, int[][] g, int[][] b) {
        this.width = witdth;
        this.height = height;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Image(int width, int height, double[][] alpha) {
        this.width = width;
        this.height = height;
        r = new int[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                r[i][j] = (int)(alpha[i][j] * 255);
            }
        }
        g = r;
        b = r;
    }

    public Image(int width, int height, int[][] r, int[][] g, int[][] b, int[][] a) {
        this(width, height, r, g, b);
        this.alpha = a;
    }

    public int[][] getR() {
        return r;
    }
    public int[][] getG() {
        return g;
    }
    public int[][] getB() {
        return b;
    }

    public static Image loadRGBA(String path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] r = new int[height][width];
        int[][] g = new int[height][width];
        int[][] b = new int[height][width];
        int[][] a = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = image.getRGB(j, i);
                a[i][j] = (pixel >> 24) & 0xff;
                r[i][j] = (pixel >> 16) & 0xff;
                g[i][j] = (pixel >> 8) & 0xff;
                b[i][j] = pixel & 0xff;
            }
        }
        return new Image(width, height, r, g, b, a);
    }

    public static Image load(String path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] r = new int[height][width];
        int[][] g = new int[height][width];
        int[][] b = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixels = image.getRGB(j, i);
                r[i][j] = (pixels >> 16) & 0xff;
                g[i][j] = (pixels >> 8) & 0xff;
                b[i][j] = pixels & 0xff;
            }
        }
        return new Image(width, height, r, g, b);
    }

    public void write(String path, String ext) {

        BufferedImage buffer = new BufferedImage(width, height,
                alpha == null? BufferedImage.TYPE_INT_RGB: BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if(alpha == null) {
                    int rgb = r[i][j];
                    rgb = (rgb << 8) + g[i][j];
                    rgb = (rgb << 8) + b[i][j];
                    buffer.setRGB(j, i, rgb);
                }else {
                    int rgba = alpha[i][j];
                    rgba = (rgba << 8) + r[i][j];
                    rgba = (rgba << 8) + g[i][j];
                    rgba = (rgba << 8) + b[i][j];
                    buffer.setRGB(j, i, rgba);
                }

            }
        }
        try {
            ImageIO.write(buffer, ext, new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printRGB() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int R = r[i][j];
                int G = g[i][j];
                int B = b[i][j];
                System.out.print("[" + R  + ", " + G  + ", " + B + "]  ");
            }
            System.out.println();
        }
    }

    public void printRGBA() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int R = r[i][j];
                int G = g[i][j];
                int B = b[i][j];
                int A = alpha[i][j];
                System.out.print("[" + R  + ", " + G  + ", " + B + ", " + A +"]  ");
            }
            System.out.println();
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
