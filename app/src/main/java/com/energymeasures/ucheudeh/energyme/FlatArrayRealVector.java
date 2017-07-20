package com.energymeasures.ucheudeh.energyme;

import android.util.Log;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.RealVectorFormat;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by ucheudeh on 7/19/17.
 */

public class FlatArrayRealVector implements Serializable {
    /** Serializable version identifier. */
    private static final long serialVersionUID = -1097961340710804027L;
    /** Default format. */
    private static final RealVectorFormat DEFAULT_FORMAT = RealVectorFormat.getInstance();

    /** Entries of the vector. */
    private byte data[];
    final int HEADER = 8;
    final int rowDimension = 1;
    final int DOUBLE = 8;
    int columnDimension;
    /**
     * Build a 0-length vector.
     * Zero-length vectors may be used to initialized construction of vectors
     * by data gathering. We start with zero-length and use either the {@link
     * #(FlatArrayRealVector, FlatArrayRealVector)} constructor

     */
    public FlatArrayRealVector() {
        data = new byte[HEADER];
    }

    /**
     * Construct a vector of zeroes.
     *
     * @param size Size of the vector.
     */
    public FlatArrayRealVector(int size) {
        data = new byte[size+HEADER];
        initialize(data, size);
    }

    public FlatArrayRealVector(byte [] readyByte, int size) {
        if (integrityCheck(readyByte)){
        this.data = readyByte;
        this.columnDimension = size;
        }else{
            Log.e("Incompartible Object: ","Row is not 1");
            System.exit(-1);
        }
    }
/*
checks that this is a valid vecor
 */
    private boolean integrityCheck(byte[] readyByte) {
        int lengthCount= readyByte.length;
        ByteBuffer check = ByteBuffer.wrap(readyByte);
        int row = check.getInt();
        int dimension = check.getInt();
        if ((row*dimension*DOUBLE+HEADER)==lengthCount&row ==1){
            return true;
        }
        return false;
    }

    private void initialize(byte[] data, int size) {
        ByteBuffer.wrap(data).putInt(rowDimension).putInt(size);
        this.columnDimension = size;
    }

    /**
     * Construct a vector with preset values.
     *
     * @param size Size of the vector
     * @param preset All entries will be set with this value.
     */
    public FlatArrayRealVector(int size, double preset) {
        double [] dataArray  = new double [size];
        Arrays.fill(dataArray, preset);
        copyOut(dataArray,size);
    }

    private void copyOut(double[] dataArray, int size) {
        byte [] outByte = new byte[size+HEADER];
        ByteBuffer outData = intializeBuffer(outByte,size);
        for (int i=0; i<size;i++)outData.putDouble(dataArray[i]);
        data = outByte;
        this.columnDimension = size;
    }

    private ByteBuffer intializeBuffer(byte[] outByte, int length) {
        return ByteBuffer.wrap(outByte).putInt(rowDimension).putInt(length);
    }

    /**
     * Construct a vector from an array, copying the input array.
     *
     * @param d Array.
     */
    public FlatArrayRealVector(double[] d) {
        copyOut(d, d.length);
    }

