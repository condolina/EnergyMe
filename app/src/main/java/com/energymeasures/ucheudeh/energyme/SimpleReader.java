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
 * will have a bias towards Java NIO where ever possible. Channels and Buffers will be used
 * for all IOs
 */

class SimpleReader extends Reader {

    FileChannel fc;

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


        timeStamps.add(System.nanoTime());



        readIn();


        timeStamps.add(System.nanoTime());
        /*
        TODO: Send Stop Trigger to power tool

        norm the CVSWriter for now
        csvWriter2File();
         */



    }

    void readIn() throws IOException, FileNotFoundException {
        /*
        Header of the file contains the size of the data and is used to determine the
        proper buffer size for a single buffer fill.
         */
        timeStamps.add(System.nanoTime());//Buffer allocate header start/read data to Buffer_Start
        ByteBuffer headerBuff = ByteBuffer.allocate(4);
        // TODO Consider locking the file here and releasing just before return
        //timeStamps.add(System.nanoTime()); // Buffer allocate header end
        fc.read(headerBuff);
        //timeStamps.add(System.nanoTime());fill header buffer
        headerBuff.flip();
        //timeStamps.add(System.nanoTime());// buffer allocate main buffer start
        ByteBuffer dataBuff = getBuffer(headerBuff.getInt());
        //timeStamps.add(System.nanoTime());// buffer allocate main_end/start fill buffer
        fc.read(dataBuff);
        //timeStamps.add(System.nanoTime());// fill main buffer end
        fc.close();
        dataBuff.flip();
        timeStamps.add(System.nanoTime());//Read Data to buffer_end


        composerFactory(dataBuff);

    }
}
