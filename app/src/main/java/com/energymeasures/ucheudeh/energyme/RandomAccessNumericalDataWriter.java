package com.energymeasures.ucheudeh.energyme;

import android.content.Context;
import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by ucheudeh on 6/12/17.
 * The class extends the NumericalDataWriter, it is intended for RandomAccessFiles. It attaches a
 * trailer that holds the library of the pointers to the first byte of the Numerical data, and
 * the size of the required buffer to read a single numerical data
 */

public class RandomAccessNumericalDataWriter {


    FileChannel fc;
    ArrayList<Long> library;
    Context context;
    final int INT_SIZE = 4;
    final int DOUBLE_SIZE = 8;
    final int LONG_SIZE = 8;




        public RandomAccessNumericalDataWriter(Context context) throws FileNotFoundException {

            this.context = context;

            this.library = new ArrayList<Long>();
        }


        void write(SnapshotsBasket numData) throws IOException {
            File path = new File(context.getFilesDir(), "eneMRnd"+numData.getGroupType()+".dat");


            fc = new RandomAccessFile(path,"rw").getChannel();
            int numMatrix = numData.getNumMatrices();// in the test file this will be 4
            int numVector = numData.getNumVectors();

            for (Array2DRowRealMatrix matrix : numData.getMatrixElements()) {
                ByteBuffer matrixBuffer = constructMatrix(matrix);
                library.add(fc.position());
                library.add((long)matrixBuffer.capacity());// 4ease buffers are tricky bitches ;-)
                fc.write(matrixBuffer);

            }

            for (ArrayRealVector vector : numData.getVectorElements()) {
                ByteBuffer vectorBuffer = constructVector(vector);
                library.add(fc.position());
                library.add((long)vectorBuffer.capacity());// but once mastered. It's a barrel.
                fc.write(vectorBuffer);
            }

            long tail = fc.position();//Library will start here
            ByteBuffer libraryTailBuffer = ByteBuffer.allocate(library.size()*LONG_SIZE+LONG_SIZE);
            for(Long element: library){
                libraryTailBuffer.putLong(element.longValue());
            }
            libraryTailBuffer.putLong(tail);
            libraryTailBuffer.flip();
            fc.write(libraryTailBuffer);
            fc.force(false);

            fc.close();

            Log.i("File InfoWrite:FileSize", "eneMRnd"+"-"+Long.toString(path.length()));



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

