package com.energymeasures.ucheudeh.energyme;

import android.content.Context;
import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by ucheudeh on 6/12/17.
 *
 * USAGE: This class constructs the header and trailer for the Numerical Data Structure . It
 * is constructed with a File Object passed. Hence it is permanently dedicated to this file.
 *
 * The constructor takes a File Object e.g File file = new File(context.getDir(),mode
 *
 * Write method take a SnapShotbasket (a container for the matrices and vectors we want to sent), and
 * marshals them to a the file including the appropriate headers for a reconstruction by the reader.
 */

public class NumericalDataWriter {
    FileChannel fc;
    final int INT_SIZE = 4;
    final int DOUBLE_SIZE = 8;
    final int LONG_SIZE = 8;
    final String mode = "Regular_Mapped";
    Context context;




    public NumericalDataWriter(Context context) throws FileNotFoundException {
        this.context = context;

    }

    void write(SnapshotsBasket numData) throws IOException {

        File path = new File(context.getFilesDir(),"regMapped1");
        fc = new FileOutputStream(path).getChannel();

        int numMatrix = numData.getAmSize();
        int numVector = numData.getsnapshotSize();

        for (Array2DRowRealMatrix matrix : numData.getAm()) {
            ByteBuffer matrixBuffer = constructMatrix(matrix);
            fc.write(matrixBuffer);
        }

        for (ArrayRealVector vector : numData.getSnapshot()) {
            ByteBuffer vectorBuffer = constructVector(vector);
            fc.write(vectorBuffer);
        }
        Log.i("NumericalWriterSize : ", Long.toString(fc.size()));
        fc.close();


    }

    ByteBuffer constructMatrix(Array2DRowRealMatrix matrix) {
        int rowDim = matrix.getRowDimension();
        int columnDim = matrix.getColumnDimension();
        ByteBuffer matrixBuffer = ByteBuffer.allocate(rowDim*columnDim*DOUBLE_SIZE+(2*INT_SIZE));
        matrixBuffer.putInt(rowDim).putInt(columnDim);//metadata for this matrix
        double[][] backingMatrix = matrix.getData();

        for(int i =0; i<rowDim;i++){
            for (int k = 0; k<columnDim;k++){
                matrixBuffer.putDouble(backingMatrix[i][k]);
            }
        }
        matrixBuffer.flip();
        return matrixBuffer;

    }

    ByteBuffer constructVector(ArrayRealVector vector) {
        int rowDim = 1;
        int columnDim = vector.getDimension();
        ByteBuffer vectorBuffer = ByteBuffer.allocate(columnDim*DOUBLE_SIZE+(2*INT_SIZE));
        vectorBuffer.putInt(rowDim).putInt(columnDim);//metadata for this matrix
        double[] backingArray = vector.toArray();


            for (int k = 0; k<columnDim;k++){
                vectorBuffer.putDouble(backingArray[k]);
            }
        vectorBuffer.flip();

        return vectorBuffer;

    }

}