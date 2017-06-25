package com.energymeasures.ucheudeh.energyme;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by ucheudeh on 5/27/17.
 */

public class JSerializer  {

    Context context;

    public JSerializer(Context context) {
        super();
        this.context = context;
    }


        public void doWrite(SnapshotsBasket makeData){
            // Make a file from the write, and get the absolute path of the the file
            // INDIVIDUAL  file writes

            String basename = "jSerIndi"+makeData.getGroupType();
            Log.i("jSerIndi", Double.toString(makeData.getMatrix(0).getEntry(1, 1)));

            for (int i = 0; i < makeData.getNumMatrices(); i++) {
                String filename = basename.concat("m").concat(Integer.toString(i)).concat(".dat");//Basisfilem1.dat;
                File multiFile = new File(context.getFilesDir(), filename);


                try {

                    ObjectOutputStream out2 = new ObjectOutputStream(new FileOutputStream(multiFile));
                    out2.writeObject(makeData.getMatrix(i).getData());// write the backing array
                    out2.close();
                } catch (FileNotFoundException e) {

                    Log.e("EnergyMeIOfilenotF", e.toString());
                } catch (IOException e) {

                    Log.e("EnergyMeIOexcp", e.toString());
                }
                //Log the info about each single file (path, name and size)
                Log.i("File InfoWrite", multiFile.getAbsolutePath());
                Log.i("File InfoWrite", "File_Size" + Integer.toString(i).concat("-").concat(Long.toString(multiFile.length())));

            }

            for (int i = 0; i < makeData.getNumVectors(); i++) {
                String filename = basename.concat("v").concat(Integer.toString(i)).concat(".dat");//Basisfilem1.dat

                File multiFile = new File(context.getFilesDir(), filename);


                try {

                    ObjectOutputStream out2 = new ObjectOutputStream(new FileOutputStream(multiFile));
                    out2.writeObject(makeData.getVector(i).toArray());
                    out2.close();
                    Log.i("File InfoWrite", "File_Size" + Integer.toString(i).concat("-").concat(Long.toString(multiFile.length())));
                } catch (FileNotFoundException e) {

                    Log.e("EnergyMeIOfilenotF", e.toString());
                } catch (IOException e) {

                    Log.e("EnergyMeIOexcp", e.toString());
                }
                //Log the info about each single file (path, name and size)
                Log.i("File InfoWrite", multiFile.getAbsolutePath());


            }

            //BigFile for everthing

            File outfile = new File(context.getFilesDir(), "jSerBig"+makeData.getGroupType()+".dat");
            try {
                ObjectOutputStream out1 = new ObjectOutputStream(new FileOutputStream(outfile));
                //matrix write
                for(int i = 0;i<makeData.getNumMatrices();i++) {
                    out1.writeObject(makeData.getMatrix(i).getData());
                }


                // vector write
                for (int i = 0; i<makeData.getNumVectors();i++) {

                    out1.writeObject(makeData.getVector(i).toArray());
                }


                Log.i("File InfoWrite:FileSize", Long.toString(outfile.length()));
                out1.close();
            } catch (FileNotFoundException e) {

                Log.e("EnergyMeIO", e.toString());
            } catch (IOException e) {

                Log.e("EnergyMeIO", e.toString());
            }
            //Log the info about each single file (path, name and size)
           // Log.i("jSer_File Info", outfile.getAbsolutePath());




        }


    }

