package com.energymeasures.ucheudeh.energyme;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ucheudeh on 6/11/17.
 */

public class MappedReader extends SimpleReader {

    FileChannel fc;

    public MappedReader(File path) throws IOException {
        super(path);
        this.mode = "Mapped_Reader";
        //this.fc = new FileInputStream(path).getChannel();
            /*
            With RandomAccessFile in read only mode
            */
        //this.fc = new RandomAccessFile(path,"r").getChannel();


    }
    public ExpBox read(Context context, String basename, List<String[]> replicate)throws IOException {


        ArrayList<Long> durations = new ArrayList<>();
        ArrayList<String> headers = new ArrayList<>();
        boolean clustering = true;

        for (String [] exp: replicate) {
        int k = getSelector(); //returns a random integer [0-3]
        String filename = basename.concat(exp[4]+"m").concat(Integer.toString(k)).concat(".dat");//Basisfilem1.dat or 2kExpD_10_m2.dat
        //Log.i("read","starting");
        Long startTime = System.nanoTime();//here b4 readIn(). Not measuring open().
        path = new File(context.getFilesDir(), filename);
        readIn();
            durations.add(System.nanoTime() - startTime);
            String header = exp[1] + exp[4] + exp[2];

            headers.add(header);
            exp[0]= "0"; // mark as done
        }

        return new ExpBox(durations,replicate,headers);

    }


    @Override
    void readIn() throws IOException, FileNotFoundException {

        // MMaps the entire file
        fc = new RandomAccessFile(path,"r").getChannel();

        MappedByteBuffer dataBuff = fc.map(FileChannel.MapMode.READ_ONLY,0,fc.size());

        composerFactory(dataBuff);
    }



}