    /**
     * Create a new ArrayRealVector using the input array as the underlying
     * data array.
     * If an array is built specially in order to be embedded in a
     * ArrayRealVector and not used directly, the {@code copyArray} may be
     * set to {@code false}. This will prevent the copying and improve
     * performance as no new array will be built and no data will be copied.
     *
     * @param d Data for the new vector.
     * @param copyArray if {@code true}, the input array will be copied,
     * otherwise it will be referenced.
     * @throws NullArgumentException if {@code d} is {@code null}.
     * @see #ArrayRealVector(double[])
     */
    /*
    public ArrayRealVector(double[] d, boolean copyArray)
            throws NullArgumentException {
        if (d == null) {
            throw new NullArgumentException();
        }
        data = copyArray ? d.clone() :  d;
    }
*/
    /**
     * Construct a vector from part of a array.
     *
     * @param d Array.
     * @param pos Position of first entry.
     * @param size Number of entries to copy.
     * @throws NullArgumentException if {@code d} is {@code null}.
     * @throws NumberIsTooLargeException if the size of {@code d} is less
     * than {@code pos + size}.
     *//*
    public ArrayRealVector(double[] d, int pos, int size)
            throws NullArgumentException, NumberIsTooLargeException {
        if (d == null) {
            throw new NullArgumentException();
        }
        if (d.length < pos + size) {
            throw new NumberIsTooLargeException(pos + size, d.length, true);
        }
        data = new double[size];
        System.arraycopy(d, pos, data, 0, size);
    }
*/
    /**
     * Construct a vector from an array.
     *
     * @param d Array of {@code Double}s.
     */
    /*
    public FlatArrayRealVector(Double[] d) {
        data = new double[d.length];
        for (int i = 0; i < d.length; i++) {
            data[i] = d[i].doubleValue();
        }
    }
*/
    /**
     * Construct a vector from part of an array.
     *
     * @param d Array.
     * @param pos Position of first entry.
     * @param size Number of entries to copy.
     * @throws NullArgumentException if {@code d} is {@code null}.
     * @throws NumberIsTooLargeException if the size of {@code d} is less
     * than {@code pos + size}.
     */
    /*
    public FlatArrayRealVector(Double[] d, int pos, int size)
            throws NullArgumentException, NumberIsTooLargeException {
        if (d == null) {
            throw new NullArgumentException();
        }
        if (d.length < pos + size) {
            throw new NumberIsTooLargeException(pos + size, d.length, true);
        }
        data = new double[size];
        for (int i = pos; i < pos + size; i++) {
            data[i - pos] = d[i].doubleValue();
        }
    }
    */

    /**
     * Construct a vector from another vector, using a deep copy.
     *
     * @param v vector to copy.
     * @throws NullArgumentException if {@code v} is {@code null}.
     */
    /*
    public FlatArrayRealVector(FlatArrayRealVector v) throws NullArgumentException {
        if (v == null) {
            throw new NullArgumentException();
        }
        data = new double[v.getDimension()];
        for (int i = 0; i < data.length; ++i) {
            data[i] = v.getEntry(i);
        }
    }
*/
    /**
     * Construct a vector from another vector, using a deep copy.
     *
     * @param v Vector to copy.
     * @throws NullArgumentException if {@code v} is {@code null}.
     */
    /*
    public FlatArrayRealVector(FlatArrayRealVector v) throws NullArgumentException {
        this(v, true);
    }
*/
    /**
     * Construct a vector from another vector.
     *
     * @param v Vector to copy.
     * @param deep If {@code true} perform a deep copy, otherwise perform a
     * shallow copy.
     */
   /* public ArrayRealVector(FlatArrayRealVector v, boolean deep) {
        data = deep ? v.data.clone() : v.data;
    }
*/
    /**
     * Construct a vector by appending one vector to another vector.
     * @param v1 First vector (will be put in front of the new vector).
     * @param v2 Second vector (will be put at back of the new vector).
     */
 /*   public ArrayRealVector(FlatArrayRealVector v1, FlatArrayRealVector v2) {
        data = new double[v1.data.length + v2.data.length];
        System.arraycopy(v1.data, 0, data, 0, v1.data.length);
        System.arraycopy(v2.data, 0, data, v1.data.length, v2.data.length);
    }
*/
    /**
     * Construct a vector by appending one vector to another vector.
     * @param v1 First vector (will be put in front of the new vector).
     * @param v2 Second vector (will be put at back of the new vector).
     */
 /*   public ArrayRealVector(FlatArrayRealVector v1, FlatArrayRealVector v2) {
        final int l1 = v1.data.length;
        final int l2 = v2.getDimension();
        data = new double[l1 + l2];
        System.arraycopy(v1.data, 0, data, 0, l1);
        for (int i = 0; i < l2; ++i) {
            data[l1 + i] = v2.getEntry(i);
        }
    }
*/
    /**
     * Construct a vector by appending one vector to another vector.
     * @param v1 First vector (will be put in front of the new vector).
     * @param v2 Second vector (will be put at back of the new vector).
     */
 /*   public ArrayRealVector(FlatArrayRealVector v1, FlatArrayRealVector v2) {
        final int l1 = v1.getDimension();
        final int l2 = v2.data.length;
        data = new double[l1 + l2];
        for (int i = 0; i < l1; ++i) {
            data[i] = v1.getEntry(i);
        }
        System.arraycopy(v2.data, 0, data, l1, l2);
    }
*/
    /**
     * Construct a vector by appending one vector to another vector.
     * @param v1 First vector (will be put in front of the new vector).
     * @param v2 Second vector (will be put at back of the new vector).
     */
  /*  public ArrayRealVector(FlatArrayRealVector v1, double[] v2) {
        final int l1 = v1.getDimension();
        final int l2 = v2.length;
        data = new double[l1 + l2];
        System.arraycopy(v1.data, 0, data, 0, l1);
        System.arraycopy(v2, 0, data, l1, l2);
    }
*/
    /**
     * Construct a vector by appending one vector to another vector.
     * @param v1 First vector (will be put in front of the new vector).
     * @param v2 Second vector (will be put at back of the new vector).
     */
/*    public ArrayRealVector(double[] v1, FlatArrayRealVector v2) {
        final int l1 = v1.length;
        final int l2 = v2.getDimension();
        data = new double[l1 + l2];
        System.arraycopy(v1, 0, data, 0, l1);
        System.arraycopy(v2.data, 0, data, l1, l2);
    }
*/
    /**
     * Construct a vector by appending one vector to another vector.
     * @param v1 first vector (will be put in front of the new vector)
     * @param v2 second vector (will be put at back of the new vector)
     */
/*    public ArrayRealVector(double[] v1, double[] v2) {
        final int l1 = v1.length;
        final int l2 = v2.length;
        data = new double[l1 + l2];
        System.arraycopy(v1, 0, data, 0, l1);
        System.arraycopy(v2, 0, data, l1, l2);
    }
*/
    /** {@inheritDoc} */
 /*
    public FlatArrayRealVector copy() {
        return new FlatArrayRealVector(this, true);
    }
*/
    /** {@inheritDoc} */

