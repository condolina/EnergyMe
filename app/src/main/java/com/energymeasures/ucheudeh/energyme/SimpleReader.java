package com.energymeasures.ucheudeh.energyme;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by ucheudeh on 6/11/17. Java NIO is considered more efficient than Java IO. As such we
 * will have a bias towards Java NIO where ever possible. Channels and Buffers will be used
 * for all IOs
 */

class SimpleReader extends Reader {

    FileChannel fc; // the Ovberloaded read method does not use this object, but makes multiplstrms

    public SimpleReader(File path) throws IOException {
        super(path);
        this.mode = "Simple_Reader";
        try {
           this.fc = new FileInputStream(path).getChannel();


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


        //timeStamps.add(System.nanoTime());



        readIn();


        //timeStamps.add(System.nanoTime());
        /*
        TODO: Send Stop Trigger to power tool

        norm the CVSWriter for now
        csvWriter2File();
         */

        //Log.i("EneM First element : ",Double.toString(this.getFirstMatrix().getEntry(1,1)));



    }

    void readIn() throws IOException, FileNotFoundException {
        /*
        Get the size of the file and make a buffer to contain the entire File.
         */
        //timeStamps.add(System.nanoTime());//Buffer allocate header start/read data to Buffer_Start

        // TODO Consider locking the file here and releasing just before return

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

    public void read(Context context, String basename)throws IOException, FileNotFoundException {


        /*
        This method takes a basefile name and reads individual files by calling the readIn method.
        Each time setting fc to the new filechannel.
        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

        TODO Send start trigger to Power tool


         */

        startTime = System.nanoTime();// It is instructive to place the start time here b4 readIn().


        //timeStamps.add(System.nanoTime());
        for(int i = 0; i<4;i++){
            String filename = basename.concat("m").concat(Integer.toString(i)).concat(".dat");//Basisfilem1.dat
            fc = context.openFileInput(filename).getChannel();
            readIn();
        }

        for(int i = 0; i<4;i++){
            String filename = basename.concat("v").concat(Integer.toString(i)).concat(".dat");//Basisfilem1.dat
            fc = context.openFileInput(filename).getChannel();
            readIn();
        }







        //timeStamps.add(System.nanoTime());
        /*
        TODO: Send Stop Trigger to power tool

        norm the CVSWriter for now
        csvWriter2File();
         */

        //Log.i("EneM First element : ",Double.toString(this.getFirstMatrix().getEntry(1,1)));



    }
}
