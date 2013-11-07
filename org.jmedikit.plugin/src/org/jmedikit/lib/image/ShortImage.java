package org.jmedikit.lib.image;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;

public class ShortImage extends AbstractImage{

	private short[] storedValues;
	
	private short[] pixels;
	
	public ShortImage(int width, int height){
		super(width, height);
		imageType = AbstractImage.TYPE_SHORT_SIGNED;
		pixels = new short[width*height];
		storedValues = new short[width*height];
	}
	
	public ShortImage(int width, int height, DataBuffer buffer) {
		this(width, height);	
		
		if(buffer instanceof DataBufferUShort){
			for(int i = 0; i < buffer.getSize(); i++){
				storedValues[i] = (short) buffer.getElem(i);
			}
		}
		else throw new IllegalArgumentException("expected buffer type DataBufferUShort, "+buffer.getClass().getName()+" given");
	}
	
	public ShortImage(int width, int height, DataBuffer buffer, float m, float b) {
		this(width, height);

		rescaleSlope = m;
		rescaleIntercept = b;
		
		if(buffer instanceof DataBufferUShort){
			for(int i = 0; i < buffer.getSize(); i++){
				short value = (short) buffer.getElem(i);
				storedValues[i] = value;
				pixels[i] = (short) (rescaleSlope * value + rescaleIntercept);
			}
		}
		else throw new IllegalArgumentException("expected buffer type DataBufferUShort, "+buffer.getClass().getName()+" given");
		
		this.determineMinMaxValues(pixels);
	}
	
	public ShortImage(int width, int height, DataBuffer buffer, float m, float b, float windowCenter, float windowWidth) {
		this(width, height, buffer, m, b);
		this.windowCenter = windowCenter;
		this.windowWidth = windowWidth;
	}
	
	@Override
	public int getPixel(int x, int y) {
		return pixels[y * width + x];
	}
	
	@Override
	public Object getPixels() {
		return pixels;
	}

	@Override
	public void setPixel(int x, int y, int value) {
		short newValue = (short)value;
		pixels[y * width + x] = newValue;
	}

}
