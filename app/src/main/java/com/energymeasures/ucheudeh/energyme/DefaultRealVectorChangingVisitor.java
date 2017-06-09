package com.energymeasures.ucheudeh.energyme;

import org.apache.commons.math3.linear.RealVectorChangingVisitor;

/**
 * Created by ucheudeh on 5/28/17.
 */

public class DefaultRealVectorChangingVisitor implements RealVectorChangingVisitor {
    @Override
    public void start(int dimension, int start, int end) {
        for( int i = start; i<dimension; i++){
            visit(i, 0);

            }

        }



    @Override
    public double visit(int index, double value) {
        return 0;
    }

    @Override
    public double end() {
        return 0;
    }
}
