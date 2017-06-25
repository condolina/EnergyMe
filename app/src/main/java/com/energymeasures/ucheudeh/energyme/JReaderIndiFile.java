package com.energymeasures.ucheudeh.energyme;

import android.content.Context;
import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by ucheudeh on 6/18/17.
 *
 * This Object will read 4 file each for matrice and vectors with each object presaved to one file
 */

public class JReaderIndiFile extends Reader {
    public JReaderIndiFile(File path) throws IOException {
        super(path);// semantics for path should be a list of Files for each record. Will be fixed.
    }

    public void read(Context context, String basename)throws IOException, FileNotFoundException {


        /*
        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

        TODO Send start trigger to Power tool


         */

        startTime = System.nanoTime();// It is instructive to place the start time here b4 readIn().

        // endTime in main activity


        //timeStamps.add(System.nanoTime());



        readIn(context, basename);


        //timeStamps.add(System.nanoTime());
        /*
        TODO: Send Stop Trigger to power tool

        norm the CVSWriter for now
        csvWriter2File();
         */

        Log.i("JReadr First element : ",Double.toString(this.getFirstMatrix().getEntry(1,1)));



    }

    private void readIn(Context context, String basename) {
        // read the multiple file version of the snapshots: numData. Each record is saved in a Single file

        //USAGE for basename: basename[m or v][x], e.g Basisfilem1.dat, Basisfilev1.dat.

        /*
        presently this method will fail for Border, and Extreme, requires 4 vectors and 4 matrix
        this is just a test. It is possible to separate matrix and vectors in separate files,
        and read entire file casting the object in a loop.
         */


        for(int i=0 ; i<4; i++){
            String filename = basename.concat("m").concat(Integer.toString(i)).concat(".dat");//Basisfilem1.dat

            //Log the info about each single file (path, name and size)
            //Log.i("File InfoRead", filename);
            //Log.i("File InfoRead", "BasisSingle"+Integer.toString(i).concat("-").concat(Long.toString(multiFile.length())));
            try {
                ObjectInputStream in2 = new ObjectInputStream(context.openFileInput(filename));
                Array2DRowRealMatrix blk = new Array2DRowRealMatrix((double [][]) in2.readObject());
                matriceTable.add(blk);
                //Log the first element of each array
                Log.i("First Elements"+Integer.toString(i), Double.toString(blk.getEntry(1,1)));
                in2.close();
            } catch (FileNotFoundException e) {

                Log.e("EnergyMeIO", e.toString());
            } catch (IOException e) {

                Log.e("EnergyMeIO", e.toString());
            } catch (ClassNotFoundException e) {
                Log.e("EnergyMeIO", e.toString());
            }

        }
        // read Vectors

        for(int i=0 ; i<4; i++){
            String filename = basename.concat("v").concat(Integer.toString(i)).concat(".dat");//Basisfilem1.dat

            //Log the info about each single file (path, name and size)
            //Log.i("File InfoRead", filename);
            //Log.i("File InfoRead", "BasisSingle"+Integer.toString(i).concat("-").concat(Long.toString(multiFile.length())));
            try {
                ObjectInputStream in2 = new ObjectInputStream(context.openFileInput(filename));
                ArrayRealVector vec = new ArrayRealVector((double[]) in2.readObject());
                vectorTable.add(vec);
                //Log the first element of each array
                Log.i("First Elements"+Integer.toString(i), Double.toString(vec.getEntry(1)));
                in2.close();
            } catch (FileNotFoundException e) {

                Log.e("EnergyMeIO", e.toString());
            } catch (IOException e) {

                Log.e("EnergyMeIO", e.toString());
            } catch (ClassNotFoundException e) {
                Log.e("EnergyMeIO", e.toString());
            }

        }
    }

    }
