package com.energymeasures.ucheudeh.energyme;


import android.content.Context;
import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.System.nanoTime;

/**
 * Created by ucheudeh on 6/18/17.
 * the super constructor does nothing at currently.
 * The read meathod call takes the context and a basefile name. e.g msgPIndiFiles.
 * this version will read 4 matrices then 4 vectors into an array list.
 */

class MsgPReaderIndiFile extends Reader {
    MsgPReaderIndiFile(File path) throws IOException {
        super(path);
    }

    public ArrayList<Long> read(Context context, String basename) throws IOException {


        /*
        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

        TODO Send start trigger to Power tool


         */


        ArrayList<Long> duration = readIn(context, basename);



        /*
        TODO: Send Stop Trigger to power tool


         */

        //Log.i("msgPInd FirstElement : ", Double.toString(this.getFirstMatrix().getEntry(1, 1)));
        return duration;


    }

    private ArrayList<Long> readIn(Context context, String basename) throws IOException {
        ArrayList<Long> durations = new ArrayList<>();
        // read the multiple file version of the snapshots: numData. Each record is saved in a Single file

        //USAGE for basename: basename[m or v][x], e.g msgPBasisfilem1.dat, msgPBasisfilev1.dat.

        //read matrices 4 records

        // full Object read

        MessageUnpacker unMsgPk;

        int numMatrix = 4;
        int numVector = 4;

        for (int l = 0; l < numMatrix; l++) {

            String filename = basename.concat("m").concat(Integer.toString(l)).concat(".dat");//Basisfilem1.dat


            try {
                FileInputStream in = context.openFileInput(filename);
                // a more reliable io method will be better e.g Java.nio
                Long startTime = nanoTime();
                int fileSize = in.available();
                byte[] inBuff = new byte[fileSize]; // buffer size change here
                int fileRead = in.read(inBuff, 0, fileSize);//in fairness read entire file into buffer in memory
                unMsgPk = MessagePack.newDefaultUnpacker(inBuff); // unpacker has own buffer 8k

                in.close();


                int row = unMsgPk.unpackInt();
                int column = unMsgPk.unpackInt();
                double[][] root = new double[row][column];
                for (int t = 0; t < row; t++) {
                    for (int q = 0; q < column; q++) {
                        root[t][q] = unMsgPk.unpackDouble();
                    }
                }
                matriceTable.add(new Array2DRowRealMatrix(root));
                unMsgPk.close();
                durations.add(Long.valueOf(nanoTime()-startTime));

            } catch (IOException e) {
                // TODO: optimisation make a tag string object that is passed to all log entry for message pack
                Log.e("MsgPack", e.toString());
            }
            //Log.i("MsgPInd FirstElement : ", Double.toString(matriceTable.get(k).getEntry(1, 1)));
        }
        for (int k = 0; k < numVector; k++) {
            String filename = basename.concat("v").concat(Integer.toString(k)).concat(".dat");//Basisfilem1.dat

            try {
                FileInputStream in = context.openFileInput(filename);
                // a more reliable io method will be better e.g Java.nio
                Long startTime = nanoTime();
                int fileSize = in.available();
                byte[] inBuff2 = new byte[fileSize];
                int fileRead = in.read(inBuff2, 0, fileSize);
                unMsgPk = MessagePack.newDefaultUnpacker(inBuff2);

                in.close();

                int size = unMsgPk.unpackInt();
                double[] rootVector = new double[size];
                    for (int z = 0; z < size; z++) {
                        rootVector[z] = unMsgPk.unpackDouble();
                    }
                    vectorTable.add(new ArrayRealVector(rootVector));
                unMsgPk.close();
                durations.add(nanoTime() - startTime);


            } catch (IOException e) {
                // TODO: optimisation make a tag string object that is passed to all log entry for message pack
                Log.e("MsgPInd", e.toString());
            }

            //Log.i("MsgPInd FirstElement : ", Double.toString(vectorTable.get(k).getEntry(1, 1)));


            // get headers


        }

        return durations;
    }

}