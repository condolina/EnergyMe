package com.energymeasures.ucheudeh.energyme;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


;

/**
 * Created by ucheudeh on 6/11/17. Java NIO is considered more efficient than Java IO. As such we
 * will have a bias towards Java NIO where ever possible. Channels and Buffers will be used
 * for all IOs
 */

class SimpleReader extends Reader {
    final int PAGE_SIZE =4096;

    private int nativeFD=0;
    //int objectCounter =0;


    // the Overloaded read method does not use this object, but makes multiplstrms

    SimpleReader(File path) throws IOException {
        super(path);
        this.mode = "Simple_Reader";

    }

    public void read(Context context, String s)throws IOException {



    //Reads using buffer of same size as file
        readIn();









    }
    public native int nativeRead(int fd, ByteBuffer buf,int count,int offset, int bufOffset);
    //public native byte [] nativeRead(String path);

    static {
        System.loadLibrary("CMessenger");
    }



    private FileChannel connect(File path){

        FileChannel fc = null;


        try {
            fc = new FileInputStream(path).getChannel();
            return fc;


        } catch (FileNotFoundException e) {
            Log.e(this.mode," : "+ e.toString());
        }

        return fc;
    }

    private int readInChunks(boolean clustering, int direct, int bufSize, int readMode) throws IOException{//

        FileChannel fc = connect(path);
        int fileSize = (int)fc.size();

        int rCount,bCount,inBytes = 0;
        double [] vector = null;
        double [][] matrix = null;
        int sysCallsCount =1; // set to 1 due to extra call that returned EoF.
        boolean doneRead = false; // consider optimizatin to Bitfield

        int row = 0;
        int column = 0;
        int k = 0;
        final int DOUBLE = 8;
        final int HEADER = 8;
        ByteBuffer buf=null;
        byte [] bufBack=null;
        ByteBuffer tmp =null;

        if(direct == 1){
            buf = ByteBuffer.allocateDirect(bufSize);
        }else {
            bufBack = new byte[bufSize];
            buf = ByteBuffer.wrap(bufBack);

        }





       //while((rCount= fc.read(buf) )!=-1) { //normal read. Uncomment for pure Java reads. Then comment nativeRead.
        while((rCount=readInNative(buf,inBytes,tmp))!=-1){//nativeRead pass in null reference tmp
            sysCallsCount++;

            if (rCount == 0) break;
            inBytes+=rCount;
            buf.flip();
            while (buf.hasRemaining()) {
                if(buf.remaining()<HEADER) {

                    break;
                }// we want to be able to get header at the least
            if (row == 0) {
                row = buf.getInt();
                column = buf.getInt(); // we now have a header for a record check for correctness
            }
            if (row == 1) {
                if (vector == null) vector = new double[column];
                // sufficient to test if this is a vector or a matix

                    int rem = column - k;
                    bCount = Math.min(buf.remaining(), rem * DOUBLE);
                    for(int s =0;(s*DOUBLE)<bCount;s++) {
                        vector[k] = buf.getDouble();
                        k++;
                    }
                    if (k==column) {
                        vectorTable.add(new ArrayRealVector(vector));
                        k=0;
                        row =0;
                        column = 0;

                        if(!clustering){
                            doneRead = true;
                            break; // if single file reading stop here all bytes read.
                        }
                    };
                }else if (row > 1) {

                    if (matrix == null) matrix = new double[row][column];
                    // sufficient to test if this is a vector or a matix

                    int rem = (row * column) - k;
                    bCount = Math.min(buf.remaining(), rem * DOUBLE);
                for(int s =0;(s*DOUBLE)<bCount;s++) {
                        matrix[k / column][k % column] = buf.getDouble();
                        k++;
                    }
                    if (k == (column * row)) {
                        matriceTable.add(new Array2DRowRealMatrix(matrix));
                        k = 0;
                        row = 0;
                        column = 0;

                        //objectCounter++;
                        if(!clustering){
                            doneRead = true;
                            break; // if single file reading stop here all bytes read.
                        }
                    }

                    } else {
                        Log.e("contruct error", "rowDimension<=0");
                        System.exit(-1);
                    }



                }
                if (doneRead)break;; // saves on syscall add for bulk later ||inBytes==fileSizes
                buf.compact();

            }


        return sysCallsCount; // or bytes read
    }




    int readInNative( ByteBuffer buf, int offset, ByteBuffer tmp) throws IOException{
        // from the calling method, Offset it the size of the already read bytes returned previously
        if (nativeFD == 0)setNativeFD();
        int pos = buf.position();
        int lim = buf.limit();
        int rem = (pos <= lim?lim - pos: 0);
        if(buf.isDirect() ) {
            return nativeRead(nativeFD, buf, rem, offset,pos);
        }
        else{
            if (tmp == null){
                tmp = ByteBuffer.allocateDirect(buf.capacity());
            }

            int byteRead = nativeRead(nativeFD, tmp, rem, offset,pos);
            if (byteRead>0){
                //tmp.flip();
                buf.put(tmp);
            }

           return byteRead;
        }

    }

    private void setNativeFD() throws FileNotFoundException {
        nativeFD = ParcelFileDescriptor.open(path, ParcelFileDescriptor.MODE_READ_ONLY).getFd();
    }


    void readIn() throws IOException {
        /*
        Get the size of the file and make a buffer to contain the entire File.
         */
        //timeStamps.add(System.nanoTime());//Buffer allocate header start/read data to Buffer_Start

        FileChannel fc = connect(path);



        int dBuffSize = (int)fc.size();// only for testing will be passed directly on nextline
        ByteBuffer dataBuff = getBuffer(dBuffSize);


        fc.read(dataBuff);

        fc.close();
        dataBuff.flip();
        composerFactory(dataBuff);


    }

    public ExpBox read(Context context, String basename, List<String[]> replicate, boolean clustering)throws IOException {
        ArrayList<Long> durations = new ArrayList<>();
        ArrayList<String> headers = new ArrayList<>();




        //timeStamps.add(System.nanoTime());
        for (String [] exp: replicate) {
            int bufSize = Integer.parseInt(exp[4]);
            int direct = Integer.parseInt(exp[3]);
            int readMode = Integer.parseInt(exp[5]);

            //for (int i = 0; i < 4; i++) {
                //For 2k experiment we generate a random file selector between 0 and 3 inclusive.
                int k = getSelector(); //returns a random integer [0-3]
            //String filename = "regMappedIndidCorem3".concat(".dat");
            String filename = basename.concat(exp[2]+"m").concat(Integer.toString(k)).concat(".dat");//Basisfilem1.dat or 2kExpD_10_m2.dat
                //Log.i("read","starting");
                Long startTime = System.nanoTime();//here b4 readIn(). Not measuring open().
                path = new File(context.getFilesDir(), filename);
                int readBytes = readInChunks(clustering, direct, bufSize, readMode); // actual read method selected in read() see comments


                durations.add(System.nanoTime() - startTime);
                String header = exp[1] + exp[2] + "_" + exp[4] + "_" + "D" + exp[3] + "N" + exp[5];

                headers.add(header);


            //}
            exp[0]= "0"; // mark as done
        }

        return new ExpBox(durations,replicate,headers);
    }

    int getSelector() {
        int k =0;
        Random ran = new Random();
        k = ran.nextInt(4)+0;// min:0, max:3
        if(k==4)k=3;
        return k;
    }
}