    public int getIndex( int column) {

        return (HEADER + DOUBLE * ((this.columnDimension * rowDimension) + column));
    }
   /*
   returns a FlatArrayRealVector with the same size as this vector
    */
    public FlatArrayRealVector add(FlatArrayRealVector v)

    {
        int dim = this.columnDimension;

        byte[] outByte = this.data.clone();
        try {

            ByteBuffer outData = ByteBuffer.wrap(outByte); // makes a clone of data
            ByteBuffer vData = ByteBuffer.wrap(v.data);

            for (int i = 0; i < dim; i++) {
                outData.putDouble(getIndex(i), (outData.getDouble(getIndex(i)) + vData.getDouble(getIndex(i))));
            }



        } catch (DimensionMismatchException e) {
            Log.e("Incompatible size: ", e.toString());
            System.exit(-1);
        }
        return new FlatArrayRealVector(outByte, dim);
    }
    /** {@inheritDoc} */
   
    public FlatArrayRealVector subtract(FlatArrayRealVector v)
    {
        int dim = this.columnDimension;

        byte[] outByte = this.data.clone();
        try {

            ByteBuffer outData = ByteBuffer.wrap(outByte); // makes a clone of data
            ByteBuffer vData = ByteBuffer.wrap(v.data);

            for (int i = 0; i < dim; i++) {
                outData.putDouble(getIndex(i), (outData.getDouble(getIndex(i)) - vData.getDouble(getIndex(i))));
            }



        } catch (DimensionMismatchException e) {
            Log.e("Incompatible size: ", e.toString());
            System.exit(-1);
        }
        return new FlatArrayRealVector(outByte, dim);
    }

