package com.energymeasures.ucheudeh.energyme;

import android.content.Context;
import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.msgpack.core.*;


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

            Log.e("MsgPack",e.toString());
        }
        // get headers




    }

public void write(SnapshotsBasket numData) throws IOException {
    doWrite(numData);
    writeIndi(numData);
}

    @Override
    void doWrite(SnapshotsBasket flk) throws IOException {

        Log.i("MsgPackFLK_firstelement", Double.toString(flk.getMatrix(0).getEntry(1, 1)));
        String outfile = "MsgPBasisBig"+flk.getGroupType();
        int matrixNumber = flk.getNumMatrices();
        int vectorNumber = flk.getNumVectors();
        MessageBufferPacker msgPk = MessagePack.newDefaultBufferPacker();
        msgPk.packInt(matrixNumber);
        msgPk.packInt(vectorNumber);

        // normally in message pack one can define a header marker here for mostly for typeless arrays

        for (Array2DRowRealMatrix matAry : flk.getMatrixElements()) {
            int row = matAry.getRowDimension();
            int column = matAry.getColumnDimension();

            //header
            msgPk.packInt(row);
            msgPk.packInt(column);
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    msgPk.packDouble(matAry.getEntry(i,j));
                }
            }


        }

        for (ArrayRealVector vec : flk.getVectorElements()) {
            int size = vec.getDimension();
            msgPk.packInt(size);
            for (int r = 0; r < size; r++) {
                msgPk.packDouble(vec.getEntry(r));
            }

        }

        byte[] buff = msgPk.toByteArray();

        msgPk.close();


        File out = new File(context.getFilesDir(), outfile+".dat");

        // Serialize BlockReal matices with message pack and write to file
        // for nio implementation use writeablebytechannel instead of outputstream
        try {

            FileOutputStream fileOut = new FileOutputStream(out);

            fileOut.write(buff);


            fileOut.close();

        } catch (FileNotFoundException e) {
            Log.e("MsgPackFilenotFound", e.toString());
        } catch (IOException e) {
            Log.e("MsgPackIOexception", e.toString());
        }
        Log.i("InfoWrite_MsgPack Size_", outfile+"__ "+Long.toString(out.length()));
    }


        // WRITE INDI FILES next


    void writeIndi(SnapshotsBasket numData)throws IOException{

        // a new file per record
        MessageBufferPacker msgPk = MessagePack.newDefaultBufferPacker();
        String basename = "MsgPIndi"+numData.getGroupType();
        int k =0;
        for (Array2DRowRealMatrix matrix : numData.getMatrixElements()) {
            File path = new File ( context.getFilesDir(),basename.concat("m").concat(Integer.toString(k)).concat(".dat"));
            //e.g. MsgPIndim0.dat
            int row = matrix.getRowDimension();
            int column = matrix.getColumnDimension();

            //header
            msgPk.packInt(row);
            msgPk.packInt(column);
            for (int i = 0; i<row;i++){
                for(int j=0; j<column;j++){
                    msgPk.packDouble(matrix.getData()[i][j]);
                }
            }
            byte[] buff = msgPk.toByteArray();


            msgPk.flush();
            msgPk.clear();

            try {

                FileOutputStream fileOut = new FileOutputStream(path);

                fileOut.write(buff);


                fileOut.close();

                Log.i("File InfoWrite:FileSize", basename+"m"+k+"-"+Long.toString(path.length()));

            } catch (FileNotFoundException e) {
                Log.e("MsgPackFilenotFound", e.toString());
            }catch (IOException e){
                Log.e("MsgPackIOexception", e.toString());
            }
            k++;
        }

        k =0; // reinitialized
        for (ArrayRealVector vector : numData.getVectorElements()) {
            File path = new File ( context.getFilesDir(),basename.concat("v").concat(Integer.toString(k)).concat(".dat"));
            //e.g. regMappedv0.dat




                //header
            int size = vector.getDimension();
            msgPk.packInt(size);
            for (int r=0; r<size;r++){
                msgPk.packDouble(vector.toArray()[r]);
            }
                byte[] buff = msgPk.toByteArray();

                k++;
                msgPk.flush();
                msgPk.clear();

                try {

                    FileOutputStream fileOut = new FileOutputStream(path);

                    fileOut.write(buff);


                    fileOut.close();

                    Log.i("File InfoWrite:FileSize", basename+"v"+k+"-"+Long.toString(path.length()));

                } catch (FileNotFoundException e) {
                    Log.e("MsgPackFilenotFound", e.toString());
                }catch (IOException e){
                    Log.e("MsgPackIOexception", e.toString());
                }
        }
        msgPk.close();
    }

}


