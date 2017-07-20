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

public class FlatArray2DRowRealMatrix extends FlatAbstractRealMatrix  implements Serializable {

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





        /**
         * Creates a matrix with no data
         */
        public FlatArray2DRowRealMatrix() {}

        /**
         * Create a new FlatRealMatrix with the supplied rowDimension and column dimensions.
         *
         * @param rowDimension Number of rows in the new matrix.
         * @param columnDimension Number of columns in the new matrix.
         * @throws NotStrictlyPositiveException if the rowDimension or column dimension is
         * not positive.
         */
        public FlatArray2DRowRealMatrix(final int rowDimension,
                                        final int columnDimension)
                throws NotStrictlyPositiveException {
            super(rowDimension, columnDimension);

            // the first eight bytes will hold the rowDimension and column sizes. Row = 1 : vector

            initialize(this.data, rowDimension, columnDimension);//puts header
        }

    private void initialize(byte[] data, int rowDimension, int columnDimension) {
        this.matrixRow=rowDimension;
        this.matrixColumn=columnDimension;
        data = new byte[rowDimension*columnDimension*DOUBLE +(HEADER)];
        ByteBuffer.wrap(data).putInt(rowDimension).putInt(columnDimension);

    }

    private ByteBuffer initializeBuffer(byte [] data, int rows, int columns){
        return ByteBuffer.wrap(data).putInt(rows).putInt(columns);
    }


    public FlatArray2DRowRealMatrix(final double[][] d)
                throws DimensionMismatchException, NoDataException, NullArgumentException {
            copyInB(d); // will be redefined
        }

    private void copyInB(double[][] d) {
        int inRow = d.length;
        int inColumn = d[0].length;
        initialize(this.data,inRow,inColumn);
        ByteBuffer dataPuter = ByteBuffer.wrap(this.data);
        for(int s = 0; s<inRow; s++){
            for (int k = 0; k<inColumn;k++)
                dataPuter.putDouble(getIndex(inColumn,s,k),d[s][k]);
        }
        dataPuter=null;

    }

    //this is the main constructor for our test

