package com.energymeasures.ucheudeh.energyme;
import com.energymeasures.ucheudeh.energyme.UtilityC;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;



/**
 * The structure of the numerical data to be read is held by this object of this class.
 * The member variables are real vector objects and matrices objects. The applicable classes (Array2D
 * RowRealMatrix, and ArrayRealVector are basic double and single array of doubles with extra method
 * for normal vector and matrix operation bundled in as well as severe constructors. These objects
 *  will be preserved.
 * Created by ucheudeh on 5/27/17.
 */

class SnapshotsBasket {






    private Array2DRowRealMatrix[] aM = new Array2DRowRealMatrix[4];
    private ArrayRealVector[] snapshot = new ArrayRealVector[4];
    /**
     * Default Constructor makes 4 matrix 100X 100 and fills them with random number 0-5
     * and 4 RealVectors in ArrayForm
     */
    public SnapshotsBasket(){
        this.aM[0] = UtilityC.fillMatrix(new Array2DRowRealMatrix(40,50), 1);
        this.aM[1] = UtilityC.fillMatrix(new Array2DRowRealMatrix(20,15), 3);
        this.aM[2] = UtilityC.fillMatrix(new Array2DRowRealMatrix(15,15), 1);
        this.aM[3] = UtilityC.fillMatrix(new Array2DRowRealMatrix(34,42), 5);
        this.snapshot[0] = UtilityC.fillVector(new ArrayRealVector(512*512), 5);
        this.snapshot[1] = UtilityC.fillVector(new ArrayRealVector(256*256), 3);
        this.snapshot[2] = UtilityC.fillVector(new ArrayRealVector(512*512), 5);
        this.snapshot[3] = UtilityC.fillVector(new ArrayRealVector(128*128), 1);
    }

    public Array2DRowRealMatrix getAm(int index){
        return aM[index];
    }



    public ArrayRealVector getSnapshot(int index){
        return this.snapshot[index];
    }

    public int getAmSize() {
        return this.aM.length;
    }

    public int getsnapshotSize() {
        return this.snapshot.length;
    }

    public Array2DRowRealMatrix[] getAm(){
        return this.aM;
    }

    public ArrayRealVector[] getSnapshot(){
        return this.snapshot;
    }

}
