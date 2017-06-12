package com.energymeasures.ucheudeh.energyme;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by ucheudeh on 6/11/17.
 */

public abstract class Reader {

    String mode;
    double startTime, endTime;
    int dataSize;
    File path;
    ArrayList<Array2DRowRealMatrix> matriceTable;
    ArrayList<RealVector> vectorTable;

    public Reader(File path){

        this.path = path;
    }
    public enum ComposerMode{
        VECTOR,MATRIX
    }



    public ByteBuffer getBuffer(int size){
        /*
        Litrature suggest that there are benefits with using a direct buffer
        - one less copy process, OS always requires a direct buffer even if it is temporary to
            interact with the indirect buffer. Reason being that OS requires a contigous sequence of
            byte spaces in memory if it is going to interact directly with such a buffer. Given that
            the OS will directly access the user space memory, with out recurse to to use application
            about any form of indirection that may otherwise exist, for example, in an indirect
            buffer where there are not gurantees of a contigous allocation. (Java NiO pg 45)
         */

        return ByteBuffer.allocate(size);

    }

     protected void composerFactory(ByteBuffer dataBuff){


        ComposerMode mode = null;


        dataSize = dataBuff.remaining();
        while (dataBuff.hasRemaining()) {
            int numRows = dataBuff.getInt();
            if (numRows == 1) mode = ComposerMode.VECTOR;
            if (numRows > 1) mode = ComposerMode.MATRIX;


            switch (mode) {
                case VECTOR:

                    vectorTable.add(vectorComposer(dataBuff));
                    break;
                case MATRIX:
                    matriceTable.add(matrixComposer(dataBuff, numRows));
                    break;
                default:
                    vectorTable = null;
                    matriceTable = null;
                    break;
            }

        }

    }

    /*
    Efficiency can be improved by using bulk gets in the composer Row or Column-wise
     */

    public Array2DRowRealMatrix matrixComposer(ByteBuffer dataBuff, int numRows){
        /*
        Some kind of lock maybe  required on the ByteBuffer or the file channel. So no other
        method advances the position. Otherwise Absolute gets(index) will be used on the buffer.
         */



            int row = numRows;
            int columns = dataBuff.getInt();

            if (columns<=0){

                return  null;

            }


            double[][] backingMatrix = new double[row][columns];
            //row-wise composition
            for (int k = 0; k<row;k++){
                for(int z =0; z<columns; z++){
                    backingMatrix[k][z] = dataBuff.getDouble();
                }
                //
            }

        return (new Array2DRowRealMatrix(backingMatrix));
    }



    public ArrayRealVector vectorComposer (ByteBuffer dataBuff){


        int numElements = dataBuff.getInt();

        if (numElements<=0){
            return  null;
        }

        double [] backingVector = new double[numElements];

        for (int w=0; w<numElements;w++){
            backingVector[w] = dataBuff.getDouble();
        }

        return (new ArrayRealVector(backingVector));
    }



}
