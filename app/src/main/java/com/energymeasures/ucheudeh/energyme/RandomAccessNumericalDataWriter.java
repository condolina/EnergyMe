package com.energymeasures.ucheudeh.energyme;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by ucheudeh on 6/12/17.
 * The class extends the NumericalDataWriter, it is intended for RandomAccessFiles. It attaches a
 * trailer that holds the library of the pointers to the first byte of the Numerical data, and
 * the size of the required buffer to read a single numerical data
 */

public class RandomAccessNumericalDataWriter extends NumericalDataWriter {


    FileChannel fc;
    ArrayList<Long> library;


        public RandomAccessNumericalDataWriter(File path) throws FileNotFoundException {
            this.fc = new RandomAccessFile(path,"rw").getChannel();
            this.library = new ArrayList<Long>();
        }

        @Override
        void write(SnapshotsBasket numData) throws IOException {
            int numMatrix = numData.getAmSize();
            int numVector = numData.getsnapshotSize();

            for (Array2DRowRealMatrix matrix : numData.getAm()) {
                ByteBuffer matrixBuffer = constructMatrix(matrix);
                library.add(fc.position());
                library.add((long)matrixBuffer.capacity());// we can save 4 bytes here ;-)
                fc.write(matrixBuffer);
            }

            for (ArrayRealVector vector : numData.getSnapshot()) {
                ByteBuffer vectorBuffer = constructVector(vector);
                library.add(fc.position());
                library.add((long)vectorBuffer.capacity());// and here. There are few more possible
                fc.write(vectorBuffer);
            }

            long tail = fc.position();

            ByteBuffer libraryTailBuffer = ByteBuffer.allocate(library.size()*LONG_SIZE+LONG_SIZE);
            for(Long element: library){
                libraryTailBuffer.putLong(element.longValue());
            }
            libraryTailBuffer.putLong(tail);

            fc.write(libraryTailBuffer);

            fc.close();



        }

    }

