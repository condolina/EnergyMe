package com.energymeasures.ucheudeh.energyme;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by ucheudeh on 6/22/17.
 */

public class MMapRandomAccessReader extends RandomAccessReader {
    public MMapRandomAccessReader(File path, Context context) throws IOException {
        super(path, context);
    }


// we cater here for both read call signatures
    public void read(int[] bringList, ReadMode fetch ) throws IOException{

                 /*
        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

        TODO Send start trigger to Power tool


         */

        //startTime = System.nanoTime();// It is instructive to place the start time here b4 readIn().


        //timeStamps.add(System.nanoTime());

        readIn(bringList);

        //timeStamps.add(System.nanoTime());
        /*
        TODO: Send Stop Trigger to power tool

         */

        //Read current //timeStamps and append to CVS file next line at the same time reset time to 0.


    }

    public void read (int [] bringList) throws IOException {

                 /*
        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

        TODO Send start trigger to Power tool


         */

        //startTime = System.nanoTime();// It is instructive to place the start time here b4 readIn().


        //timeStamps.add(System.nanoTime());

        queueRequest(bringList);

        //timeStamps.add(System.nanoTime());
        /*
        TODO: Send Stop Trigger to power tool

         */

        //Read current //timeStamps and append to CVS file next line at the same time reset time to 0.
    }

    private void queueRequest(int[] bringList) throws IOException {

        /*
        USAGE: The library holds a pointer to the start of the data, and the size of file section.
        We shall be mapping just this section for each record
         */
        for(int index: bringList){

            //timeStamps.add(System.nanoTime());//Begin mmap of the file section
            if(index == 0)index=1;// incase user forgets. Kostenlos :-)
            long pointer =  library.asLongBuffer().get(2*index-2);
            int recordSize = (int)library.asLongBuffer().get(2*index-1);

// Map just this record to memory and return to the application
            MappedByteBuffer dataBuff = fc.map(FileChannel.MapMode.READ_ONLY,pointer,recordSize);
            //timeStamps.add(System.nanoTime());//Read record to buffer_end



            //timeStamps.add(System.nanoTime());//Buffer for record Ready

            composerFactory(dataBuff);
        }
    }

}