    /** {@inheritDoc} */
   /*
    public FlatArrayRealVector map(UnivariateFunction function) {
        return copy().mapToSelf(function);
    }
*/
    /** {@inheritDoc} */
  /*
    public FlatArrayRealVector mapToSelf(UnivariateFunction function) {
        for (int i = 0; i < data.length; i++) {
            data[i] = function.value(data[i]);
        }
        return this;
    }
*/
    /** {@inheritDoc} */
/*
    public FlatArrayRealVector mapAddToSelf(double d) {
        for (int i = 0; i < data.length; i++) {
            data[i] += d;
        }
        return this;
    }
*/
    /** {@inheritDoc} */
/*
    public FlatArrayRealVector mapSubtractToSelf(double d) {
        for (int i = 0; i < data.length; i++) {
            data[i] -= d;
        }
        return this;
    }
*/
    /** {@inheritDoc} */
/*
    public FlatArrayRealVector mapMultiplyToSelf(double d) {
        for (int i = 0; i < data.length; i++) {
            data[i] *= d;
        }
        return this;
    }
*/
    /** {@inheritDoc} */
 /*
    public FlatArrayRealVector mapDivideToSelf(double d) {
        for (int i = 0; i < data.length; i++) {
            data[i] /= d;
        }
        return this;
    }
*/
    /** {@inheritDoc} */
/*
    public FlatArrayRealVector ebeMultiply(FlatArrayRealVector v)
            throws DimensionMismatchException {
        if (v instanceof FlatArrayRealVector) {
            final double[] vData = ((FlatArrayRealVector) v).data;
            final int dim = vData.length;
            checkVectorDimensions(dim);
            FlatArrayRealVector result = new FlatArrayRealVector(dim);
            double[] resultData = result.data;
            for (int i = 0; i < dim; i++) {
                resultData[i] = data[i] * vData[i];
            }
            return result;
        } else {
            checkVectorDimensions(v);
            double[] out = data.clone();
            for (int i = 0; i < data.length; i++) {
                out[i] *= v.getEntry(i);
            }
            return new FlatArrayRealVector(out, false);
        }
    }
*/
    /** {@inheritDoc} */
/*
    public FlatArrayRealVector ebeDivide(FlatArrayRealVector v)
            throws DimensionMismatchException {
        if (v instanceof FlatArrayRealVector) {
            final double[] vData = ((FlatArrayRealVector) v).data;
            final int dim = vData.length;
            checkVectorDimensions(dim);
            FlatArrayRealVector result = new FlatArrayRealVector(dim);
            double[] resultData = result.data;
            for (int i = 0; i < dim; i++) {
                resultData[i] = data[i] / vData[i];
            }
            return result;
        } else {
            checkVectorDimensions(v);
            double[] out = data.clone();
            for (int i = 0; i < data.length; i++) {
                out[i] /= v.getEntry(i);
            }
            return new FlatArrayRealVector(out, false);
        }
    }
*/
    /**
     * Get a reference to the underlying data array.
     * This method does not make a fresh copy of the underlying data.
     *
     * @return the array of entries.
     */
    /*
    public double[] getDataRef() {
        return data;
    }
*/
    /** {@inheritDoc} */
 /*
    public double dotProduct(FlatArrayRealVector v) throws DimensionMismatchException {
        if (v instanceof FlatArrayRealVector) {
            final double[] vData = ((FlatArrayRealVector) v).data;
            checkVectorDimensions(vData.length);
            double dot = 0;
            for (int i = 0; i < data.length; i++) {
                dot += data[i] * vData[i];
            }
            return dot;
        }
        return super.dotProduct(v);
    }
*/
    /** {@inheritDoc} */
/*
    public double getNorm() {
        double sum = 0;
        for (double a : data) {
            sum += a * a;
        }
        return FastMath.sqrt(sum);
    }
*/
    /** {@inheritDoc} */
 /*
    public double getL1Norm() {
        double sum = 0;
        for (double a : data) {
            sum += FastMath.abs(a);
        }
        return sum;
    }
*/
    /** {@inheritDoc} */
/*
    public double getLInfNorm() {
        double max = 0;
        for (double a : data) {
            max = FastMath.max(max, FastMath.abs(a));
        }
        return max;
    }
*/
    /** {@inheritDoc} */
/*
    public double getDistance(FlatArrayRealVector v) throws DimensionMismatchException {
        if (v instanceof FlatArrayRealVector) {
            final double[] vData = ((FlatArrayRealVector) v).data;
            checkVectorDimensions(vData.length);
            double sum = 0;
            for (int i = 0; i < data.length; ++i) {
                final double delta = data[i] - vData[i];
                sum += delta * delta;
            }
            return FastMath.sqrt(sum);
        } else {
            checkVectorDimensions(v);
            double sum = 0;
            for (int i = 0; i < data.length; ++i) {
                final double delta = data[i] - v.getEntry(i);
                sum += delta * delta;
            }
            return FastMath.sqrt(sum);
        }
    }
*/
    /** {@inheritDoc} */
/*
    public double getL1Distance(FlatArrayRealVector v)
            throws DimensionMismatchException {
        if (v instanceof FlatArrayRealVector) {
            final double[] vData = ((FlatArrayRealVector) v).data;
            checkVectorDimensions(vData.length);
            double sum = 0;
            for (int i = 0; i < data.length; ++i) {
                final double delta = data[i] - vData[i];
                sum += FastMath.abs(delta);
            }
            return sum;
        } else {
            checkVectorDimensions(v);
            double sum = 0;
            for (int i = 0; i < data.length; ++i) {
                final double delta = data[i] - v.getEntry(i);
                sum += FastMath.abs(delta);
            }
            return sum;
        }
    }
*/
    /** {@inheritDoc} */
/*
    public double getLInfDistance(FlatArrayRealVector v)
            throws DimensionMismatchException {
        if (v instanceof FlatArrayRealVector) {
            final double[] vData = ((FlatArrayRealVector) v).data;
            checkVectorDimensions(vData.length);
            double max = 0;
            for (int i = 0; i < data.length; ++i) {
                final double delta = data[i] - vData[i];
                max = FastMath.max(max, FastMath.abs(delta));
            }
            return max;
        } else {
            checkVectorDimensions(v);
            double max = 0;
            for (int i = 0; i < data.length; ++i) {
                final double delta = data[i] - v.getEntry(i);
                max = FastMath.max(max, FastMath.abs(delta));
            }
            return max;
        }
    }
*/
    /** {@inheritDoc} */
 /*
    public RealMatrix outerProduct(FlatArrayRealVector v) {
        if (v instanceof FlatArrayRealVector) {
            final double[] vData = ((FlatArrayRealVector) v).data;
            final int m = data.length;
            final int n = vData.length;
            final RealMatrix out = MatrixUtils.createRealMatrix(m, n);
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    out.setEntry(i, j, data[i] * vData[j]);
                }
            }
            return out;
        } else {
            final int m = data.length;
            final int n = v.getDimension();
            final RealMatrix out = MatrixUtils.createRealMatrix(m, n);
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    out.setEntry(i, j, data[i] * v.getEntry(j));
                }
            }
            return out;
        }
    }
*/
    /** {@inheritDoc} */
/*
    public double getEntry(int index) throws OutOfRangeException {
        try {
            return data[index];
        } catch (IndexOutOfBoundsException e) {
            throw new OutOfRangeException(LocalizedFormats.INDEX, index, 0,
                    getDimension() - 1);
        }
    }
*/
    /** {@inheritDoc} */
   
