package org.jmedikit.lib.core;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.jmedikit.lib.image.AbstractImage;

public class ImageWindowInterpolation {

	private static int interpolatePixel(int value, int wc, int ww, int min, int max){
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
	
	public static ImageData interpolateImage(AbstractImage img, float wc, float ww, int min, int max){
		
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
		System.out.println(minValue + " " + maxValue);
		System.out.println("Old Center "+ wc + ", Old Width " + ww + " ImageType = "+img.getImageType());
		System.out.println("New Center "+ windowCenter + ", New Width " + windowWidth + " ImageType = "+img.getImageType());
		
		PaletteData palette = new PaletteData(0xFF , 0xFF00 , 0xFF0000);
		ImageData imgData = new ImageData(width, height, 24, palette);
		
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){

				int value = img.getPixel(x, y);
				int grey = interpolatePixel(value, windowCenter, windowWidth, min, max);

				RGB rgb = new RGB(grey, grey, grey);
				imgData.setPixel(x, y, palette.getPixel(rgb));
			}
		}
		
		return imgData;
	}
}