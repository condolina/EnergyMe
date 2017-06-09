package com.energymeasures.ucheudeh.energyme;

import android.content.Context;
import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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


    }

    @Override
    String doWrite(SnapshotsBasket makeData) {
        // Make a file from the write, and get the absolute path of the the file

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
        File outfile = new File(context.getFilesDir(),"BasisBig.dat");
        try {
            ObjectOutputStream out1 = new ObjectOutputStream(new FileOutputStream(outfile));
            out1.writeObject(makeData.getAm(0));
            out1.writeObject(makeData.getAm(1));
            out1.writeObject(makeData.getAm(2));
            out1.writeObject(makeData.getAm(3));
            out1.close();
        } catch (FileNotFoundException e) {

            Log.e("EnergyMeIO", e.toString());
        } catch (IOException e) {

            Log.e("EnergyMeIO", e.toString());
        }
        //Log the info about each single file (path, name and size)
        Log.i("File Info", outfile.getAbsolutePath());
        Log.i("File Info", "BasisBig.dat"+"-"+Long.toString(outfile.length()));
        return outfile.getParent();






    }


    }
