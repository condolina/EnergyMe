package com.energymeasures.ucheudeh.energyme;

import android.content.Context;
import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by ucheudeh on 5/27/17.
 */

public class JSerializer extends SerializeMe {

    Context context ;

    public JSerializer(Context context) {
        super();
        this.context = context;
    }

    @Override
    void doRead() {
/*

// read the multiple file version of the snapshots: numData. Each record is saved in a Single file

        String multi = "BasisSingle";


        for(int i=0 ; i<4; i++){
            String filename = multi.concat(Integer.toString(i)).concat(".dat");

            //Log the info about each single file (path, name and size)
            //Log.i("File InfoRead", filename);
            //Log.i("File InfoRead", "BasisSingle"+Integer.toString(i).concat("-").concat(Long.toString(multiFile.length())));
            try {
                ObjectInputStream in2 = new ObjectInputStream(context.openFileInput(filename));
                Array2DRowRealMatrix blk = (Array2DRowRealMatrix) in2.readObject();
                //Log the first element of each array
                Log.i("First Elements"+Integer.toString(i), Double.toString(blk.getEntry(1,1)));
                in2.close();
            } catch (FileNotFoundException e) {

                Log.e("EnergyMeIO", "check this");
            } catch (IOException e) {

                Log.e("EnergyMeIO", "check this");
            } catch (ClassNotFoundException e) {
                Log.e("EnergyMeIO", "check this");
            }

        }
        */

// read a single BIG file containing all the records and re construct the records. Throw in List

        String bigSingle = "BasisBig.dat";
        // a little cheat here, we know the number of matrix and vectors in the file :-O

        try {
            ObjectInputStream in3 = new ObjectInputStream(context.openFileInput(bigSingle));
            ArrayList<Array2DRowRealMatrix> matList = new ArrayList<Array2DRowRealMatrix>();
            ArrayList<ArrayRealVector> vecList = new ArrayList<ArrayRealVector>();

            for (int i =0; i<4;i++)matList.add((Array2DRowRealMatrix) in3.readObject());
            for (int i =0; i<4;i++)vecList.add((ArrayRealVector) in3.readObject());

            in3.close();

            Log.i("JSer First element : ",Double.toString(matList.get(0).getEntry(1,1)));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }




    }

    @Override
    String doWrite(SnapshotsBasket makeData) {
        // Make a file from the write, and get the absolute path of the the file
// Single file writes
        /*
        String multi = "BasisSingle";
        Log.i("JserriOrgMakedata",Double.toString(makeData.getAm(0).getEntry(1,1)));

        for(int i=0 ; i<4; i++){
            String filename = multi.concat(Integer.toString(i)).concat(".dat");
            File multiFile = new File(context.getFilesDir(),filename);


            try {

                ObjectOutputStream out2 = new ObjectOutputStream(new FileOutputStream(multiFile));
                out2.writeObject(makeData.getAm(i));
                out2.close();
            } catch (FileNotFoundException e) {

                Log.e("EnergyMeIOfilenotF", e.toString());
            } catch (IOException e) {

                Log.e("EnergyMeIOexcp", e.toString());
            }
            //Log the info about each single file (path, name and size)
            Log.i("File InfoWrite", multiFile.getAbsolutePath());
            Log.i("File InfoWrite", "BasisSingle"+Integer.toString(i).concat("-").concat(Long.toString(multiFile.length())));

        }
        */
        File outfile = new File(context.getFilesDir(),"BasisBig.dat");
        try {
            ObjectOutputStream out1 = new ObjectOutputStream(new FileOutputStream(outfile));
            //matrix write
            out1.writeObject(makeData.getAm(0));
            out1.writeObject(makeData.getAm(1));
            out1.writeObject(makeData.getAm(2));
            out1.writeObject(makeData.getAm(3));

            // vector write

            out1.writeObject(makeData.getSnapshot(0));
            out1.writeObject(makeData.getSnapshot(1));
            out1.writeObject(makeData.getSnapshot(2));
            out1.writeObject(makeData.getSnapshot(3));

            Log.i("JavWriteSize - ", Long.toString(outfile.length()));
            out1.close();
        } catch (FileNotFoundException e) {

            Log.e("EnergyMeIO", e.toString());
        } catch (IOException e) {

            Log.e("EnergyMeIO", e.toString());
        }
        //Log the info about each single file (path, name and size)
        Log.i("File Info", outfile.getAbsolutePath());

        return outfile.getParent();






    }


    }
