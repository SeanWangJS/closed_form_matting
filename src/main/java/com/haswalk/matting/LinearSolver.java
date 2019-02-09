package com.haswalk.matting;

import com.haswalk.matting.op.Plus;
import com.haswalk.linalg.mat.CSRMatrix;
import com.haswalk.linalg.solver.SparseSolve;

public class LinearSolver {

    public static double[] exec(CSRMatrix csrmat, double[] rhs) {
        double[] solu = new double[rhs.length];
        SparseSolve.exec(
                csrmat.getRowNum(),
                Plus.apply(csrmat.getRowIndex(), 1),
                Plus.apply(csrmat.getColumns(), 1),
                csrmat.getValues().length,
                csrmat.getValues(),
                rhs,
                solu
        );
        return solu;
    }

}
