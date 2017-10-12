package com.energymeasures.ucheudeh.energyme;

import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.ArrayList;

/**
 * Created by ucheudeh on 6/11/17. Parent reader class, Composition takes place here
 *
 */

public abstract class Reader {

    String mode; // e.g mmap,simple
    File path;
    ArrayList<Array2DRowRealMatrix> matriceTable = new ArrayList<>();
    ArrayList<ArrayRealVector> vectorTable = new ArrayList<>();
    final int DOUBLE_SIZE = 8;
    ArrayList<Flatmatrix> matriceTableFLAT = new ArrayList<>();
    ArrayList<FlatArrayRealVector> vectorTableFLAT = new ArrayList<>();
    byte [] buffArray = null;


    public Reader(File path) throws IOException {

        this.path = path;

    }


    ByteBuffer getBuffer(int size){
        /*
        Litrature suggest that there are benefits with using a direct buffer
        - one less copy process per buffer trip, OS always requires a direct buffer even if it is temporary to
            interact with the indirect buffer. Reason being that OS prefers a contiguous sequence of
            pages in memory if it is going to interact directly with such a buffer. Given that
            the OS will directly access the user space memory, with out recurse to to user application
            about any form of indirection or meaning that may otherwise exist. For example, in an indirect
            buffer where there are not gurantees of a contigous allocation, or a wrapped ByteBuffer
             for which meaning exisit only within Java and is not really an allocation but just a
             reservation of heap (ref: Java NiO pg 45).
            NOTE: If any form of indirect buffer is used, wrapped or allocated, Java copies creates a
         */
        buffArray = new byte[size]; // will be used as universal backing array for this Reader
        return ByteBuffer.wrap(buffArray); //indirect buffer


        //return ByteBuffer.allocate(size); // indirect buffer
        //return ByteBuffer.allocateDirect(size); //direct buffer

    }

     void composerFactory(ByteBuffer dataBuff){



         /*
         This methods receives a Buffer, which  may contain 1 to N data units/records and composes all data units in Buffer
         by looping thru each record header in the buffer. Every buffer starts with a header: rank,rows, columns
         rank maybe skipped if only one or two dimensions are used in application as was this case. This method uses the header
         identify matrix or vector and calls the appropriate Composer
          */
        while (dataBuff.hasRemaining()) {
            // method calls will be uncommented accordingly to test different factors
            int numRows = dataBuff.getInt();

            //FLAT structure
            if (numRows == 1)matrixVectorComposerFLAT(dataBuff, numRows);
            //if (numRows == 1)vectorComposer(dataBuff);

             //FLAT Structure
            else if (numRows > 1) matrixVectorComposerFLAT(dataBuff, numRows);
            //else if (numRows > 1) matrixComposer(dataBuff, numRows);
            else{
                Log.e("Format Error", "Row =< 0!");
            System.exit(-1);}

        }


    }



    private void matrixVectorComposerFLAT(ByteBuffer dataBuff, int numRows) {
        /*
        This method will attempt to reduce latency by delaying the structural definition
        of the numerical data, i.e. no POST-PROCESSING

         */
        final int HEADER = 8;
        final int DOUBLE = 8;
        int columns = dataBuff.getInt();

        if (columns <= 0) {

            System.exit(1);

        }
        int recordSize = numRows * columns * DOUBLE_SIZE + HEADER;
        if (dataBuff.capacity() == recordSize) {
            if(numRows==1){
                this.vectorTableFLAT.add(new FlatArrayRealVector(buffArray,columns));
                dataBuff.position(recordSize);
            }else {
               this.matriceTableFLAT.add(new Flatmatrix(ByteBuffer.wrap(buffArray), numRows, columns));
                dataBuff.position(recordSize);
            }
        } else {
            byte[] buffByte = new byte[recordSize];
            ByteBuffer matrixBuffer = ByteBuffer.wrap(buffByte).putInt(numRows).putInt(columns);

            dataBuff.get(buffByte, HEADER, (numRows * columns * DOUBLE));
            if(numRows==1){
                //this.vectorTableFLAT.add(new FlatArrayRealVector(buffByte,columns));
            }else {
                this.matriceTableFLAT.add(new Flatmatrix(ByteBuffer.wrap(buffByte), numRows, columns));
            }
        }
    }

    /*
    Efficiency can be improved by using bulk gets in the composer Row or Column-wise.
     */

    private void matrixComposer(ByteBuffer dataBuff, int numRows){


        int columns = dataBuff.getInt();

            if (columns<=0){

                System.exit(1);

            }


            double[][] backingMatrix = new double[numRows][columns];
           //rowDimension-wise composition
            for (int k = 0; k< numRows; k++){


                for(int z =0; z<columns; z++){
                    backingMatrix[k][z] = dataBuff.getDouble();
                }

/*
                // Row blocks to be blocked off for normal composer.
                byte [] rowbyte = new byte[columns*DOUBLE_SIZE];
                dataBuff.get(rowbyte);

                DoubleBuffer backingBuffer = ByteBuffer.wrap(rowbyte).asDoubleBuffer();

                if(backingBuffer.hasArray()){
                    backingMatrix[k]=backingBuffer.array();
                }else{

                    backingBuffer.get(backingMatrix[k]);
                }
*/

            }
        //timeStamps.add(System.nanoTime());//backing array End
        //
        Array2DRowRealMatrix minx = new Array2DRowRealMatrix(backingMatrix);
        //timeStamps.add(System.nanoTime());//Object Construction _End Composer end
        this.matriceTable.add(minx);
        //Log.i("Minx", "Added");// just for testing remove afterwards
        backingMatrix=null; // garbage on during test

    }



    private void vectorComposer(ByteBuffer dataBuff){

        //timeStamps.add(System.nanoTime());//Compose recode start will be many depending on quantity


        int numElements = dataBuff.getInt();

        if (numElements<=0){
            System.exit(2);
        }

/*
// Row-block dumping: processes row-by-row
        double [] backingVector = new double[numElements];
        byte [] rowbyte = new byte[numElements*DOUBLE_SIZE];
        dataBuff.get(rowbyte);

        DoubleBuffer backingBuffer= ByteBuffer.wrap(rowbyte).asDoubleBuffer();
        if(backingBuffer.hasArray()){
            backingVector=backingBuffer.array();
        }else{

            backingBuffer.get(backingVector);
        }

*/ // Element by element dumping (same as ReadIn Chunks)
        double [] backingVector = new double[numElements];
        for (int w=0; w<numElements;w++){
            backingVector[w] = dataBuff.getDouble();
        }

        //timeStamps.add(System.nanoTime());//backing array End
       // ArrayRealVector vinx = new ArrayRealVector(backingVector,false);

        //timeStamps.add(System.nanoTime());//Object Construction _End Composer end


        //vectorTable.add(vinx);
        backingVector =null; // just for testing to save memory and fairness
    }




    Array2DRowRealMatrix getFirstMatrix(){
        return matriceTable.get(0);
    }
    Flatmatrix getFirstMYMatrix(){return matriceTableFLAT.get(0);}

    ArrayRealVector getFirstVector(){
        return vectorTable.get(0);
    }


}
