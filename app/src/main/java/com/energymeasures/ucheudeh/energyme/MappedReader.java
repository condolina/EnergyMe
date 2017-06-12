package com.energymeasures.ucheudeh.energyme;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by ucheudeh on 6/11/17.
 */

public class MappedReader extends SimpleReader {

    public MappedReader(File path){
        super(path);
        this.mode = "Mapped_Reader";
        try {
            this.fc = new FileInputStream(path).getChannel();

        } catch (FileNotFoundException e) {
            Log.e(this.mode," : "+ e.toString());
        }

    }
    @Override
    void readIn() throws IOException, FileNotFoundException {
        final int INT_LENGHT = 4;

        MappedByteBuffer dataBuff = fc.map(FileChannel.MapMode.READ_ONLY,INT_LENGHT,fc.size()-INT_LENGHT);
        fc.close();
        composerFactory(dataBuff);
    }
}
