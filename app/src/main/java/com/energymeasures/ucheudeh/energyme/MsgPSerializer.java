package com.energymeasures.ucheudeh.energyme;

import android.content.Context;
import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;

import org.msgpack.core.*;
import org.msgpack.value.*;


/**
 * Created by ucheudeh on 5/27/17.
 */

public class MsgPSerializer extends SerializeMe {

    Context context ;
    ArrayList <Array2DRowRealMatrix> mat2D;
    ArrayList <ArrayRealVector> snapshots;


    public MsgPSerializer(Context context) {
        super();
        this.context= context;
    }

    @Override
    void doRead() {
        // full Object read
        String path = "MsgPBasisBig.dat";
        MessageUnpacker unMsgPk;


        try {
            FileInputStream in = context.openFileInput(path);
            // a more reliable io method will be better e.g Java.nio
            int fileSize = in.available();
            byte [] inBuff = new byte[fileSize];
            in.read(inBuff,0,fileSize);
            unMsgPk=MessagePack.newDefaultUnpacker(inBuff);

            in.close();
            int numMatrix = unMsgPk.unpackInt();
            int numVector = unMsgPk.unpackInt();
            mat2D = new ArrayList<Array2DRowRealMatrix>();
            snapshots = new ArrayList <ArrayRealVector>();

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

            Log.i("MsgPRE First element : ",Double.toString(mat2D.get(0).getEntry(1,1)));


        } catch (IOException e) {
            // TODO: optimisation make a tag string object that is passed to all log entry for message pack
            Log.e("MsgPack",e.toString());
        }
        // get headers




    }



    @Override
    String doWrite(SnapshotsBasket flk) throws IOException {

        Log.i("MsgPackFLK_firstelement",Double.toString(flk.getAm(0).getEntry(1,1)));
        String outfile = "MsgPBasisBig.dat";
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

            FileOutputStream fileOut = new FileOutputStream(out);

            fileOut.write(buff);


            fileOut.close();

        } catch (FileNotFoundException e) {
            Log.e("MsgPackFilenotFound", e.toString());
        }catch (IOException e){
            Log.e("MsgPackIOexception", e.toString());
        }
        Log.i("MsgPack Size", Long.toString(out.length()));

        return out.getParent();
    }

}
