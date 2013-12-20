package org.jmedikit.lib.image;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;

public class IntegerImage extends AbstractImage{

	int[] pixels;
	
	public IntegerImage(int width, int height) {
		super(width, height);
		imageType = AbstractImage.TYPE_INT_SIGNED;
		samplesPerPixel = 3;
		pixels = new int[width*height];
	}

	public IntegerImage(int width, int height, DataBuffer buffer, int planarConfiguration){
		this(width, height);	

		if(buffer instanceof DataBufferByte){
			if(planarConfiguration == 0){
				for(int i = 0; i < buffer.getSize(); i+=3){
					int red = buffer.getElem(i);
					int green = buffer.getElem(i+1);
					int blue = buffer.getElem(i+2);

					int value = blue + (green << 8) + (red << 16);
					pixels[i/3] = value;
				}
			}
			/*else if(planarConfiguration == 1){
				int step = buffer.getSize()/3;
				
				for(int i = 0; i < step; i++){
					int red = buffer.getElem(i);
					int green = buffer.getElem(i+step);
					int blue = buffer.getElem(i+step*2);
					
					int value = blue + (green << 8) + (red << 16);
					pixels[i] = value;
				}
			}*/
			else throw new IllegalArgumentException("PlanarConfiguration unknown, 0 allowed, "+planarConfiguration+" given");
		}
		else throw new IllegalArgumentException("expected buffer type DataBufferUShort, "+buffer.getClass().getName()+" given");
	}
	
	public IntegerImage(int width, int height, DataBuffer buffer, float m, float b, int planarConfiguration){
		this(width, height);	
		
		rescaleSlope = m;
		rescaleIntercept = b;
		
		if(buffer instanceof DataBufferByte){
			if(planarConfiguration == 0){
				for(int i = 0; i < buffer.getSize(); i+=3){
					int red = buffer.getElem(i);
					int green = buffer.getElem(i+1);
					int blue = buffer.getElem(i+2);

					int value = blue + (green << 8) + (red << 16);

					pixels[i/3] = (int) (rescaleSlope * value + rescaleIntercept);
				}
			}
			/*else if(planarConfiguration == 1){
				int step = buffer.getSize()/3;
				System.out.println("buffer "+buffer.getSize()+", "+width+" x "+height);
				for(int i = 0; i < buffer.getSize(); i++){
					
					int red = buffer.getElem(i);
					int green = buffer.getElem(i);
					int blue = buffer.getElem(i);
					if(red  != 0){
						System.out.println(i+", "+" red "+red);
					}
					int value = blue + (green << 8) + (red << 16);
					//int value = red;
					pixels[i] = green;
				}
			}*/
			else throw new IllegalArgumentException("PlanarConfiguration unknown, 0 allowed, "+planarConfiguration+" given");
		}
		else throw new IllegalArgumentException("expected buffer type DataBufferByte, "+buffer.getClass().getName()+" given");
		
		this.determineMinMaxValues(pixels);
	}
	
	public IntegerImage(int width, int height, DataBuffer buffer, float m, float b, int planarConfiguration, float windowCenter, float windowWidth){
		this(width, height, buffer, m, b, planarConfiguration);	
		this.windowCenter = windowCenter;
		this.windowWidth = windowWidth;
	}
	
	@Override
	public int getPixel(int x, int y) {
		return pixels[y * width +x];
	}

	@Override
	public void setPixel(int x, int y, int value) {
		pixels[y * width + x] = value;
	}
	
	public void setPixel(int x, int y, int r, int g, int b){
		int value = b + (g << 8) + (r << 16) ;
		pixels[y * width + x] = value;
	}
	
	@Override
	public Object getPixels() {
		return pixels;
	}

}
