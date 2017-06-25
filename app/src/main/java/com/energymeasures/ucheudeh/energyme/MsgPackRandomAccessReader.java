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
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by ucheudeh on 6/9/17.
 * My attempt to make a RandomAccessReader from MsgPack. Do to msgP being  sequential serilizer
 * like java, protobuf, Random Access in does not apply as the entire file has to be unpacked first
 * before it can be interactd with.  internal compression of primitives does not help with and form
 * of raw decoding on a manual level. Also does not support MMAP for the same reason, like JAVA
 */

public class MsgPackRandomAccessReader extends Reader {
    Context context ;
    ArrayList<Array2DRowRealMatrix> mat2D = new ArrayList<Array2DRowRealMatrix>();
    ArrayList <ArrayRealVector> snapshots = new ArrayList <ArrayRealVector>();
    ArrayList<Long> library;
    FileChannel fc;
    MessageUnpacker unMsgPk;

    public MsgPackRandomAccessReader(File path, File libFile) throws IOException {
        super(path);



        this.fc = new RandomAccessFile(path, "r").getChannel();//



        /* a single file solution implementation would have been nice, but @msgPack implements
        a narrowing algorithim that seeks to use a few bytes as possible to represent a number.
        As a result the structure of numerical Data is not solid. Relying on a fixed tail lenght as
        used in eneM for libsize will be untenable. Hence the use of a separate .lib file to
        hold the library.
        */


            if (library == null) {
                library = new ArrayList<Long>();
                FileChannel fc2 = new FileInputStream(libFile).getChannel();
                ByteBuffer libBuffer = ByteBuffer.allocate((int) fc2.size());
                fc2.read(libBuffer);



                unMsgPk = MessagePack.newDefaultUnpacker(libBuffer.array());

                //create Library.

                while (unMsgPk.hasNext()) {
                    library.add((long) unMsgPk.unpackLong());
                }
                Log.i("msgPRNDReader", " Library is Ready to take your bringList");
                fc2.close();//GC'd
                //reset unMsgPk in read with new byte array stream for each element read

            }
        }

    public void read(int[] bringList)throws IOException, FileNotFoundException {


        /*
        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

        TODO Send start trigger to Power tool


         */

        startTime = System.nanoTime();// It is instructive to place the start time here b4 readIn().

        // endTime in main activity


        //timeStamps.add(System.nanoTime());



        readIn(bringList);


        //timeStamps.add(System.nanoTime());
        /*
        TODO: Send Stop Trigger to power tool

        norm the CVSWriter for now
        csvWriter2File();
         */

        Log.i("JReadr First element : ",Double.toString(this.getFirstMatrix().getEntry(1,1)));



    }




    void readIn(int[] bringList) throws IOException {
        /*
        Implementing only Early fetch. The raw file is held in virtual memory an actual request for
        a specific record, results in its reconstruction and delivery
         */
        ByteBuffer fileBuffer = ByteBuffer.allocate((int)fc.size());
        fc.read(fileBuffer);




            int numMatrix = unMsgPk.unpackInt();
            int numVector = unMsgPk.unpackInt();


            for (int l = 0; l < numMatrix; l++) {
                int row = unMsgPk.unpackInt();
                int column = unMsgPk.unpackInt();
                double[][] root = new double[row][column];
                for (int t = 0; t < row; t++) {
                    for (int q = 0; q < column; q++) {
                        root[t][q] = unMsgPk.unpackDouble();
                    }
                }
                mat2D.add(new Array2DRowRealMatrix(root));
            }

            for (int w = 0; w < numVector; w++) {
                int size = unMsgPk.unpackInt();
                double[] rootVector = new double[size];
                for (int z = 0; z < size; z++) {
                    rootVector[z] = unMsgPk.unpackDouble();
                }
                snapshots.add(new ArrayRealVector(rootVector));

            }


            // get headers


        }


    }





