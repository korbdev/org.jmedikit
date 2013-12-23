package org.jmedikit.lib.core;

import org.jmedikit.lib.image.AbstractImage;
import org.jmedikit.lib.image.ROI;
import org.jmedikit.lib.util.SimpleImageFactory;

public class BilinearInterpolation {
	
	private AbstractImage img;
	
	private AbstractImage newImg;
	
	public BilinearInterpolation(AbstractImage src){
		img = src;
	}
	
	public AbstractImage resampleROI(final ROI roi, int oldWidth, int oldHeight, int newWidth, int newHeight){

		int resultX = (int)(roi.x * newWidth+0.5);
		int resultY = (int)(roi.y * newHeight+0.5);
		int resultWidth = (int)(roi.width * newWidth+0.5);
		int resultHeight = (int)(roi.height * newHeight+0.5);
		
		int resampledWidth = resultWidth-resultX;
		int resampledHeight = resultHeight-resultY;
		
		AbstractImage resampled = SimpleImageFactory.getAbstractImage(img.getImageType(), resampledWidth, resampledHeight);
		
		float x_index;
		float y_index;
		
		for(int y = resultY; y < resultHeight; y++){
			for(int x = resultX; x < resultWidth; x++){
				x_index = ((float)x/(float)newWidth)*(float)oldWidth;
				y_index = ((float)y/(float)newHeight)*(float)oldHeight;

				int value = (int)(getInterpolatedPixel(x_index, y_index)+0.5);
				resampled.setPixel(x-resultX, y-resultY, value);
			}
		}
		
		return resampled;
	}
	
	public AbstractImage resample(int oldWidth, int oldHeight, int newWidth, int newHeight){
		
		AbstractImage resampled = SimpleImageFactory.getAbstractImage(img.getImageType(), newWidth, newHeight);
		
		float x_index;
		float y_index;
		
		for(int y = 0; y < newHeight; y++){
			for(int x = 0; x < newWidth; x++){

				x_index = ((float)x/(float)newWidth)*(float)oldWidth;
				y_index = ((float)y/(float)newHeight)*(float)oldHeight;

				int value = (int)(getInterpolatedPixel(x_index, y_index)+0.5);
				resampled.setPixel(x, y, value);
			}
		}
		newImg = resampled;
		return resampled;
	}
	
	public AbstractImage getResampled(){
		return newImg;
	}
	
	private float getInterpolatedPixel(float x, float y){
		int x1 = (int)x;
		int y1 = (int)y;
		
		float x_difference = x - x1;
		float y_difference = y - y1;
		
		int index_x = x1;
		int index_x_1 = x1+1;
		int index_y = y1;
		int index_y_1 = y1+1;
		
		if(index_x_1 >= img.getWidth()){
			index_x_1 = index_x;
		}
		if(index_y_1 >= img.getHeight()){
			index_y_1 = index_y;
		}
		
		float result = 0f;
		
		if(img.getSamplesPerPixel() > 1){
			float result_r = 0f;
			float result_g = 0f;
			float result_b = 0f;
			
			float A_r = (float)( (img.getPixel(index_x, index_y) & 0xff0000) >> 16 );
			float B_r = (float)( (img.getPixel(index_x_1, index_y) & 0xff0000) >> 16 );
			float C_r = (float)( (img.getPixel(index_x, index_y_1) & 0xff0000) >> 16 );
			float D_r = (float)( (img.getPixel(index_x_1, index_y_1) & 0xff0000) >> 16 );
			
			//System.out.println(A+", "+B+", "+C+", "+D+", int "+img.getPixel(index_x, index_y)+", to Int "+((int)A));
			
			float E_r = A_r + x_difference * (B_r - A_r);
			float F_r = C_r + x_difference * (D_r - C_r);
			
			result_r = (E_r + y_difference * (F_r - E_r))+0.5f;
			
			float A_g = (float)( (img.getPixel(index_x, index_y) & 0xff00) >> 8 );
			float B_g = (float)( (img.getPixel(index_x_1, index_y) & 0xff00) >> 8 );
			float C_g = (float)( (img.getPixel(index_x, index_y_1) & 0xff00) >> 8 );
			float D_g = (float)( (img.getPixel(index_x_1, index_y_1) & 0xff00) >> 8 );
			
			//System.out.println(A+", "+B+", "+C+", "+D+", int "+img.getPixel(index_x, index_y)+", to Int "+((int)A));
			
			float E_g = A_g + x_difference * (B_g - A_g);
			float F_g = C_g + x_difference * (D_g - C_g);
			
			result_g = (E_g + y_difference * (F_g - E_g))+0.5f;
			
			float A_b = (float)( (img.getPixel(index_x, index_y) & 0xff) );
			float B_b = (float)( (img.getPixel(index_x_1, index_y) & 0xff) );
			float C_b = (float)( (img.getPixel(index_x, index_y_1) & 0xff) );
			float D_b = (float)( (img.getPixel(index_x_1, index_y_1) & 0xff) );
			
			//System.out.println(A+", "+B+", "+C+", "+D+", int "+img.getPixel(index_x, index_y)+", to Int "+((int)A));
			
			float E_b = A_b + x_difference * (B_b - A_b);
			float F_b = C_b + x_difference * (D_b - C_b);
			
			result_b = (E_b + y_difference * (F_b - E_b))+0.5f;
			
			result = (int)result_b + ((int)result_g << 8) + ((int)result_r << 16) ;
		}
		else{
			float A = (float)(img.getPixel(index_x, index_y));
			float B = (float)(img.getPixel(index_x_1, index_y));
			float C = (float)(img.getPixel(index_x, index_y_1));
			float D = (float)(img.getPixel(index_x_1, index_y_1));
			
			//System.out.println(A+", "+B+", "+C+", "+D+", int "+img.getPixel(index_x, index_y)+", to Int "+((int)A));
			
			float E = A + x_difference * (B - A);
			float F = C + x_difference * (D - C);
			
			result = (E + y_difference * (F - E))+0.5f;
		}
		return result;
	}
}
