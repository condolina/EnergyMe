package com.energymeasures.ucheudeh.energyme;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;

/**
 * Created by ucheudeh on 6/11/17. Parent reader class, Composition takes place here
 */

public abstract class Reader {

    String mode;
    File path;
    ArrayList<Array2DRowRealMatrix> matriceTable = new ArrayList<>();
    ArrayList<ArrayRealVector> vectorTable = new ArrayList<>();
    final int DOUBLE_SIZE = 8;




    public Reader(File path) throws IOException {

        this.path = path;

    }


    ByteBuffer getBuffer(int size){
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

     void composerFactory(ByteBuffer dataBuff){



         /*
         This methods recieves a Buffer may contain 1 to N records and composes all records in Buffer
         by looping thru each record header in the buffer. Every buffer starts with a header:
         number of rows (see diagram of the structure of the buffer). This method uses the header
         identify matrix or vector and calls the appropriate Composer
          */
        while (dataBuff.hasRemaining()) {

            int numRows = dataBuff.getInt();
            if (numRows == 1) vectorComposer(dataBuff);
            else if (numRows > 1) matrixComposer(dataBuff, numRows);
            else{break;}

        }


    }

    /*
    Efficiency can be improved by using bulk gets in the composer Row or Column-wise.
     */

    private void matrixComposer(ByteBuffer dataBuff, int numRows){
        /*
        Some kind of lock maybe  required on the ByteBuffer or the file channel. So no other
        method advances the position. Otherwise Absolute gets(index) will be used on the buffer.
         */

        //timeStamps.add(System.nanoTime());//Compose recode start will be many depending on quantity


        int columns = dataBuff.getInt();

            if (columns<=0){

                System.exit(1);

            }


            double[][] backingMatrix = new double[numRows][columns];
            //row-wise composition
            for (int k = 0; k< numRows; k++){
/*

                for(int z =0; z<columns; z++){
                    backingMatrix[k][z] = dataBuff.getDouble();
                }
*/

                // Row blocks to be blocked off for normal composer.
                ByteBuffer rowBlock = dataBuff.get(new byte[columns*DOUBLE_SIZE]);

                rowBlock.asDoubleBuffer().get(backingMatrix[k]);


            }
        //timeStamps.add(System.nanoTime());//backing array End
        //
        Array2DRowRealMatrix minx = new Array2DRowRealMatrix(backingMatrix);
        //timeStamps.add(System.nanoTime());//Object Construction _End Composer end
        this.matriceTable.add(minx);
        //Log.i("Minx", "Added");// just for testing remove afterwards

    }



    private void vectorComposer(ByteBuffer dataBuff){

        //timeStamps.add(System.nanoTime());//Compose recode start will be many depending on quantity


        int numElements = dataBuff.getInt();

        if (numElements<=0){
            System.exit(2);
        }


        double [] backingVector = new double[numElements];
        ByteBuffer rowBlock = dataBuff.get(new byte[numElements*DOUBLE_SIZE]);
        rowBlock.asDoubleBuffer().get(backingVector);

/*
        double [] backingVector = new double[numElements];
        for (int w=0; w<numElements;w++){
            backingVector[w] = dataBuff.getDouble();
        }
*/
        //timeStamps.add(System.nanoTime());//backing array End
        ArrayRealVector vinx = new ArrayRealVector(backingVector);

        //timeStamps.add(System.nanoTime());//Object Construction _End Composer end


        vectorTable.add(vinx);
    }




    Array2DRowRealMatrix getFirstMatrix(){
        return matriceTable.get(1);
    }

    ArrayRealVector getFirstVector(){
        return vectorTable.get(1);
    }


}
