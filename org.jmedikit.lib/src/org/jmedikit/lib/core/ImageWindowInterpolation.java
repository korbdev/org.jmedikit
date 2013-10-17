package org.jmedikit.lib.core;

import java.awt.Color;
import java.awt.image.BufferedImage;

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
	
	public static BufferedImage interpolateImage(AbstractImage img, float wc, float ww, int min, int max){
		
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
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){

				int value = img.getPixel(x, y);
				int grey = interpolatePixel(value, windowCenter, windowWidth, min, max);

				Color c = new Color(grey, grey, grey);
				bufferedImage.setRGB(x, y, c.getRGB());
			}
		}
		
		return bufferedImage;
	}
}
