package com.kh.spring.util.ai;

import java.lang.reflect.Type;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kh.spring.util.common.Regexp;

public class VectorUtil {
	
	//랜덤 투영
	public static float[] randomProject(float[] floatVector) {
	    int D = floatVector.length;
	    int d = Regexp.REDUCE_DIMENSION;
	    
	    Random rnd = new Random(42);
	    
	    // 결과 벡터 초기화
	    float[] result = new float[d];
	    
	    // 스파스 랜덤 투영: 0이 많은 행렬을 활용하여 계산량 감소
	    float normalizationFactor = 1.0f / (float) Math.sqrt(d);
	    
	    for (int i = 0; i < D; i++) {
	        float inputValue = floatVector[i];
	        if (inputValue == 0.0f) continue;
	        
	        for (int j = 0; j < d; j++) {
	            float prob = rnd.nextFloat();
	            float projectionValue;
	            
	            if (prob < 0.1667f) {
	                projectionValue = (float) Math.sqrt(3.0) * normalizationFactor;
	            } else if (prob < 0.3333f) {
	                projectionValue = -(float) Math.sqrt(3.0) * normalizationFactor;
	            } else {
	                continue;
	            }
	            
	            result[j] += inputValue * projectionValue;
	        }
	    }
	    
	    return result;
	}
	
    /**
     * JSON 형태의 float[][] 문자열을 float[] 배열로 변환
     * @param 문자열로 둔갑한 JSON
     * @return float[]
     */
    public static float[] parseJsonToFloatArray(String jsonString) {
        Gson gson = new Gson();
        Type type = new TypeToken<float[][]>(){}.getType();
        float[][] matrix = gson.fromJson(jsonString, type);
        
        if (matrix == null || matrix.length == 0) {
            throw new IllegalArgumentException("이거 JSON아닌데?");
        }
        
        return matrix[0]; // 첫 번째 행 반환 (1xN 행렬에서 N차원 벡터 추출)
    }
    
    /**
     * 두 벡터의 코사인 유사도 계산
     * @param vec1 첫 번째 벡터
     * @param vec2 두 번째 벡터
     * @return 코사인 유사도 (-1 ~ 1)
     */
    public static double calculateCosineSimilarity(float[] vec1, float[] vec2) throws Exception{
    	if(vec1.length != vec2.length) throw new Exception("길이가 달라요!");
    	
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            magnitude1 += vec1[i] * vec1[i];
            magnitude2 += vec2[i] * vec2[i];
        }
        
        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);
        
        if (magnitude1 == 0.0 || magnitude2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (magnitude1 * magnitude2);
    }


}

