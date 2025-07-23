package com.kh.spring.util.ai;

import java.util.Random;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import com.google.gson.Gson;
import com.kh.spring.util.common.Regexp;

public class VectorUtil {

    /**
     * Gaussian 랜덤 행렬로 D→d 축소
     * @param data n×D 크기 배열
     * @return n×d 크기 배열
     */
    public static double[][] reduceRandomProj(float[] floatVector){
    	double[][] data = wrapFloatArray(floatVector);
    	int d = Regexp.REDUCE_DIMENSION;
        int D = data[0].length;
        
        Random rnd = new Random(42);
        // Gaussian 랜덤 행렬 R (D×d)
        RealMatrix R = MatrixUtils.createRealMatrix(D, d);
        for (int i = 0; i < D; i++)
            for (int j = 0; j < d; j++)
                R.setEntry(i, j, rnd.nextGaussian() / Math.sqrt(d));
        // 투영: data(n×D) × R(D×d)
        RealMatrix X = MatrixUtils.createRealMatrix(data);
        RealMatrix proj = X.multiply(R);
        return proj.getData();
    }
    
    //float[]을 double[][]로 바꿔요!
    public static double[][] wrapFloatArray(float[] floatVector){
        int D = floatVector.length;
        // 1) float[] → double[]
        double[] asDouble = new double[D];
        for (int i = 0; i < D; i++) {
            asDouble[i] = floatVector[i];
        }
        // 2) double[] → double[1][D]
        return new double[][] { asDouble };
    }
    
    //double[][]을 json으로 바꿔요!
    public static String DoubleToJson(double[][] doubleVector) {
        double[][] vectors = {
            {0.12, 0.34, 0.56},
            {0.78, 0.90, 0.11}
        };

        Gson gson = new Gson();
        String json = gson.toJson(vectors);

        System.out.println(json);
        // 출력: [[0.12,0.34,0.56],[0.78,0.9,0.11]]
        
        return json.toString();//toString금지
    }

}