    public int getDimension() {
        return this.columnDimension;
    }

    /** {@inheritDoc} */
 /*
    public FlatArrayRealVector append(FlatArrayRealVector v) {
        try {
            return new FlatArrayRealVector(this, (FlatArrayRealVector) v);
        } catch (ClassCastException cce) {
            return new FlatArrayRealVector(this, v);
        }
    }
*/
    /**
     * Construct a vector by appending a vector to this vector.
     *
     * @param v Vector to append to this one.
     * @return a new vector.
     */

    /*
    public FlatArrayRealVector append(FlatArrayRealVector v) {
        return new FlatArrayRealVector(this, v);
    }
*/
    /** {@inheritDoc} */
/*
    public FlatArrayRealVector append(double in) {
        final double[] out = new double[data.length + 1];
        System.arraycopy(data, 0, out, 0, data.length);
        out[data.length] = in;
        return new FlatArrayRealVector(out, false);
    }
*/
    /** {@inheritDoc} */
/*
    public FlatArrayRealVector getSubVector(int index, int n)
            throws OutOfRangeException, NotPositiveException {
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, n);
        }
        FlatArrayRealVector out = new FlatArrayRealVector(n);
        try {
            System.arraycopy(data, index, out.data, 0, n);
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
            checkIndex(index + n - 1);
        }
        return out;
    }
*/
    /** {@inheritDoc} */
 /*
    public void setEntry(int index, double value) throws OutOfRangeException {
        try {
            data[index] = value;
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
        }
    }
*/
    /** {@inheritDoc} */
   /*
    public void addToEntry(int index, double increment)
            throws OutOfRangeException {
        try {
            data[index] += increment;
        } catch(IndexOutOfBoundsException e){
            throw new OutOfRangeException(LocalizedFormats.INDEX,
                    index, 0, data.length - 1);
        }
    }
*/
    /** {@inheritDoc} */
/*
    public void setSubVector(int index, FlatArrayRealVector v)
            throws OutOfRangeException {
        if (v instanceof FlatArrayRealVector) {
            setSubVector(index, ((FlatArrayRealVector) v).data);
        } else {
            try {
                for (int i = index; i < index + v.getDimension(); ++i) {
                    data[i] = v.getEntry(i - index);
                }
            } catch (IndexOutOfBoundsException e) {
                checkIndex(index);
                checkIndex(index + v.getDimension() - 1);
            }
        }
    }
*/
    /**
     * Set a set of consecutive elements.
     *
     * @param index Index of first element to be set.
     * @param v Vector containing the values to set.
     * @throws OutOfRangeException if the index is inconsistent with the vector
     * size.
     */
    /*
    public void setSubVector(int index, double[] v)
            throws OutOfRangeException {
        try {
            System.arraycopy(v, 0, data, index, v.length);
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
            checkIndex(index + v.length - 1);
        }
    }
*/
    /** {@inheritDoc} */
 /*
    public void set(double value) {
        Arrays.fill(data, value);
    }
*/
    /** {@inheritDoc} */
 /*
    public double[] toArray(){
        return data.clone();
    }
*/
    /** {@inheritDoc} */
 /*
    public String toString(){
        return DEFAULT_FORMAT.format(this);
    }
*/
    /**
     * Check if instance and specified vectors have the same dimension.
     *
     * @param v Vector to compare instance with.
     * @throws DimensionMismatchException if the vectors do not
     * have the same dimension.
     */

