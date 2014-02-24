package org.jmedikit.lib.core;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.jmedikit.lib.image.AImage;

public class ImageWindowInterpolation {

	
	/**
	 * Die Methode interpoliert einen Pixelwert zwischen WindowCenter und WindowWidth auf einen Wert zwischen min und max.
	 * Ist der Wert value kleiner als wc-ww/2 bekommt er den Wert min, ist der Wert größer wc+ww/2 bekommt er den Wert max.
	 * 
	 * 
	 * @param value Pixelwert
	 * @param wc WindowCenter
	 * @param ww WindowWidth
	 * @param min minimaler Pixelwert
	 * @param max maximaler Pixelwert
	 * @return
	 */
	public static int interpolatePixel(int value, int wc, int ww, int min, int max){
		int returnValue = 0;
		int c = wc;
		int w = ww;
		
		if(value <= c-0.5-((w-1)/2)){
			returnValue = min;
		}
		else if(value > c-0.5+((w-1)/2)){
			returnValue = max;
		}
		else{
			returnValue = (int) (((value - (c-0.5)) / (w-1)+0.5)*(max-min)+min);
		}
		return returnValue;
	}
	
	/**
	 * Diese Methode interpoliert ein ganzes Bild nach der Vorgehensweise von {@link ImageWindowInterpolation#interpolatePixel(int, int, int, int, int)}
	 * Liegt ein Farbbild vor, wird jeder Kanal separat interpoliert
	 * 
	 * @param img zu interpolierendes Bild
	 * @param wc WindowCenter
	 * @param ww WindowWidth
	 * @param min minimaler Pixelwert
	 * @param max maximaler Pixelwert
	 * @return
	 */
	public static ImageData interpolateImage(AImage img, float wc, float ww, int min, int max){
		
		int width = img.getWidth();
		int height = img.getHeight();
		
		int maxValue = img.getMax();
		int minValue = img.getMin();
		
		int windowCenter = (int) (wc+0.5);
		int windowWidth = (int) (ww+0.5);
		
		if(windowCenter == 0 && windowWidth == 0){
			int range = maxValue-minValue;
			windowCenter = minValue+range/2;
			windowWidth = range/2;
		}

		PaletteData palette = new PaletteData(0xFF , 0xFF00 , 0xFF0000);
		ImageData imgData = new ImageData(width, height, 24, palette);
		
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){

				int value = img.getPixel(x, y);
				int interpolatedValue = interpolatePixel(value, windowCenter, windowWidth, min, max);
				
				int r = interpolatedValue; int g = interpolatedValue; int b = interpolatedValue;
				
				if(img.getSamplesPerPixel() > 1){
					r = interpolatePixel(((value & 0xff0000) >> 16), windowCenter, windowWidth, min, max);
					g = interpolatePixel(((value & 0xff00) >> 8), windowCenter, windowWidth, min, max);
					b = interpolatePixel((value & 0xff), windowCenter, windowWidth, min, max);
				}
				RGB rgb = new RGB(r, g, b);
				imgData.setPixel(x, y, palette.getPixel(rgb));
			}
		}
		
		return imgData;
	}
}
