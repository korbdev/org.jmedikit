package org.jmedikit.lib.image;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;


public class UnsignedByteImage extends AbstractImage{

	//private final short UNSIGNEDBYTEIMAGE_MIN_VALUE = 0;
	//private final short UNSIGNEDBYTEIMAGE_MAX_VALUE = 255;
	
	private short[] storedValues;
	
	private short[] pixels;
	
	public UnsignedByteImage(int width, int height, DataBuffer buffer) {
		super(width, height);
		imageType = AbstractImage.TYPE_BYTE_UNSIGNED;
		if(buffer instanceof DataBufferByte){
			pixels = new short[width*height];
			for(int i = 0; i < buffer.getSize(); i++){
				pixels[i] = (short) buffer.getElem(i);
			}
		}
		else throw new IllegalArgumentException("expected buffer type DataBufferUShort, "+buffer.getClass().getName()+" given");
	}

	public UnsignedByteImage(int width, int height, DataBuffer buffer, float m, float b){
		super(width, height);
		imageType = AbstractImage.TYPE_BYTE_UNSIGNED;
		rescaleSlope = m;
		rescaleIntercept = b;
		if(buffer instanceof DataBufferByte){
			pixels = new short[width*height];
			storedValues = new short[width*height];
			for(int i = 0; i < buffer.getSize(); i++){
				short value = (short) buffer.getElem(i);
				storedValues[i] = value;
				pixels[i] = (short) (rescaleSlope * value + rescaleIntercept);
			}
		}
		else throw new IllegalArgumentException("expected buffer type DataBufferUShort, "+buffer.getClass().getName()+" given");
		
		this.determineMinMaxValues(pixels);
	}
	
	public UnsignedByteImage(int width, int height, DataBuffer buffer, float m, float b, float windowCenter, float windowWidth){
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

}