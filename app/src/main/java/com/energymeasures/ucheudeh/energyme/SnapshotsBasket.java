package com.energymeasures.ucheudeh.energyme;

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






    private Array2DRowRealMatrix[] matrixElements;
    private ArrayRealVector[] vectorElements;
    private String groupType;

    int [] boundLims = new int []{1,2,7,5,9,10,20,13};// the intergers will form the upperbound

    /**
     * Default Constructor makes 4 matrix 100X 100 and fills them with random number 0-5
     * and 4 RealVectors in ArrayForm  
     */
    public SnapshotsBasket() {
        this(new int[]{5,6,7,8},"dCore");
    }

    /**
     * Default Constructor makes 4 matrix 100X 100 and fills them with random number 0-5
     * and 4 RealVectors in ArrayForm
     * @param ints: in array with the powers of 2 needed to generate Matrices and vectors
     * @param groupType: the experimental categorization according to the power of 2. Namely:
     *
     *  dPreamble: [1,2,3,4], dCore: [5,6,7,8], dBoarder: [9,10], dExtreme: [11,12], dInsane: [13]
     *
     *
     */
    SnapshotsBasket(int[] ints, String groupType){
        this.groupType = groupType;
        this.matrixElements = new Array2DRowRealMatrix[ints.length];
        this.vectorElements = new ArrayRealVector[ints.length];
        
        for (int i = 0; i<ints.length;i++){
            int size = (int)Math.pow(2,ints[i]);
            if(size <=0){size = 2;}


            this.matrixElements[i] = UtilityC.fillMatrix(new Array2DRowRealMatrix(size,size), boundLims[i]);
            this.vectorElements[i] = UtilityC.fillVector(new ArrayRealVector(size * size), boundLims[i]);
        }
            
        
        
    }
    

    Array2DRowRealMatrix getMatrix(int index){
        return this.matrixElements[index];
    }
    
    ArrayRealVector getVector(int index){
        return this.vectorElements[index];
    }

    int getNumMatrices() {
        return this.matrixElements.length;
    }

    int getNumVectors() {return this.vectorElements.length; }

    Array2DRowRealMatrix[] getMatrixElements(){
        return this.matrixElements;
    }

    ArrayRealVector[] getVectorElements(){
        return this.vectorElements;
    }
    
    String getGroupType(){return this.groupType;}



}
