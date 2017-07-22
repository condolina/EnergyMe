package com.energymeasures.ucheudeh.energyme;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by ucheudeh on 6/11/17.
 */

public class MappedReader extends SimpleReader {

    FileChannel fc;

    public MappedReader(File path) throws IOException {
        super(path);
        this.mode = "Mapped_Reader";
        try {
            //this.fc = new FileInputStream(path).getChannel();
            /*
            With RandomAccessFile in read only mode
            */
            this.fc = new RandomAccessFile(path,"r").getChannel();



        } catch (FileNotFoundException e) {
            Log.e(this.mode," : "+ e.toString());
        }

    }@Override
    public void read()throws IOException, FileNotFoundException {


        /*
        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

        TODO Send start trigger to Power tool


         */

        //startTime = System.nanoTime();// It is instructive to place the start time here b4 readIn().


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
    @Override
    void readIn() throws IOException, FileNotFoundException {
        final int INT_LENGHT = 4;

        //timeStamps.add(System.nanoTime());//Start file mapping
        // MMaps the entire file

        MappedByteBuffer dataBuff = fc.map(FileChannel.MapMode.READ_ONLY,0,fc.size());

        composerFactory(dataBuff);
    }
}
