package org.jmedikit.lib.image;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class AbstractImage {
	
	public static final int TYPE_BYTE_SIGNED = 0;
	public static final int TYPE_BYTE_UNSIGNED = 1;
	public static final int TYPE_SHORT_SIGNED = 2;
	public static final int TYPE_SHORT_UNSIGNED = 3;
	
	/**
	 * PixelData is stored as unsigned value
	 */
	public static final int TYPE_UNSIGNED = 0;
	
	/**
	 * PixelData is stored as Two Complement
	 */
	public static final int TYPE_SIGNED = 1;
	
	/**
	 * Image width
	 */
	protected int width;
	
	/**
	 * Image height
	 */
	protected int height;

	/**
	 * Minimum value in the Image
	 */
	protected int min;
	
	/**
	 * Maximum value in the Image
	 */
	protected int max;
	
	/**
	 * is set when extrema are determined via method determinMinMaxValues
	 */
	protected boolean extrema;
	
	/**
	 * device independet values are calculated with the equation
	 * rescaleSlope * storedValue + rescale Intercept
	 */
	protected float rescaleSlope;
	
	/**
	 * description @see {@link AbstractImage#rescaleSlope}
	 */
	protected float rescaleIntercept;
	
	/**
	 * Window width and window center is used to map grey values to a scale of 0 - 255
	 * Medical images usually use grey values between 0 and 4096
	 * 
	 * if window width = 200 and window center = 0
	 * pixels between -100 and 100 are mapped
	 * 
	 * values lower than -100 are mapped as black
	 * values higher than 100 are mapped as white
	 * between the pixels store values from 1 to 254
	 */
	protected float windowWidth;
	
	/**
	 * description @see {@link AbstractImage#windowWidth}
	 */
	protected float windowCenter;
	
	/**
	 * Beschreibt den Typ des Bildes
	 */
	protected int imageType;
	
	/**
	 * Basic Image Constructor
	 * 
	 * @param width
	 * @param height
	 */
	public AbstractImage(int width, int height){
		this.width = width;
		this.height = height;
		extrema = false;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getImageType(){
		return imageType;
	}
	
	/**
	 * 
	 * @return returns the image width 
	 */
	public int getWidth(){
		return width;
	}
	
	/**
	 * 
	 * @return returns the image height
	 */
	public int getHeight(){
		return height;
	}
	
	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public float getRescaleSlope() {
		return rescaleSlope;
	}

	public void setRescaleSlope(float rescaleSlope) {
		this.rescaleSlope = rescaleSlope;
	}

	public float getRescaleIntercept() {
		return rescaleIntercept;
	}

	public void setRescaleIntercept(float rescaleIntercept) {
		this.rescaleIntercept = rescaleIntercept;
	}

	public float getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	public float getWindowCenter() {
		return windowCenter;
	}

	public void setWindowCenter(int windowCenter) {
		this.windowCenter = windowCenter;
	}

	/*public void assignExtrema(boolean extrema){
		this.extrema = extrema;
	}*/
	
	/**
	 * returns true if minimum and maximum are set
	 * 
	 * @return 
	 */
	public boolean extremaAssigned(){
		return extrema;
	}
	/**
	 * 
	 * @param x x Location
	 * @param y	y Location
	 * @return returns a single Pixel value. Gibt int zurueck, cast von niedrige auf h�her unproblematisch
	 */
	public abstract int getPixel(int x, int y);
	
	/**
	 * 
	 * @return returns an array of pixel values. Array type depends on image type
	 */
	public abstract Object getPixels();
	
	/**
	 * Finds and return maximum of given byte array
	 * 
	 * @param pixels pixel array of bytes
	 * @return maximum
	 */
	protected static byte findBytetMaximum(byte[] pixels){
		byte maxValue = Byte.MIN_VALUE;
		
		for(int i = 0; i < pixels.length; i++){
			byte value = pixels[i];
			if(value > maxValue){
				maxValue = value;
			}
		}
		return maxValue;
	}
	
	/**
	 * Finds and return minimum of given byte array
	 * 
	 * @param pixels pixel array of bytes
	 * @return minimum
	 */
	protected static byte findByteMinimum(byte[] pixels){
		byte minValue = Byte.MAX_VALUE;
		
		for(int i = 0; i < pixels.length; i++){
			byte value = pixels[i];
			if(value < minValue){
				minValue = value;
			}
		}
		return minValue;
	}
	
	/**
	 * Finds and return maximum of given short array
	 * 
	 * @param pixels pixel array of shorts
	 * @return maximum
	 */
	protected static short findShortMaximum(short[] pixels){
		short maxValue = Short.MIN_VALUE;
		
		for(int i = 0; i < pixels.length; i++){
			short value = pixels[i];
			if(value > maxValue){
				maxValue = value;
			}
		}
		return maxValue;
	}
	
	/**
	 * Finds and return minimum of given short array
	 * 
	 * @param pixels pixel array of shorts
	 * @return minimum
	 */
	protected static short findShortMinimum(short[] pixels){
		short minValue = Short.MAX_VALUE;
		
		for(int i = 0; i < pixels.length; i++){
			short value = (short)pixels[i];
			if(value < minValue){
				minValue = value;
			}
		}
		return minValue;
	}
	
	/**
	 * Finds and return maximum of given integer array
	 * 
	 * @param pixels pixel array of ints
	 * @return maximum
	 */
	protected static int findIntMaximum(int[] pixels){
		int maxValue = Integer.MIN_VALUE;
		
		for(int i = 0; i < pixels.length; i++){
			int value = pixels[i];
			if(value > maxValue){
				maxValue = value;
			}
		}
		return maxValue;
	}
	
	/**
	 * Finds and return minimum of given integer array
	 * 
	 * @param pixels pixel array of ints
	 * @return minimum
	 */
	protected static int findIntMinimum(int[] pixels){
		int minValue = Integer.MAX_VALUE;
		
		for(int i = 0; i < pixels.length; i++){
			int value = pixels[i];
			if(value < minValue){
				minValue = value;
			}
		}
		return minValue;
	}
	
	/**
	 * Finds and sets the minimum and maximum value of a given pixel array
	 * 
	 * @param pixels array of pixel values. Must be of type byte[], short[] or int[]
	 */
	public void determineMinMaxValues(Object pixels){
		if(pixels instanceof byte[]){
			min = findByteMinimum((byte[]) pixels);
			max = findBytetMaximum((byte[]) pixels);
			extrema = true;
		}
		else if(pixels instanceof short[]){
			min = findShortMinimum((short[]) pixels);
			max = findShortMaximum((short[]) pixels);
			extrema = true;
		}
		else if(pixels instanceof int[]){
			min = findIntMinimum((int[]) pixels);
			max = findIntMaximum((int[]) pixels);
			extrema = true;
		}
		else throw new IllegalArgumentException("PixelType of pixels not supported");
		//System.out.println(pixels.getClass().getName());
		//System.out.println("Minimum: "+min);
		//System.out.println("Maximum: "+max);
		
	}
	
	/**
	 * Converts an unsigned short stored in an integer value to signed short
	 * 
	 * @param toConvert value which needs to be converted
	 * @return
	 */
	public static short unsignedShortToSignedShort(int toConvert){
		return (short)toConvert;
	}
	
	/**
	 * Converts signed short to unsigned short value
	 * 
	 * @param toConvert
	 * @return
	 */
	public static int signedShortToUnsignedShort(short toConvert){
		return toConvert & 0xFFFF;
	}
	
	public static void printPixelValues(String path, AbstractImage img){
		PrintWriter pWriter = null;
		try {
			pWriter = new PrintWriter(new FileWriter(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int y = 0; y < img.height; y++) {
			for (int x = 0; x < img.width; x++) {
				pWriter.print("("+img.getPixel(x, y)+")");
			}
			pWriter.println("");
		}
		pWriter.flush();
	}
}