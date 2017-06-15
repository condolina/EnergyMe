package com.energymeasures.ucheudeh.energyme;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
    ByteBuffer library = null; // read with getLong(int index).Application is indices-aware e.g. via http
    long libStart; // Start pointer for the Library, set by makeLibrary once

    public RandomAccessReader(File path) throws IOException {
        super(path);
        this.mode = "RandomAccessReader_Reader";
        try {
            this.fc = new RandomAccessFile(path,"r").getChannel();

            /*
            Build library
             */

            timeStamps.add(System.nanoTime());//Build library Start

            long fileSize = fc.size();
            ByteBuffer tailBuffer = getBuffer(8);
            long markPosition = fileSize - 7;
            fc.read(tailBuffer, (int) markPosition);
            tailBuffer.flip();
            libStart = tailBuffer.getLong();

            long libSize = (markPosition - libStart);
            library = getBuffer((int) libSize);

            fc.read(library, libStart);
            library.flip();

            timeStamps.add(System.nanoTime());//Build Library End

        } catch (FileNotFoundException e) {
            Log.e(this.mode," : "+ e.toString());
        }

    }



    public enum ReadMode{
        EARLY_FETCH,LATE_FETCH
    }

    public void read(Set <Integer> bringList, ReadMode fetch ) throws IOException {

         /*
        Assumption here is that it is possible to create a Reader without actually reading anything
        Time log begins when the read method is called on the Reader (INTERNAL VALIDATION)

        TODO Send start trigger to Power tool


         */


       timeStamps.add(System.nanoTime());



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





       timeStamps.add(System.nanoTime());
        /*
        TODO: Send Stop Trigger to power tool

         */

        //Read current timeStamps and append to CVS file next line at the same time reset time to 0.
        csvWriter2File();




    }


        /*
        This method is the method called after creating the randomAccess object.
        It iterates the Set, picks an integer index of the desired structure as pre-known from a
        e.g. http sperate message regarding the content of this file. So the applicaition knows the
        index of each vector or matrix

        There are two options: lazy pull, or early pull. Lazy pull leaves the data on storage and
        puts only what is requested in memory. The are eventually more IOs to get every structure.
        However, itt may  be useful if NOT all the content of the file is needed. e.g. if a
        satisfactory result is already achieved with just the first few elements.

        The second: early pull, puts the entire file in memory and grants the request for specific
        structure from the Buffer in memory. Here there will be buffer copying each time structures
        are requested each with the bringList.

        Read(File path,Set<Integer> bringList) as abstract  and implemented by RandomAccessReader.
        Other readers return null to this method in their implentation. This read calls ReadIn with
        ByteBuffer for each element
        The method
        -picks one element of this set which reflects what the application needs at the
        moment,
        -looks up the start index on the library buffer, an uses the size detail to create byte buffer
        - (fills the buffer with bytes from the start index+1 and returns to the read method.
        -retr
         */





    // Overloaded method EARLY RANDOM ACCESS READ
    // ReadMode in the parameter serves just to overload the method
    void readIn(Set <Integer> bringList, ReadMode fetch) throws IOException, FileNotFoundException {


        /*
        Make buffer to load the entire data area of the File
         */
        timeStamps.add(System.nanoTime());//Allocate Buffer_Start
        ByteBuffer fileBuffer = ByteBuffer.allocate((int)libStart-1);
        timeStamps.add(System.nanoTime());//Allocate  Buffer_End/ Read File-Start
        fc.read(fileBuffer);
        fileBuffer.flip();
        timeStamps.add(System.nanoTime());//Main Buffer filled

        queueRequest(bringList, fileBuffer);
    }



    // Overloaded method LAZY RANDOM ACCESS READ
    void readIn(Set <Integer> bringList) throws IOException, FileNotFoundException {

        queueRequest(bringList);

    }



    private void queueRequest(Set<Integer> bringList) throws IOException {

        /*
        The library holds a pointer to the start of the data, and the size of the needed buffer to
        get this Numerical data. Pointer is located at (2*index-2), while the size : (2*index-1).
         */
        for(Integer index: bringList){

            timeStamps.add(System.nanoTime());//Allocate Buffer for single record_Start
            long pointer =  library.getLong(2*index-2);
            int bufferSize = (int)library.getLong(2*index-1);
            ByteBuffer dataBuff = ByteBuffer.allocate(bufferSize);
            timeStamps.add(System.nanoTime());//Allocate Buffer for Single record_ end/Fill buffer_S
            fc.read(dataBuff,pointer);
            timeStamps.add(System.nanoTime());//Read record to buffer_end

            dataBuff.flip();

            timeStamps.add(System.nanoTime());//Buffer for record Ready

            composerFactory(dataBuff);
        }
    }

    private void queueRequest(Set<Integer> bringList, ByteBuffer fileBuffer){
        for(Integer index: bringList){
            timeStamps.add(System.nanoTime());//Fill buffer for one record_Start
            long pointer =  library.getLong(2*index-2);
            int bufferSize = (int)library.getLong(2*index-1);
            byte [] dataArray = new byte[bufferSize];
            fileBuffer.position((int)pointer);
            fileBuffer.get(dataArray,0,dataArray.length);
            ByteBuffer dataBuff = ByteBuffer.wrap(dataArray);

            dataBuff.flip();
            timeStamps.add(System.nanoTime());//Buffer ready

            composerFactory(dataBuff);
        }
    }
}
