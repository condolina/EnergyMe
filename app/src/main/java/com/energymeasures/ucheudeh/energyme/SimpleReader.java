package com.energymeasures.ucheudeh.energyme;

import android.content.Context;
import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by ucheudeh on 6/11/17. Java NIO is considered more efficient than Java IO. As such we
 * will have a bias towards Java NIO where ever possible. Channels and Buffers will be used
 * for all IOs
 */

class SimpleReader extends Reader {

    private FileChannel fc; // the Ovberloaded read method does not use this object, but makes multiplstrms

    SimpleReader(File path) throws IOException {
        super(path);
        this.mode = "Simple_Reader";
        try {
           this.fc = new FileInputStream(path).getChannel();


        } catch (FileNotFoundException e) {
            Log.e(this.mode," : "+ e.toString());
        }
    }

    public void read()throws IOException {


        /*
        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

        TODO Send start trigger to Power tool


         */

        //startTime = System.nanoTime();// It is instructive to place the start time here b4 readIn().


        //timeStamps.add(System.nanoTime());



        readIn();
        //readInChunks();



        //timeStamps.add(System.nanoTime());
        /*
        TODO: Send Stop Trigger to power tool

        norm the CVSWriter for now
        csvWriter2File();
         */

        //Log.i("EneM First element : ",Double.toString(this.getFirstMatrix().getEntry(1,1)));



    }



    void readInChunks() throws IOException{
        /*
        This method reads with a smaller fixed buffer size. 2048 Bytes is chosen due to the page
        size commonly used in many NAND flash brands. It provides a efficient read. The methods
        reads can read any combination of vectors or matrix in a single file.
         */

        int row = 0;
        int column = 0;
        int k = 0;
        final int DOUBLE = 8;
        final int HEADER = 8;


        byte [] bufBack = new byte[8192];


        ByteBuffer buf = ByteBuffer.wrap(bufBack);// shown by experiment to be the a good mark
        // beyond which performance flattens see David Nadeau'S EXPERIMENT
        //ByteBuffer buf = ByteBuffer.allocateDirect(8192); // direct buffer implementation

        int rCount,bCount;
        double [] vector = null;
        double [][] matrix = null;

        while((rCount= fc.read(buf) )!=-1) {
            if (rCount == 0) break;// Optimize this line, extra IO just to get file end!
            buf.flip();
            while (buf.hasRemaining()) {
                if(buf.remaining()<HEADER) {

                    break;
                }// we want to be able to get header at the least
            if (row == 0) {
                row = buf.getInt();
                column = buf.getInt(); // we now have a header for a record check for correctness
            }
            if (row == 1) {
                if (vector == null) vector = new double[column];
                // sufficient to test if this is a vector or a matix

                    int rem = column - k;
                    bCount = Math.min(buf.remaining(), rem * DOUBLE);
                    for(int s =0;(s*DOUBLE)<bCount;s++) {
                        vector[k] = buf.getDouble();
                        k++;
                    }
                    if (k==column) {
                        vectorTable.add(new ArrayRealVector(vector));
                        k=0;
                        row =0;
                        column = 0;
                        vector=null;
                    };
                }else if (row > 1) {

                    if (matrix == null) matrix = new double[row][column];
                    // sufficient to test if this is a vector or a matix

                    int rem = (row * column) - k;
                    bCount = Math.min(buf.remaining(), rem * DOUBLE);
                for(int s =0;(s*DOUBLE)<bCount;s++) {
                        matrix[k / column][k % column] = buf.getDouble();
                        k++;
                    }
                    if (k == (column * row)) {
                        matriceTable.add(new Array2DRowRealMatrix(matrix));
                        k = 0;
                        row = 0;
                        column = 0;
                        matrix = null;
                    }

                    } else {
                        Log.e("contruct error", "rowDimension<=0");
                        System.exit(-1);
                    }


                }
                buf.compact();

            }


        }







    void readIn() throws IOException {
        /*
        Get the size of the file and make a buffer to contain the entire File.
         */
        //timeStamps.add(System.nanoTime());//Buffer allocate header start/read data to Buffer_Start



        int dBuffSize = (int)fc.size();// only for testing will be passed directly on nextline
        ByteBuffer dataBuff = getBuffer(dBuffSize);
        ////timeStamps.add(System.nanoTime());// buffer allocate main_end/start fill buffer
        fc.read(dataBuff);
        ////timeStamps.add(System.nanoTime());// fill main buffer end
        fc.close();
        dataBuff.flip();
        //timeStamps.add(System.nanoTime());//Read Data to buffer_end


        composerFactory(dataBuff);


    }

    public ArrayList<Long> read(Context context, String basename)throws IOException {
        ArrayList<Long> durations = new ArrayList<>();


        /*
        This method takes a basefile name and reads individual files by calling the readIn method.
        Each time setting fc to the new filechannel.
        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

        TODO Send start trigger to Power tool


         */

        //.


        //timeStamps.add(System.nanoTime());
        for(int i = 0; i<4;i++){
            String filename = basename.concat("m").concat(Integer.toString(i)).concat(".dat");//Basisfilem1.dat
            fc = context.openFileInput(filename).getChannel();//here open() is executed
            Long startTime = System.nanoTime();//here b4 readIn(). Not measuring open().
            readIn();
            durations.add(System.nanoTime() - startTime);
        }

        for(int i = 0; i<4;i++){
            String filename = basename.concat("v").concat(Integer.toString(i)).concat(".dat");//Basisfilem1.dat
            fc = context.openFileInput(filename).getChannel();
            Long startTime = System.nanoTime();//here b4 readIn(). Not measuring open().
            readIn();
            durations.add(System.nanoTime() - startTime);
        }







        //timeStamps.add(System.nanoTime());
        /*
        TODO: Send Stop Trigger to power tool

        norm the CVSWriter for now
        csvWriter2File();
         */

        //Log.i("EneM First element : ",Double.toString(this.getFirstMatrix().getEntry(1,1)));
        // Convert Long arraylist to String Array



        return durations;
    }
}
