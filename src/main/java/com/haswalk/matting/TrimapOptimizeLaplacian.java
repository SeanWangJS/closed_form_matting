package com.haswalk.matting;

import com.haswalk.linalg.mat.CSRMatrix;
import com.haswalk.linalg.mat.IdxMatrix;
import com.haswalk.matting.op.Minus;
import com.haswalk.matting.op.Mul;
import com.haswalk.matting.op.Plus;
import com.haswalk.matting.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class TrimapOptimizeLaplacian {

    private Logger logger = LoggerFactory.getLogger(TrimapOptimizeLaplacian.class);

    private int width;
    private int height;

    private List<double[]> rgb;

    private int win_w_num;
    private int win_h_num;

    private final int win_diam = 3;
    private final int win_rad = 1;
    private final int win_size = 9;
    private final double eps = Math.pow(10, -7);

    private int[] confidence;
    private double[] prior;
    private double[] rhs;

    private CSRMatrix csrmat;
    private Map<Integer, Integer> ridx_idx_map;
    private Map<Integer, Integer> idx_ridx_map;
    private int[] indexes;
    private int unknownLen;

    public TrimapOptimizeLaplacian(int width, int height, double[][][] image, double[][] trimap) {

        assert width == image[0][0].length && width == trimap[0].length
                && height == image[0].length && height == trimap.length;

        logger.info("initialize...");
        long start = System.currentTimeMillis();
        this.height = height;
        this.width = width;
        int len = width * height;
        this.win_w_num = width - 2 * win_rad;
        this.win_h_num = height - 2 * win_rad;

        rgb = IntStream.range(0, height)
                .boxed()
                .flatMap(i -> IntStream.range(0, width)
                        .mapToObj(j -> new double[]{image[0][i][j],
                                image[1][i][j],
                                image[2][i][j]
                        })
                )
                .collect(Collectors.toList());

        prior = Flatten.apply(trimap);
        confidence = DoubleStream.of(prior)
                .mapToInt(d -> (d >= 0.1 && d <= 0.9) ? 0 : 100)
                .toArray();

        indexes = IntStream.range(0, len)
                .filter(i -> confidence[i] == 0)
                .toArray();

        unknownLen = indexes.length;

        ridx_idx_map = IntStream.range(0, unknownLen)
                .boxed()
                .collect(Collectors.toMap(
                        i -> i,
                        i -> indexes[i]
                ));
        idx_ridx_map = IntStream.range(0, unknownLen)
                .boxed()
                .collect(Collectors.toMap(
                        i -> indexes[i],
                        i -> i
                ));
        logger.info("time: " + ((System.currentTimeMillis() - start) / 1000.0 + "s"));
    }

    public double[][] exec() {
        laplacian();
        return solve();
    }

    private void laplacian() {
        logger.info("compute laplacian...");
        long start = System.currentTimeMillis();

        List<List<int[]>> winInds = winInds();
        Map<Long, Double> idxValue = new HashMap<>();
        rhs = new double[unknownLen];

        for (int p = 0; p < win_h_num; p++) {
            for (int q = 0; q < win_w_num; q++) {
                int[] win = winInds.get(p).get(q);
                List<double[]> Is = IntStream.of(win)
                        .mapToObj(k -> rgb.get(k))
                        .collect(Collectors.toList());
                List<double[]> Is_mu = Is.stream()
                        .map(I -> Minus.minus(I, Mean.apply(Is)))
                        .collect(Collectors.toList());

                double[][] inverse = Inverse.apply3(
                        Plus.apply(
                                CovarianceMatrix.apply(Is),
                                Mul.apply(Identity.matrix(1, win_diam), eps / win_size)
                        )
                );
                for (int k = 0; k < win_size; k++) {
                    for (int l = 0; l < win_size; l++) {
                        int i = win[k];
                        if(confidence[i] == 100) {
                            continue;
                        }
                        int j = win[l];
                        double temp = (1 + Mul.apply(
                                Mul.apply(
                                        Is_mu.get(k),
                                        inverse
                                ),
                                Is_mu.get(l)
                        )) / win_size;

                        double value = ((i == j) ? 1 : 0) - temp;
                        if(confidence[j] == 100) {
                            rhs[idx_ridx_map.get(i)] += - value * prior[j];
                            continue;
                        }
                        long index = ijToIndex(idx_ridx_map.get(i), idx_ridx_map.get(j), unknownLen);
                        idxValue.merge(index, value, (a, b) -> a + b);
                    }
                }
            }
        }

        IdxMatrix idx = new IdxMatrix(idxValue, unknownLen, unknownLen, 'F')
                .upperTriangle();

        csrmat = idx.toCSR();

        logger.info("time: " + ((System.currentTimeMillis() - start) / 1000.0 + "s"));

    }
    private final long ijToIndex(int i, int j, int colLen) {
        return (long)i * colLen + j;
    }
    private List<List<int[]>> winInds() {

        List<int[]> collect = IntStream.range(win_rad, win_h_num + win_rad)
                .map(i -> i * width + win_rad)
                .flatMap(i -> IntStream.range(0, win_w_num).map(j -> j + i))
                .mapToObj(this::win)
                .collect(Collectors.toList());

        return IntStream.range(0, win_h_num)
                .mapToObj(i -> collect.subList(i * win_w_num, (i + 1) * win_w_num))
                .collect(Collectors.toList());

    }

    private int[] win(int w) {
        int i = w / width;
        int j = w - i * width;

        int[] win = new int[win_size];
        for (int k = 0; k < win_diam; k++) {
            for (int l = 0; l < win_diam; l++) {
                int ii = i - win_rad + k;
                int jj = j - win_rad + l;
                win[k * win_diam + l] = ii * width + jj;
            }
        }
        return win;
    }

    private double[][] solve() {
        logger.info("solving...");
        long start = System.currentTimeMillis();
        double[] solu = LinearSolver.exec(csrmat, rhs);
        ridx_idx_map.forEach((ridx, idx) -> prior[idx] = solu[ridx]);

        double[][] gray = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double alpha = prior[i * width + j];
                if(alpha > 1) {
                    alpha = 1;
                }else if(alpha < 0) {
                    alpha = 0;
                }
                gray[i][j] = alpha;
            }
        }
        logger.info("time: " + ((System.currentTimeMillis() - start) / 1000.0 + "s"));
        return gray;
    }

}
