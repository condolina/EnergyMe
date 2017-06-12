package com.energymeasures.ucheudeh.energyme;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by ucheudeh on 6/11/17. Java NIO is considered more efficient than Java IO. As such we
 * will have a bias towards Java NIO where ever possible. As such Channels and Buffers will be used
 * for all IOs
 */

class SimpleReader extends Reader {

    FileChannel fc;

    public SimpleReader(File path){
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


        startTime = System.nanoTime();



        readIn();


        endTime = System.nanoTime();
        /*
        TODO: Send Stop Trigger to power tool

         */


    }

    void readIn() throws IOException, FileNotFoundException {
        /*
        Header of the file contains the size of the data and is used to determine the
        proper buffer size for a single buffer fill.
         */
        ByteBuffer headerBuff = ByteBuffer.allocate(4);
        // TODO Consider locking the file here and releasing just before return
        fc.read(headerBuff);
        headerBuff.flip();
        ByteBuffer dataBuff = getBuffer(headerBuff.getInt());
        fc.read(dataBuff);
        fc.close();
        dataBuff.flip();

        composerFactory(dataBuff);

    }
}
