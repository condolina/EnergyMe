package com.energymeasures.ucheudeh.energyme;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.MatrixDimensionMismatchException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrixChangingVisitor;
import org.apache.commons.math3.linear.RealMatrixPreservingVisitor;
import org.apache.commons.math3.util.MathUtils;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Created by ucheudeh on 7/17/17.
 */

public class FlatArray2DRowRealMatrix  implements Serializable {

    /*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


        /** Serializable version identifier. */
        private static final long serialVersionUID = -1067294169172445528L;

        /** Entries of the matrix. */
        private byte [] data;
        private int matrixRow, matrixColumn;
        final int DOUBLE = 8;
        final int HEADER = 8;
    /*
    The goal of this experriment is to represent multidimentional data structureas such as a
    matrix as a byte buffer to make for faster IO in which case there will be no need to recompose
    data. We later test for overall energy of the applicaiton and the latency for the data read
    from storage
     */







    private void initialize(byte[] data, int rowDimension, int columnDimension) {
        this.matrixRow=rowDimension;
        this.matrixColumn=columnDimension;
        data = new byte[rowDimension*columnDimension*DOUBLE +(HEADER)];
        ByteBuffer.wrap(data).putInt(rowDimension).putInt(columnDimension);

    }

    private ByteBuffer initializeBuffer(byte [] data, int rows, int columns){
        return ByteBuffer.wrap(data).putInt(rows).putInt(columns);
    }





    //this is the main constructor for our test

        public FlatArray2DRowRealMatrix(final byte [] data, int row, int column) {

            this.matrixRow=row;
            this.matrixColumn=column;
            this.data = data;
        }

    public FlatArray2DRowRealMatrix(final byte [] data) {
        ByteBuffer headRead = ByteBuffer.wrap(data);
        int row = headRead.getInt();
        int column = headRead.getInt();
        new FlatArray2DRowRealMatrix(data,row,column); // calls main constructor
    }

        public FlatArray2DRowRealMatrix add(final FlatArray2DRowRealMatrix m)
                {
            // Not checking Safety check. praying to Java


            final int rowCount    = getRowDimension();
            final int columnCount = getColumnDimension();
            final byte [] outByte = new byte[rowCount*columnCount*DOUBLE+(HEADER)];
            final ByteBuffer outData = initializeBuffer(outByte,rowCount,columnCount);
            ByteBuffer a = ByteBuffer.wrap(this.data);
            ByteBuffer b = ByteBuffer.wrap(m.data);
            a.rewind().position(HEADER);
            b.rewind().position(HEADER);
            for (int row = 0; row < rowCount; row++) {

                for (int col = 0; col < columnCount; col++) {
                    outData.putDouble(a.getDouble() + b.getDouble());
                }
            }

            return new FlatArray2DRowRealMatrix(outByte,rowCount,columnCount);
        }


        public FlatArray2DRowRealMatrix subtract(final FlatArray2DRowRealMatrix m){

            final int rowCount    = getRowDimension();
            final int columnCount = getColumnDimension();
            final byte [] outByte = new byte[rowCount*columnCount*DOUBLE+(HEADER)];
            final ByteBuffer outData = initializeBuffer(outByte,rowCount,columnCount);
            ByteBuffer a = ByteBuffer.wrap(this.data);
            ByteBuffer b = ByteBuffer.wrap(m.data);
            a.rewind().position(HEADER);
            b.rewind().position(HEADER);
            for (int row = 0; row < rowCount; row++) {

                for (int col = 0; col < columnCount; col++) {
                    outData.putDouble(a.getDouble() - b.getDouble());
                }
            }

            return new FlatArray2DRowRealMatrix(outByte,rowCount,columnCount);
        }

        public FlatArray2DRowRealMatrix multiply(final FlatArray2DRowRealMatrix m)
                {

            final int rowCount = this.getRowDimension();
            final int columnCount = m.getColumnDimension();
            final int nSum = this.getColumnDimension();

            final byte [] outByte = new byte[rowCount * columnCount *DOUBLE+(HEADER)];
            final ByteBuffer outData = initializeBuffer(outByte,rowCount,columnCount);

            //final double[][] outData = new double[rowCount][columnCount];
            // Will hold a column of "m".
            for (int mC = 0; mC< columnCount; mC++) {
                for (int tR = 0; tR < rowCount; tR++) {
                    double result = 0;
                    for (int mT = 0; mT < nSum; mT++) {
                        result += this.getEntry(tR, mT) * m.getEntry(mT, mC);
                    }
                    outData.putDouble((HEADER + (DOUBLE * ((columnCount * tR) + mC)))); // make general method
                }
            }



            return new FlatArray2DRowRealMatrix(outByte, rowCount, columnCount);
        }



        /**
         * Get a reference to the underlying data array.
         *
         * @return 2-dimensional array of entries.
         */
        public byte[] getDataRef() {
            return data;
        }




        public double getEntry(final int row, final int column)
                throws OutOfRangeException {

            return ByteBuffer.wrap(data).getDouble(getIndex(matrixColumn,row,column));

        }


        public void setEntry(final int row, final int column, final double value)
                throws OutOfRangeException {

            ByteBuffer.wrap(data).putDouble(getIndex(matrixColumn,row,column),value);
        }


        public void addToEntry(final int row, final int column,
                               final double increment)
                 {

            //move to postion, mark position, read data, at value, reset to mark, put result
            ByteBuffer outData = ByteBuffer.wrap(data);
            outData.position(getIndex(matrixColumn,row,column)).mark(); //marks the position
            double result = increment+outData.getDouble();
            outData.reset();// returns to the marked position
            outData.putDouble(result); // overwrites the data in this position

        }


        public void multiplyEntry(final int row, final int column,
                                  final double factor)
                {

            ByteBuffer outData = ByteBuffer.wrap(data);
            outData.position(getIndex(matrixColumn,row,column)).mark(); //marks the position
            double result = factor*outData.getDouble();

            outData.reset();// returns to the marked position
            outData.putDouble(result); // overwrites the data in this position

        }

        public int getRowDimension() {
            return (data == null) ? 0 : this.matrixRow;
        }


        public int getColumnDimension() {
            return (data == null) ? 0 : this.matrixColumn;
        }

        public double[] operate(final double[] v)
               {
            final int nRows = this.getRowDimension();
            final int nCols = this.getColumnDimension();
            if (v.length != nCols) {
            }
            final double[] out = new double[nRows];
            ByteBuffer outData = ByteBuffer.wrap(data);
            for (int row = 0; row < nRows; row++) {

                double sum = 0;
                for (int i = 0; i < nCols; i++) {
                    sum += outData.getDouble(getIndex(matrixColumn,row,i))  * v[i];
                }
                out[row] = sum;
            }
            return out;
        }




        public int getIndex(int mColumn, int row, int column) {

            return (HEADER + DOUBLE * ((this.matrixColumn * row) + column));
        }

        /** {@inheritDoc} */




    }


