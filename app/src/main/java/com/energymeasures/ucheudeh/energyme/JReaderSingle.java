package com.energymeasures.ucheudeh.energyme;

import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by ucheudeh on 6/18/17.
 *
 * USAGE:
 */

public class JReaderSingle extends Reader {
    FileInputStream fc;
    public JReaderSingle(File path) throws IOException {
        super(path);
        this.mode = "Java_Reader_BigFile";
        try {
            this.fc = new FileInputStream(path);//Objectstream requires a stream and not a channel

        } catch (FileNotFoundException e) {
            Log.e(this.mode," : "+ e.toString());
        }
    }

    public void read()throws IOException, FileNotFoundException {


        /*
        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

        TODO Send start trigger to Power tool


         */

        startTime = System.nanoTime();// It is instructive to place the start time here b4 readIn().

        // endTime in main activity


        //timeStamps.add(System.nanoTime());



        readIn();


        //timeStamps.add(System.nanoTime());
        /*
        TODO: Send Stop Trigger to power tool

        norm the CVSWriter for now
        csvWriter2File();
         */

        //Log.i("JReadr First element : ",Double.toString(this.getFirstMatrix().getEntry(1,1)));



    }

    private void readIn() {

        int numM = 4; // number of matrix or vectora. Foreknowledge is required here
        int numV = 4; // these can be passed as parameters for each call OR seperatefiles


        // a little cheat here, we know the number of matrix and vectors in the file :-O

        try {
            ObjectInputStream in3 = new ObjectInputStream(fc);

            /*
            First read a sequence of matrices, then vectors
             */


            for (int i =0; i<numM;i++){// get the backing array and serialize this
                //matriceTable.add((Array2DRowRealMatrix) in3.readObject());//WholeObject version
                // Comparing Apples with Apples ;-)
                matriceTable.add(new Array2DRowRealMatrix((double[][])in3.readObject()));


                }
            for (int i =0; i<numV;i++)
            //vectorTable.add((ArrayRealVector) in3.readObject());
            vectorTable.add(new ArrayRealVector((double [])in3.readObject()));

            in3.close();

            //Log.i("JSer First element : ",Double.toString(matriceTable.get(0).getEntry(1,1)));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
