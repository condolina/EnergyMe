package com.energymeasures.ucheudeh.energyme;

import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by ucheudeh on 6/18/17.
 */

public class MsgPReader extends Reader {
    public MsgPReader(File path) throws IOException {
        super(path);
    }


    public void read()throws IOException, FileNotFoundException {


        /*
        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

        TODO Send start trigger to Power tool


         */

        startTime = System.nanoTime();// It is instructive to place the start time here b4 readIn().


        //timeStamps.add(System.nanoTime());



        readIn();


        //timeStamps.add(System.nanoTime());
        /*
        TODO: Send Stop Trigger to power tool

        norm the CVSWriter for now
        csvWriter2File();
         */

        //Log.i("MsgP First element : ",Double.toString(this.getFirstMatrix().getEntry(1,1)));



    }

    void readIn() {
        // full Object read

        MessageUnpacker unMsgPk;


        try {
            FileInputStream in = new FileInputStream(path);
            // a more reliable io method will be better e.g Java.nio
            int fileSize = in.available();
            byte [] inBuff = new byte[fileSize];
            in.read(inBuff,0,fileSize);
            unMsgPk= MessagePack.newDefaultUnpacker(inBuff);

            in.close();
            int numMatrix = unMsgPk.unpackInt();
            int numVector = unMsgPk.unpackInt();


            for (int l=0;l<numMatrix;l++){
                int row = unMsgPk.unpackInt();
                int column = unMsgPk.unpackInt();
                double[][] root = new double [row][column];
                for (int t= 0; t<row;t++){
                    for (int q = 0; q<column;q++){
                        root[t][q]= unMsgPk.unpackDouble();
                    }
                }
                matriceTable.add(new Array2DRowRealMatrix(root));
            }

            for (int w = 0; w<numVector; w++){
                int size = unMsgPk.unpackInt();
                double [] rootVector = new double[size];
                for ( int z = 0; z<size;z++){
                    rootVector[z]=unMsgPk.unpackDouble();
                }
                vectorTable.add(new ArrayRealVector(rootVector));

            }

            //Log.i("MsgPRE First element : ",Double.toString(matriceTable.get(0).getEntry(1,1)));


        } catch (IOException e) {
            // TODO: optimisation make a tag string object that is passed to all log entry for message pack
            Log.e("MsgPack",e.toString());
        }
        // get headers




    }
}
