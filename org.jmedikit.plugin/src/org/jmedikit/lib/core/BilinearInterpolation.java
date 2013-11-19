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
		
		float A = img.getPixel(index_x, index_y);
		float B = img.getPixel(index_x_1, index_y);
		float C = img.getPixel(index_x, index_y_1);
		float D = img.getPixel(index_x_1, index_y_1);
		
		float E = A + x_difference * (B - A);
		float F = C + x_difference * (D - C);
		
		return E + y_difference * (F - E);
	}
}