package org.jmedikit.lib.core;

import org.jmedikit.lib.image.AImage;
import org.jmedikit.lib.image.ROI;
import org.jmedikit.lib.util.SimpleImageFactory;

/**
 * Diese Klasse implementiert die Bilineare Interpolation. Es kann ein ganzes Bild oder
 * nur eine Region Of Interest interpoliert werden.
 * 
 * @author rkorb
 *
 */
public class BilinearInterpolation {
	
	private AImage img;
	
	private AImage newImg;
	
	public BilinearInterpolation(AImage src){
		img = src;
	}
	
	/**
	 * 
	 * Interpoliert einen Bildbereich, der durch die Region Of Interest markiert ist. Die Größenangaben entsprechen nicht der Dimension der ROIs, 
	 * sondern der Dimensionen des Quellbildes und die neue Größe des Zielbildes.
	 * 
	 * @param roi Die zu interpolierende Region Of Interest
	 * @param oldWidth Breite des Quellbildes
	 * @param oldHeight Höhe des Quellbildes
	 * @param newWidth Breite des Zielbildes
	 * @param newHeight Höhe des Zielbildes
	 * @return interpoliertes Bild in der Größe der ROI mit den Daten entsprechend der Zielgröße
	 */
	public AImage resampleROI(final ROI roi, int oldWidth, int oldHeight, int newWidth, int newHeight){

		int resultX = (int)(roi.x1 * newWidth+0.5);
		int resultY = (int)(roi.y1 * newHeight+0.5);
		int resultWidth = (int)(roi.x2 * newWidth+0.5);
		int resultHeight = (int)(roi.y2 * newHeight+0.5);
		
		int resampledWidth = resultWidth-resultX;
		int resampledHeight = resultHeight-resultY;
		
		AImage resampled = SimpleImageFactory.getAbstractImage(img.getImageType(), resampledWidth, resampledHeight);
		
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
	
	/**
	 * Die Methode nimmt die Größe des Quellbildes und die Zielgröße entgegen und gibt ein interpoliertes Bild mit der Dimension der Zielgröße zurück.
	 * 
	 * @param oldWidth Breite des Quellbildes
	 * @param oldHeight Höhe des Quellbildes
	 * @param newWidth Breite des Zielbildes
	 * @param newHeight Höhe des Zielbildes
	 * @return interpoliertes Bild
	 */
	public AImage resample(int oldWidth, int oldHeight, int newWidth, int newHeight){
		
		AImage resampled = SimpleImageFactory.getAbstractImage(img.getImageType(), newWidth, newHeight);
		
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
	
	/**
	 * Gibt das interpolierte Bild zurück
	 * @return
	 */
	public AImage getResampled(){
		return newImg;
	}
	
	/**
	 * Nimmt einen Pixel entgegen und interpoliert diesen entsprechend der bilinearen Interpolation.
	 * Für Farbdarstellungen wird jeder Kanal separat interpoliert.
	 * 
	 * @param x x-Koordinate
	 * @param y y-Koordinate
	 * @return
	 */
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
			
			result_r = (E_r + y_difference * (F_r - E_r));
			
			float A_g = (float)( (img.getPixel(index_x, index_y) & 0xff00) >> 8 );
			float B_g = (float)( (img.getPixel(index_x_1, index_y) & 0xff00) >> 8 );
			float C_g = (float)( (img.getPixel(index_x, index_y_1) & 0xff00) >> 8 );
			float D_g = (float)( (img.getPixel(index_x_1, index_y_1) & 0xff00) >> 8 );
			
			//System.out.println(A+", "+B+", "+C+", "+D+", int "+img.getPixel(index_x, index_y)+", to Int "+((int)A));
			
			float E_g = A_g + x_difference * (B_g - A_g);
			float F_g = C_g + x_difference * (D_g - C_g);
			
			result_g = (E_g + y_difference * (F_g - E_g));
			
			float A_b = (float)( (img.getPixel(index_x, index_y) & 0xff) );
			float B_b = (float)( (img.getPixel(index_x_1, index_y) & 0xff) );
			float C_b = (float)( (img.getPixel(index_x, index_y_1) & 0xff) );
			float D_b = (float)( (img.getPixel(index_x_1, index_y_1) & 0xff) );
			
			//System.out.println(A+", "+B+", "+C+", "+D+", int "+img.getPixel(index_x, index_y)+", to Int "+((int)A));
			
			float E_b = A_b + x_difference * (B_b - A_b);
			float F_b = C_b + x_difference * (D_b - C_b);
			
			result_b = (E_b + y_difference * (F_b - E_b));
			
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
			
			result = (E + y_difference * (F - E));
		}
		return result;
	}
}