        public FlatArray2DRowRealMatrix(final byte [] data, int row, int column) {
            super(row,column);
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





        /**
         * Create a new RealMatrix using the input array as the underlying
         * data array.
         * If an array is built specially in order to be embedded in a
         * RealMatrix and not used directly, the {@code copyArray} may be
         * set to {@code false}. This will prevent the copying and improve
         * performance as no new array will be built and no data will be copied.
         *
         * @param d Data for new matrix.
         * @param copyArray if {@code true}, the input array will be copied,
         * otherwise it will be referenced.
         * @throws DimensionMismatchException if {@code d} is not rectangular.
         * @throws NoDataException if {@code d} rowDimension or column dimension is zero.
         * @throws NullArgumentException if {@code d} is {@code null}.
         * @see #Array2DRowRealMatrix(double[][])

        public FlatArray2DRowRealMatrix(final double[][] d, final boolean copyArray)
                throws DimensionMismatchException, NoDataException,
                NullArgumentException {
            if (copyArray) {
                copyIn(d);
            } else {
                if (d == null) {
                    throw new NullArgumentException();
                }
                final int nRows = d.length;
                if (nRows == 0) {
                    throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_ROW);
                }
                final int nCols = d[0].length;
                if (nCols == 0) {
                    throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
                }
                for (int r = 1; r < nRows; r++) {
                    if (d[r].length != nCols) {
                        throw new DimensionMismatchException(d[r].length, nCols);
                    }
                }
                data = d;
            }
        }
         */
        /**
         * Create a new (column) RealMatrix using {@code v} as the
         * data for the unique column of the created matrix.
         * The input array is copied.
         *
         * @param v Column vector holding data for new matrix.

        public FlatArray2DRowRealMatrix(final double[] v) {
            final int nRows = v.length;
            data = new double[nRows][1];
            for (int rowDimension = 0; rowDimension < nRows; rowDimension++) {
                data[rowDimension][0] = v[rowDimension];
            }
        }
         */
        /** {@inheritDoc} */
        @Override
        public RealMatrix createMatrix(final int rowDimension,
                                       final int columnDimension)
                throws NotStrictlyPositiveException {
            return new FlatArray2DRowRealMatrix(rowDimension, columnDimension);
        }

        /** {@inheritDoc} */
        @Override
        public RealMatrix copy() {
            return new FlatArray2DRowRealMatrix(copyOut());
        }//check TODO

        /**
         * Compute the sum of {@code this} and {@code m}.
         *
         * @param m Matrix to be added.
         * @return {@code this + m}.
         * @throws MatrixDimensionMismatchException if {@code m} is not the same
         * size as {@code this}.
         */
        public FlatArray2DRowRealMatrix add(final FlatArray2DRowRealMatrix m)
                throws MatrixDimensionMismatchException {
            // Safety check.
            MatrixUtils.checkAdditionCompatible(this, m);

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

        /**
         * Returns {@code this} minus {@code m}.
         *
         * @param m Matrix to be subtracted.
         * @return {@code this - m}
         * @throws MatrixDimensionMismatchException if {@code m} is not the same
         * size as {@code this}.
         */
        public FlatArray2DRowRealMatrix subtract(final FlatArray2DRowRealMatrix m)
                throws MatrixDimensionMismatchException {
            MatrixUtils.checkSubtractionCompatible(this, m);

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

        /**
         * Returns the result of postmultiplying {@code this} by {@code m}.
         *
         * @param m matrix to postmultiply by
         * @return {@code this * m}
         * @throws DimensionMismatchException if
         * {@code columnDimension(this) != rowDimension(m)}
         */
        public FlatArray2DRowRealMatrix multiply(final FlatArray2DRowRealMatrix m)
                throws DimensionMismatchException {
            MatrixUtils.checkMultiplicationCompatible(this, m);

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

        /** {@inheritDoc} */
        @Override
        public double[][] getData() {
            return copyOut();
        }

        public byte [] getDataFlat(){

            return copyOutB();
        }

        /**
         * Get a reference to the underlying data array.
         *
         * @return 2-dimensional array of entries.
         */
        public byte[] getDataRef() {
            return data;
        }

        /** {@inheritDoc} */
        @Override
        public void setSubMatrix(final double[][] subMatrix, final int row,
                                 final int column)
                throws NoDataException, OutOfRangeException,
                DimensionMismatchException, NullArgumentException {
            if (data == null) {
                if (row > 0) {
                    throw new MathIllegalStateException(LocalizedFormats.FIRST_ROWS_NOT_INITIALIZED_YET, row);
                }
                if (column > 0) {
                    throw new MathIllegalStateException(LocalizedFormats.FIRST_COLUMNS_NOT_INITIALIZED_YET, column);
                }
                MathUtils.checkNotNull(subMatrix);
                final int nRows = subMatrix.length;
                if (nRows == 0) {
                    throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_ROW);
                }

                final int nCols = subMatrix[0].length;
                if (nCols == 0) {
                    throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
                }
                data = new byte [subMatrix.length * nCols * DOUBLE + (HEADER)];

                ByteBuffer outdata = initializeBuffer(data,row, column);

                for (int i = 0; i < subMatrix.length; ++i) {
                    if (subMatrix[i].length != nCols) {
                        throw new DimensionMismatchException(subMatrix[i].length, nCols);
                    }
                    for (int k = 0; k < subMatrix[0].length; k++) {
                        outdata.putDouble(subMatrix[i][k]);

                    }
                }
            }else {
                super.setSubMatrix(subMatrix, row, column);
            }

        }

        /** {@inheritDoc} */
        @Override
        public double getEntry(final int row, final int column)
                throws OutOfRangeException {
            MatrixUtils.checkMatrixIndex(this, row, column);
            return ByteBuffer.wrap(data).getDouble(getIndex(matrixColumn,row,column));

        }

        /** {@inheritDoc} */
        @Override
        public void setEntry(final int row, final int column, final double value)
                throws OutOfRangeException {
            MatrixUtils.checkMatrixIndex(this, row, column);
            ByteBuffer.wrap(data).putDouble(getIndex(matrixColumn,row,column),value);
        }

        /** {@inheritDoc} */
        @Override
        public void addToEntry(final int row, final int column,
                               final double increment)
                throws OutOfRangeException {
            MatrixUtils.checkMatrixIndex(this, row, column);
            //move to postion, mark position, read data, at value, reset to mark, put result
            ByteBuffer outData = ByteBuffer.wrap(data);
            outData.position(getIndex(matrixColumn,row,column)).mark(); //marks the position
            double result = increment+outData.getDouble();
            outData.reset();// returns to the marked position
            outData.putDouble(result); // overwrites the data in this position

        }

        /** {@inheritDoc} */
        @Override
        public void multiplyEntry(final int row, final int column,
                                  final double factor)
                throws OutOfRangeException {
            MatrixUtils.checkMatrixIndex(this, row, column);
            ByteBuffer outData = ByteBuffer.wrap(data);
            outData.position(getIndex(matrixColumn,row,column)).mark(); //marks the position
            double result = factor*outData.getDouble();

            outData.reset();// returns to the marked position
            outData.putDouble(result); // overwrites the data in this position

        }

        /** {@inheritDoc} */
        @Override
        public int getRowDimension() {
            return (data == null) ? 0 : this.matrixRow;
        }

        /** {@inheritDoc} */
        @Override
        public int getColumnDimension() {
            return (data == null) ? 0 : this.matrixColumn;
        }

        /** {@inheritDoc} */
        @Override
        public double[] operate(final double[] v)
                throws DimensionMismatchException {
            final int nRows = this.getRowDimension();
            final int nCols = this.getColumnDimension();
            if (v.length != nCols) {
                throw new DimensionMismatchException(v.length, nCols);
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

        /** {@inheritDoc} */
        @Override
        public double[] preMultiply(final double[] v)
                throws DimensionMismatchException {
            final int nRows = getRowDimension();
            final int nCols = getColumnDimension();
            if (v.length != nRows) {
                throw new DimensionMismatchException(v.length, nRows);
            }

            final double[] out = new double[nCols];
            ByteBuffer outData = ByteBuffer.wrap(data);
            for (int col = 0; col < nCols; ++col) {
                double sum = 0;
                for (int i = 0; i < nRows; ++i) {
                    sum += outData.getDouble(getIndex(matrixColumn,i,col)) * v[i];
                }
                out[col] = sum;
            }

            return out;

        }

        public int getIndex(int mColumn, int row, int column) {

            return (HEADER + DOUBLE * ((this.matrixColumn * row) + column));
        }

        /** {@inheritDoc} */


        @Override
        public double walkInRowOrder(final RealMatrixChangingVisitor visitor) {
            final int rows    = getRowDimension();
            final int columns = getColumnDimension();
            ByteBuffer outData = ByteBuffer.wrap(data);
            visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);// how does visitor walk ToDO
            for (int i = 0; i < rows; ++i) {

                for (int j = 0; j < columns; ++j) {
                    int index = getIndex(this.matrixColumn,i, j);//not needed
                    //data.position(index).mark();
                    double result = outData.getDouble(index);
                    //data.reset();
                    outData.putDouble(index,(visitor.visit(i, j, result)));

                }
            }
            return visitor.end();
        }

        /** {@inheritDoc} */
        @Override
        public double walkInRowOrder(final RealMatrixPreservingVisitor visitor) {
            final int rows    = getRowDimension();
            final int columns = getColumnDimension();
            visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
            ByteBuffer outData = ByteBuffer.wrap(data);
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < columns; ++j) {
                    int index = getIndex(this.matrixColumn,i, j);//not needed
                    //data.position(index).mark();
                    double result = outData.getDouble(index);
                    //data.reset();
                    visitor.visit(i, j, result);
                }
            }
            return visitor.end();
        }

        /** {@inheritDoc} */
        @Override
        public double walkInRowOrder(final RealMatrixChangingVisitor visitor,
                                     final int startRow, final int endRow,
                                     final int startColumn, final int endColumn)
                throws OutOfRangeException, NumberIsTooSmallException {
            MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
            visitor.start(getRowDimension(), getColumnDimension(),
                    startRow, endRow, startColumn, endColumn);
            ByteBuffer outData = ByteBuffer.wrap(data);
            for (int i = startRow; i <= endRow; ++i) {

                for (int j = startColumn; j <= endColumn; ++j) {
                    int index = getIndex(this.matrixColumn,i, j);
                    double result = outData.getDouble(index);
                    outData.putDouble(index,(visitor.visit(i, j, result)));
                }
            }
            return visitor.end();
        }

        /** {@inheritDoc} */
        @Override
        public double walkInRowOrder(final RealMatrixPreservingVisitor visitor,
                                     final int startRow, final int endRow,
                                     final int startColumn, final int endColumn)
                throws OutOfRangeException, NumberIsTooSmallException {
            MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
            visitor.start(getRowDimension(), getColumnDimension(),
                    startRow, endRow, startColumn, endColumn);
            ByteBuffer outData = ByteBuffer.wrap(data);
            for (int i = startRow; i <= endRow; ++i) {
                for (int j = startColumn; j <= endColumn; ++j) {
                    int index = getIndex(this.matrixColumn,i, j);//not needed
                    //data.position(index).mark();
                    double result = outData.getDouble(index);
                    //data.reset();
                    visitor.visit(i, j, result);
                }
            }
            return visitor.end();
        }

        /** {@inheritDoc} */
        @Override
        public double walkInColumnOrder(final RealMatrixChangingVisitor visitor) {
            final int rows    = getRowDimension();
            final int columns = getColumnDimension();
            visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
            ByteBuffer outData = ByteBuffer.wrap(data);
            for (int j = 0; j < columns; ++j) {
                for (int i = 0; i < rows; ++i) {

                    int index = getIndex(this.matrixColumn,i, j);
                    double result = outData.getDouble(index);
                    outData.putDouble(index,(visitor.visit(i, j, result)));
                }
            }
            return visitor.end();
        }

        /** {@inheritDoc} */
        @Override
        public double walkInColumnOrder(final RealMatrixPreservingVisitor visitor) {
            final int rows    = getRowDimension();
            final int columns = getColumnDimension();
            visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
            ByteBuffer outData = ByteBuffer.wrap(data);
            for (int j = 0; j < columns; ++j) {
                for (int i = 0; i < rows; ++i) {
                    int index = getIndex(this.matrixColumn,i, j);
                    double result = outData.getDouble(index);
                    visitor.visit(i, j, result);
                }
            }
            return visitor.end();
        }

        /** {@inheritDoc} */
        @Override
        public double walkInColumnOrder(final RealMatrixChangingVisitor visitor,
                                        final int startRow, final int endRow,
                                        final int startColumn, final int endColumn)
                throws OutOfRangeException, NumberIsTooSmallException {
            MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
            visitor.start(getRowDimension(), getColumnDimension(),
                    startRow, endRow, startColumn, endColumn);
            ByteBuffer outData = ByteBuffer.wrap(data);
            for (int j = startColumn; j <= endColumn; ++j) {
                for (int i = startRow; i <= endRow; ++i) {
                    int index = getIndex(this.matrixColumn,i, j);
                    double result = outData.getDouble(index);
                    outData.putDouble(index,(visitor.visit(i, j, result)));
                }
            }
            return visitor.end();
        }

        /** {@inheritDoc} */
        @Override
        public double walkInColumnOrder(final RealMatrixPreservingVisitor visitor,
                                        final int startRow, final int endRow,
                                        final int startColumn, final int endColumn)
                throws OutOfRangeException, NumberIsTooSmallException {
            MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
            visitor.start(getRowDimension(), getColumnDimension(),
                    startRow, endRow, startColumn, endColumn);
            ByteBuffer outData = ByteBuffer.wrap(data);
            for (int j = startColumn; j <= endColumn; ++j) {
                for (int i = startRow; i <= endRow; ++i) {
                    int index = getIndex(this.matrixColumn,i, j);
                    double result = outData.getDouble(index);
                    visitor.visit(i, j, result);
                }
            }
            return visitor.end();
        }

        /**
         * Get a fresh copy of the underlying data array.
         *
         * @return a copy of the underlying data array. HERE does nothing but return empty array
         */
        private double[][] copyOut() {
            final int nRows = this.getRowDimension();
            final double[][] out = new double[nRows][this.getColumnDimension()];
            // can't copy 2-d array in one shot, otherwise get rowDimension references
            /*
            for (int i = 0; i < nRows; i++) {
                System.arraycopy(data[i], 0, out[i], 0, data[i].length);
            }
            */
            return out;
        }
        private byte[] copyOutB() {
            return this.data.clone();
        }

        /**
         * Replace data with a fresh copy of the input array.
         *
         * @param in Data to copy.
         * @throws NoDataException if the input array is empty.
         * @throws DimensionMismatchException if the input array is not rectangular.
         * @throws NullArgumentException if the input array is {@code null}.
         */
        private void copyIn(final double[][] in)
                throws DimensionMismatchException, NoDataException, NullArgumentException {
            setSubMatrix(in, 0, 0);
        }
    }


