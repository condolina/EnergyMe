package com.energymeasures.ucheudeh.energyme;

import java.nio.ByteBuffer;

/**
 * Created by ucheudeh on 8/31/17.
 */

public class Flatmatrix {

    public ByteBuffer data;
   public int matrixRow, matrixColumn;
    final int DOUBLE = 8;
    final int HEADER = 8;

    private void initialize(byte[] data, int rowDimension, int columnDimension) {
        this.matrixRow=rowDimension;
        this.matrixColumn=columnDimension;
        data = new byte[rowDimension*columnDimension*DOUBLE +(HEADER)];
        ByteBuffer.wrap(data).putInt(rowDimension).putInt(columnDimension);

    }

    private ByteBuffer initializeBuffer(ByteBuffer data, int rows, int columns){
        return data.putInt(rows).putInt(columns);
    }

    public Flatmatrix( ByteBuffer data, int row, int column) {

        this.matrixRow=row;
        this.matrixColumn=column;
        this.data = data;
    }

    public Flatmatrix(ByteBuffer data) {

        int row = data.getInt();
        int column = data.getInt();
        new Flatmatrix(data,row,column); // calls main constructor
    }

    public static Flatmatrix clonFlat(Flatmatrix m){

       return  new Flatmatrix(ByteBuffer.wrap(m.getData().array())
        , m.getRowDimension(), m.getColumnDimension());
    }

    public ByteBuffer getData() {
        return this.data;
    }

    public Flatmatrix add(final Flatmatrix m)
    {
        // Not checking Safety check. praying to Java


        final int rowCount    = m.getRowDimension();
        final int columnCount = m.getColumnDimension();
        if (rowCount!= this.getRowDimension()|| columnCount!= this.getColumnDimension()){
            System.exit(0);
        }
        final byte [] outByte = new byte[rowCount*columnCount*DOUBLE+(HEADER)];
        final ByteBuffer outData = ByteBuffer.wrap(outByte).putInt(rowCount).putInt(columnCount);
        //ByteBuffer a = ByteBuffer.wrap(this.data);
        //ByteBuffer b = ByteBuffer.wrap(m.data);
        this.data.rewind().position(HEADER);
        m.data.rewind().position(HEADER);
        for (int row = 0; row < rowCount; row++) {

            for (int col = 0; col < columnCount; col++) {
                outData.putDouble(this.data.getDouble() + m.data.getDouble());
            }
        }

        return new Flatmatrix(outData,rowCount,columnCount);
    }
    public int getRowDimension() {
        return this.matrixRow;
    }


    public int getColumnDimension() {
        return  this.matrixColumn;
    }





    public int getIndex(int mColumn, int row, int column) {

        return (HEADER + DOUBLE * ((this.matrixColumn * row) + column));
    }
}
