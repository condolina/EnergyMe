package com.energymeasures.ucheudeh.energyme;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by ucheudeh on 6/11/17.
 */

public class RandomAccessReader extends Reader {

    /*
    Usage: After the Object of the class is instantiated with a file object to the desired file in
    application storage area. read(bringList, fetchmode) is called on the object. The fetch mode
    is an enum passed in that specifies how the file data is buffered: Early Fetch, loads the entire
    data, including the items on the bringlist, to a Buffer for quick access. Lazy fetch, only load
    the items on the bring list onto individual buffers. Each time a buffer is loaded it is sent
    for composing. The required
     */

    FileChannel fc;
    ByteBuffer library ; // read with getLong(int index).Application is indices-aware e.g. via http
    long libStart; // Start pointer for the Library, set by makeLibrary once
    Context context;

    public RandomAccessReader(File path, Context context) throws IOException {
        super(path);
        this.mode = "RandomAccessReader_Reader";
        try {

            this.fc = new RandomAccessFile(path,"r").getChannel(); // read-only mode

            //timeStamps.add(System.nanoTime());//Build library Start

            long fileSize = fc.size();
            ByteBuffer tailBuffer = getBuffer(8);
            long markPosition = fileSize - 8;
            fc.position(markPosition);
            fc.read(tailBuffer, (int) markPosition);
            tailBuffer.flip();
            libStart = tailBuffer.getLong();

            long libSize = (markPosition - libStart); // Hey... the size of the library
            library = getBuffer((int) libSize);

            fc.read(library, libStart);
            library.flip();
            long firstBuff = library.asLongBuffer().get(2);//Just for test
            Log.i("Library Ready. Bytes:", Long.toString(library.capacity()));//test


            // LIBRARY NOW part of this RandomAccessReader.

            //timeStamps.add(System.nanoTime());//Build Library End

        } catch (FileNotFoundException e) {
            Log.e(this.mode," : "+ e.toString());
        }

    }



    public enum ReadMode{
        EARLY_FETCH,LATE_FETCH
    }

    public void read(int[] bringList, ReadMode fetch ) throws IOException {

         /*
        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

        TODO Send start trigger to Power tool


         */

        startTime = System.nanoTime();// It is instructive to place the start time here b4 readIn().


       //timeStamps.add(System.nanoTime());



        switch (fetch){

            case EARLY_FETCH:

                readIn(bringList);
                break;

            case LATE_FETCH:

                readIn(bringList,fetch);
                break;

            default:
                Log.e(mode, "Wrong Random Access fetch mode issued");
                System.exit(1);
        }





       //timeStamps.add(System.nanoTime());
        /*
        TODO: Send Stop Trigger to power tool

         */

        //Read current //timeStamps and append to CVS file next line at the same time reset time to 0.





    }


        /*
        This method is the method called after creating the randomAccess object.
        It iterates the List, picks an integer index of the desired structure as pre-known from
        e.g. http sperate message regarding the content of this file. So the applicaition knows the
        index of each vector or matrix

        There are two options: lazy pull, or early pull. Lazy pull leaves the data on storage and
        puts only what is requested in memory. The are eventually more IOs to get every structure.
        However, itt may  be useful if NOT all the content of the file is needed. e.g. if a
        satisfactory result is already achieved with just the first few elements.

        The second: early pull, puts the entire file in memory and grants the request for specific
        structure from the Buffer in memory. Here there will be buffer copying each time structures
        are requested each with the bringList.
         */





    // Overloaded method EARLY RANDOM ACCESS READ
    // ReadMode in the parameter serves just to overload the method
    void readIn(int[] bringList, ReadMode fetch) throws IOException, FileNotFoundException {



        /*
        Make buffer to load the entire data area of the File
         */
        //timeStamps.add(System.nanoTime());//Allocate Buffer_Start
        //ByteBuffer fileBuffer = ByteBuffer.allocate((int)libStart-1);
        ByteBuffer fileBuffer = ByteBuffer.allocate((int)fc.size());
        //timeStamps.add(System.nanoTime());//Allocate  Buffer_End/ Read File-Start

        fc.position(0);//return filepointer to the start of file
        fc.read(fileBuffer);
        fileBuffer.flip();
        //timeStamps.add(System.nanoTime());//Main Buffer filled

        queueRequest(bringList, fileBuffer);
    }



    // Overloaded method LAZY RANDOM ACCESS READ
    void readIn(int [] bringList) throws IOException, FileNotFoundException {

        queueRequest(bringList);

    }



    private void queueRequest(int[] bringList) throws IOException {

        /*
        USAGE: The library holds a pointer to the start of the data, and the size of the needed buffer to
        get this Numerical data. Pointer is located at (2*index-2), while the size : (2*index-1).
        bringList has no index 0, must start from 1.
         */
        for(int index: bringList){

            //timeStamps.add(System.nanoTime());//Allocate Buffer for single record_Start
            if(index == 0)index=1;// incase user forgets. Kostenlos :-)
            long pointer =  library.asLongBuffer().get(2*index-2);
            int bufferSize = (int)library.asLongBuffer().get(2*index-1);
            ByteBuffer dataBuff = ByteBuffer.allocate(bufferSize);
            //timeStamps.add(System.nanoTime());//Allocate Buffer for Single record_ end/Fill buffer_S
            fc.read(dataBuff,pointer);
            //timeStamps.add(System.nanoTime());//Read record to buffer_end

            dataBuff.flip();

            //timeStamps.add(System.nanoTime());//Buffer for record Ready

            composerFactory(dataBuff);
        }
    }

    private void queueRequest(int [] bringList, ByteBuffer fileBuffer){
        for(int index: bringList){
            if(index == 0)index=1;
            //timeStamps.add(System.nanoTime());//Fill buffer for one record_Start
            long pointer =  library.asLongBuffer().get(2*index-2);
            int bufferSize = (int)library.asLongBuffer().get(2*index-1);
            byte [] dataArray = new byte[bufferSize];
            fileBuffer.position((int)pointer);
            fileBuffer.get(dataArray,0,dataArray.length);
            ByteBuffer dataBuff = ByteBuffer.wrap(dataArray);
            //ByteBuffer dataBuff = ByteBuffer.allocate(bufferSize);
            //for (int i=0; i<bufferSize+pointer;i++){
            //    dataBuff.put(fileBuffer.get((int)pointer+i));

            //}

            //dataBuff.flip();
            //timeStamps.add(System.nanoTime());//Buffer ready

            composerFactory(dataBuff);
        }
    }
}
