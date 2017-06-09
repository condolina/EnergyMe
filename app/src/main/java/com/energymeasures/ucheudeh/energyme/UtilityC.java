package com.energymeasures.ucheudeh.energyme;
import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.DefaultRealMatrixChangingVisitor;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.RealVectorChangingVisitor;

import java.util.concurrent.ThreadLocalRandom;
/**
 * Created by ucheudeh on 5/27/17.
 */

class UtilityC {
    static Array2DRowRealMatrix fillMatrix(Array2DRowRealMatrix aMatrix, final double bound) {
        // iterates a matrix and changes the value of each element from 0 to a random number within 0.0 and bound (inclusive)
        aMatrix.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor(){
            @Override
            public double visit(int row, int column, double value){
                return ThreadLocalRandom.current().nextDouble(bound);
            }


        });
        Log.d("FirstElement", Double.toString(aMatrix.getEntry(1,1)));
        return aMatrix;
    }

    static ArrayRealVector fillVector(ArrayRealVector aVector, final double bound) {
        // iterates a matrix and changes the value of each element from 0 to a random number within 0.0 and bound (inclusive)
        aVector.walkInOptimizedOrder(new DefaultRealVectorChangingVisitor(){
            @Override
            public double visit(int index, double value){
                return ThreadLocalRandom.current().nextDouble(bound);
            }


        });
        Log.d("FirstElement", Double.toString(aVector.getEntry(1)));
        return aVector;
    }






}
