package com.energymeasures.ucheudeh.energyme;

import android.content.Context;
import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import static java.lang.System.nanoTime;

/**
 * Created by ucheudeh on 6/18/17.
 *
 * This Object will read 4 file each for matrice and vectors with each object presaved to one file
 */

class JReaderIndiFile extends Reader {
    JReaderIndiFile(File path) throws IOException {
        super(path);// semantics for path should be a list of Files for each record. Will be fixed.
    }

    public ArrayList<Long> read(Context context, String basename)throws IOException {


        /*
        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

        TODO Send start trigger to Power tool


         */





        ArrayList<Long> duration = readIn(context, basename);// Structure to allow finer power measure



        /*
        TODO: Send Stop Trigger to power tool


         */

        //Log.i("JReadr First element : ",Double.toString(this.getFirstMatrix().getEntry(1,1)));


        return duration;
    }

    private ArrayList<Long> readIn(Context context, String basename) {
        ArrayList<Long> durations = new ArrayList<>();
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
                Long startTime = nanoTime();//here b4 readIn(). Not measuring open().
                Array2DRowRealMatrix blk = new Array2DRowRealMatrix((double [][]) in2.readObject());
                matriceTable.add(blk);
                //Log the first element of each array
                //Log.i("First Elements"+Integer.toString(i), Double.toString(blk.getEntry(1,1)));
                in2.close();
                durations.add(nanoTime() - startTime);
            } catch (ClassNotFoundException | IOException e) {

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
                Long startTime = nanoTime();//here b4 readIn(). Not measuring open().
                ArrayRealVector vec = new ArrayRealVector((double[]) in2.readObject());
                vectorTable.add(vec);
                //Log the first element of each array
                //Log.i("First Elements"+Integer.toString(i), Double.toString(vec.getEntry(1)));
                in2.close();
                durations.add(nanoTime() - startTime);
            } catch (ClassNotFoundException | IOException e) {

                Log.e("EnergyMeIO", e.toString());
            }

        }
        return durations;
    }

    }