    protected void checkVectorDimensions(FlatArrayRealVector v)
            throws DimensionMismatchException {
        checkVectorDimensions(v.data.length);
    }

    /**
     * Check if instance dimension is equal to some expected value.
     *
     * @param n Expected dimension.
     * @throws DimensionMismatchException if the dimension is
     * inconsistent with vector size.
     */
   
    protected void checkVectorDimensions(int n)
            throws DimensionMismatchException {
        if (data.length != n) {
            throw new DimensionMismatchException(data.length, n);
        }
    }

    /**
     * Check if any coordinate of this vector is {@code NaN}.
     *
     * @return {@code true} if any coordinate of this vector is {@code NaN},
     * {@code false} otherwise.
     */
   
    public boolean isNaN() {
        for (double v : data) {
            if (Double.isNaN(v)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether any coordinate of this vector is infinite and none
     * are {@code NaN}.
     *
     * @return {@code true} if any coordinate of this vector is infinite and
     * none are {@code NaN}, {@code false} otherwise.
     */
   
    public boolean isInfinite() {
        if (isNaN()) {
            return false;
        }

        for (double v : data) {
            if (Double.isInfinite(v)) {
                return true;
            }
        }

        return false;
    }

    /** {@inheritDoc} */
  /*
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof FlatArrayRealVector)) {
            return false;
        }

        FlatArrayRealVector rhs = (FlatArrayRealVector) other;
        if (data.length != rhs.getDimension()) {
            return false;
        }

        if (rhs.isNaN()) {
            return this.isNaN();
        }

        for (int i = 0; i < data.length; ++i) {
            if (data[i] != rhs.getEntry(i)) {
                return false;
            }
        }
        return true;
    }
*/
    /**
     * {@inheritDoc} All {@code NaN} values have the same hash code.
     */
 /*
    public int hashCode() {
        if (isNaN()) {
            return 9;
        }
        return MathUtils.hash(data);
    }
*/
    /** {@inheritDoc} */
 /*
    public FlatArrayRealVector combine(double a, double b, FlatArrayRealVector y)
            throws DimensionMismatchException {
        return copy().combineToSelf(a, b, y);
    }
*/
    /** {@inheritDoc} */
 /*
    public FlatArrayRealVector combineToSelf(double a, double b, FlatArrayRealVector y)
            throws DimensionMismatchException {
        if (y instanceof FlatArrayRealVector) {
            final double[] yData = ((FlatArrayRealVector) y).data;
            checkVectorDimensions(yData.length);
            for (int i = 0; i < this.data.length; i++) {
                data[i] = a * data[i] + b * yData[i];
            }
        } else {
            checkVectorDimensions(y);
            for (int i = 0; i < this.data.length; i++) {
                data[i] = a * data[i] + b * y.getEntry(i);
            }
        }
        return this;
    }
*/
    /** {@inheritDoc} */
/*
    public double walkInDefaultOrder(final RealVectorPreservingVisitor visitor) {
        visitor.start(data.length, 0, data.length - 1);
        for (int i = 0; i < data.length; i++) {
            visitor.visit(i, data[i]);
        }
        return visitor.end();
    }
*/
    /** {@inheritDoc} */
   /*
    public double walkInDefaultOrder(final RealVectorPreservingVisitor visitor,
                                     final int start, final int end) throws NumberIsTooSmallException,
            OutOfRangeException {
        checkIndices(start, end);
        visitor.start(data.length, start, end);
        for (int i = start; i <= end; i++) {
            visitor.visit(i, data[i]);
        }
        return visitor.end();
    }
*/
    /**
     * {@inheritDoc}
     *
     * In this implementation, the optimized order is the default order.
     */
  /*
    public double walkInOptimizedOrder(final RealVectorPreservingVisitor visitor) {
        return walkInDefaultOrder(visitor);
    }
 */
    /**
     * {@inheritDoc}
     *
     * In this implementation, the optimized order is the default order.
     */
 /*
    public double walkInOptimizedOrder(final RealVectorPreservingVisitor visitor,
                                       final int start, final int end) throws NumberIsTooSmallException,
            OutOfRangeException {
        return walkInDefaultOrder(visitor, start, end);
    }
*/
    /** {@inheritDoc} */
/*
    public double walkInDefaultOrder(final RealVectorChangingVisitor visitor) {
        visitor.start(data.length, 0, data.length - 1);
        for (int i = 0; i < data.length; i++) {
            data[i] = visitor.visit(i, data[i]);
        }
        return visitor.end();
    }
*/
    /** {@inheritDoc} */
/*
    public double walkInDefaultOrder(final RealVectorChangingVisitor visitor,
                                     final int start, final int end) throws NumberIsTooSmallException,
            OutOfRangeException {
        checkIndices(start, end);
        visitor.start(data.length, start, end);
        for (int i = start; i <= end; i++) {
            data[i] = visitor.visit(i, data[i]);
        }
        return visitor.end();
    }
*/
    /**
     * {@inheritDoc}
     *
     * In this implementation, the optimized order is the default order.
     */
/*
    public double walkInOptimizedOrder(final RealVectorChangingVisitor visitor) {
        return walkInDefaultOrder(visitor);
    }
*/
    /**
     * {@inheritDoc}
     *
     * In this implementation, the optimized order is the default order.
     */
/*
    public double walkInOptimizedOrder(final RealVectorChangingVisitor visitor,
                                       final int start, final int end) throws NumberIsTooSmallException,
            OutOfRangeException {
        return walkInDefaultOrder(visitor, start, end);
    }
    */
}

