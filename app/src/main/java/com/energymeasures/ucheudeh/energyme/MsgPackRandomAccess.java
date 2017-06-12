package com.energymeasures.ucheudeh.energyme;

import android.content.Context;
import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Created by ucheudeh on 6/9/17.
 */

public class MsgPackRandomAccess extends SerializeMe {
    Context context ;

    public MsgPackRandomAccess(Context context) {
        super();
        this.context= context;
    }

    @Override
    void doRead() {
        // full Object read
        String path = "MsgPSeekableBasisBig.dat";
        MessageUnpacker unMsgPk;
        File inFile = new File(context.getFilesDir(),path);


        try {
            RandomAccessFile inRand = new RandomAccessFile(inFile,"r");
            // a more reliable io method will be better e.g Java.nio
            int fileSize = (int)inRand.length();
            byte [] inBuff = new byte[fileSize];
            inRand.read(inBuff,0,fileSize);
            unMsgPk= MessagePack.newDefaultUnpacker(inBuff);

            inRand.close();
            int numMatrix = unMsgPk.unpackInt();
            int numVector = unMsgPk.unpackInt();
            ArrayList<Array2DRowRealMatrix> mat2D = new ArrayList<Array2DRowRealMatrix>();
            ArrayList <ArrayRealVector> snapshots = new ArrayList <ArrayRealVector>();

            for (int l=0;l<numMatrix;l++){
                int row = unMsgPk.unpackInt();
                int column = unMsgPk.unpackInt();
                double[][] root = new double [row][column];
                for (int t= 0; t<row;t++){
                    for (int q = 0; q<column;q++){
                        root[t][q]= unMsgPk.unpackDouble();
                    }
                }
                mat2D.add(new Array2DRowRealMatrix(root));
            }

            for (int w = 0; w<numVector; w++){
                int size = unMsgPk.unpackInt();
                double [] rootVector = new double[size];
                for ( int z = 0; z<size;z++){
                    rootVector[z]=unMsgPk.unpackDouble();
                }
                snapshots.add(new ArrayRealVector(rootVector));

            }


        } catch (IOException e) {
            // TODO: optimisation make a tag string object that is passed to all log entry for message pack
            Log.e("MsgPack",e.toString());
        }
        // get headers




    }



    @Override
    String doWrite(SnapshotsBasket flk) throws IOException {

        Log.i("MsgPackFLK_firstelement",Double.toString(flk.getAm(0).getEntry(1,1)));
        String outfile = "MsgPSeekableBasisBig.dat";
        int matrixNumber = flk.getAmSize();
        int vectorNumber = flk.getsnapshotSize();
        MessageBufferPacker msgPk = MessagePack.newDefaultBufferPacker();
        msgPk.packInt(matrixNumber);
        msgPk.packInt(vectorNumber);

        for(Array2DRowRealMatrix matAry: flk.getAm()){
            int row = matAry.getRowDimension();
            int column = matAry.getColumnDimension();

            //header
            msgPk.packInt(row);
            msgPk.packInt(column);
            for (int i = 0; i<row;i++){
                for(int j=0; j<column;j++){
                    msgPk.packDouble(matAry.getEntry(i,j));
                }
            }


        }

        for(ArrayRealVector vec: flk.getSnapshot()){
            int size = vec.getDimension();
            msgPk.packInt(size);
            for (int r=0; r<size;r++){
                msgPk.packDouble(vec.getEntry(r));
            }

        }

        byte[] buff = msgPk.toByteArray();

        msgPk.close();

        File out = new File(context.getFilesDir(),outfile);





        // Serialize BlockReal matices with message pack and write to file
        // for nio implementation use writeablebytechannel instead of outputstream
        try {

            RandomAccessFile fcOut = new RandomAccessFile(out,"rw");
            Long ponter1 = fcOut.getFilePointer();

            fcOut.write(buff);
            Long ponter2 = fcOut.getFilePointer();
            Log.i("MsgPackRandom file size", Long.toString(fcOut.length()));
            fcOut.close();


        } catch (FileNotFoundException e) {
            Log.e("MsgPackFilenotFound", e.toString());
        }catch (IOException e){
            Log.e("MsgPackIOexception", e.toString());
        }
        Log.i("MsgPack Size", Long.toString(out.length()));

        return out.getParent();
    }



}
