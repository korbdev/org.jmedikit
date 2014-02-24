package org.jmedikit.lib.image;


import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;

/**
 * UnsignedShortImages repr�sentieren Grauwertbilder. Die einzelnen Pixel k�nnen Werte zwischen 0 und 2^16-1 = 65535 annehmen.
 * @author rkorb
 */
public class UnsignedShortImage extends AImage{
	
	/**
	 * Minimal m�glicher Pixelwert
	 */
	public static final int MIN_VALUE = 0;
	
	/**
	 * Minimal m�glicher Pixelwert
	 */
	public static final int MAX_VALUE = (int) (Math.pow(2, 16)-1);
	
	/**
	 * Die Pixelwerte des Bildes
	 */
	private int[] pixels;
	
	/**
	 * Der Konstruktor erzeugt ein leeres UnsignedShort - Bild in der Gr��e width x height.
	 * 
	 * @param width Bildbreite
	 * @param height Bildh�he
	 */
	public UnsignedShortImage(int width, int height){
		super(width, height);
		imageType = AImage.TYPE_SHORT_UNSIGNED;
		pixels = new int[width*height];
		//storedValues = new int[width*height];
	}
	
	/**
	 * Dieser Konstruktor wird bei der DICOM-Objekterzeugung einer Datei aufgerufen.
	 * 
	 * @param width Bildbreite
	 * @param height Bildh�he
	 * @param buffer Pixelwerte
	 * @param planarConfiguration Darstellung der RGB-Werte im DICOM-Element PixelData
	 */
	public UnsignedShortImage(int width, int height, DataBuffer buffer){
		this(width, height);	

		if(buffer instanceof DataBufferUShort){
			for(int i = 0; i < buffer.getSize(); i++){
				//storedValues[i] = buffer.getElem(i);
				pixels[i]= buffer.getElem(i);
			}
		}
		else throw new IllegalArgumentException("expected buffer type DataBufferUShort, "+buffer.getClass().getName()+" given");
	}
	
	/**
	 * Der Konstruktor berechnet anhand m und b ger�teunabh�ngige Pixelwerte({@link AImage#rescaleSlope}). Wird bei der Erzeugung eines DICOM-Objekts
	 * aufgerufen
	 * 
	 * @param width Bildbreite
	 * @param height Bildh�he
	 * @param buffer Pixelwerte
	 * @param m Rescale Slope
	 * @param b Rescale Intercept
	 * @param planarConfiguration Darstellung der RGB-Werte im DICOM-Element PixelData
	 */
	public UnsignedShortImage(int width, int height, DataBuffer buffer, float m, float b){
		this(width, height);	
		
		rescaleSlope = m;
		rescaleIntercept = b;
		
		if(buffer instanceof DataBufferUShort){
			for(int i = 0; i < buffer.getSize(); i++){
				int value = buffer.getElem(i);
				//storedValues[i] = value;
				//System.out.println("WITHOUT RS/RI");
				//pixels[i] = value;
				pixels[i] = (int) (rescaleSlope * value + rescaleIntercept);
			}
		}
		else throw new IllegalArgumentException("expected buffer type DataBufferUShort, "+buffer.getClass().getName()+" given");
		
		this.determineMinMaxValues(pixels);
	}
	
	/**
	 * Der Konstruktor berechnet anhand m und b ger�teunabh�ngige Pixelwerte({@link AImage#rescaleSlope}). Wird bei der Erzeugung eines DICOM-Objekts
	 * aufgerufen. Zus�tzlich wird WindowWidth und WindowCenter aufgenommen.
	 * 
	 * @param width Bildbreite
	 * @param height Bildh�he
	 * @param buffer Pixelwerte
	 * @param m Rescale Slope
	 * @param b Rescale Intercept
	 * @param planarConfiguration Darstellung der RGB-Werte im DICOM-Element PixelData
	 */
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
	
	@Override
	public int getMinValue() {
		return MIN_VALUE;
	}

	@Override
	public int getMaxValue() {
		return MAX_VALUE;
	}
}
