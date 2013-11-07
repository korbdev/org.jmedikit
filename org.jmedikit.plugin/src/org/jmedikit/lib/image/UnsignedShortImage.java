package org.jmedikit.lib.image;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;

public class UnsignedShortImage extends AbstractImage{
	
	private int[] storedValues;
	
	private int[] pixels;
	
	public UnsignedShortImage(int width, int height){
		super(width, height);
		imageType = AbstractImage.TYPE_SHORT_UNSIGNED;
		pixels = new int[width*height];
		storedValues = new int[width*height];
	}
	
	public UnsignedShortImage(int width, int height, DataBuffer buffer){
		this(width, height);	

		if(buffer instanceof DataBufferUShort){
			for(int i = 0; i < buffer.getSize(); i++){
				storedValues[i] = buffer.getElem(i);
			}
		}
		else throw new IllegalArgumentException("expected buffer type DataBufferUShort, "+buffer.getClass().getName()+" given");
	}
	
	public UnsignedShortImage(int width, int height, DataBuffer buffer, float m, float b){
		this(width, height);	
		
		rescaleSlope = m;
		rescaleIntercept = b;
		
		if(buffer instanceof DataBufferUShort){
			for(int i = 0; i < buffer.getSize(); i++){
				int value = buffer.getElem(i);
				storedValues[i] = value;
				pixels[i] = (int) (rescaleSlope * value + rescaleIntercept);
			}
		}
		else throw new IllegalArgumentException("expected buffer type DataBufferUShort, "+buffer.getClass().getName()+" given");
		
		this.determineMinMaxValues(pixels);
	}
	
	public UnsignedShortImage(int width, int height, DataBuffer buffer, float m, float b, float windowCenter, float windowWidth){
		this(width, height, buffer, m, b);	
		this.windowCenter = windowCenter;
		this.windowWidth = windowWidth;
	}
	
	@Override
	public int getPixel(int x, int y) {
		return pixels[y * super.width + x];
	}

	@Override
	public Object getPixels() {
		return pixels;
	}

	@Override
	public void setPixel(int x, int y, int value) {
		int newValue = value;
		pixels[y * width + x] = newValue;
	}
}
