package org.jmedikit.lib.image;


import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;

/**
 * Ein UnsignedByteImage repräsentiert ein Grauwert-Bild mit dem minimalen Wert 0 und einem maximalen Wert von 2^8-1 = 255
 * @author rkorb
 */
public class UnsignedByteImage extends AImage{
	
	/**
	 * Minimal möglicher Pixelwert
	 */
	public static final int MIN_VALUE = 0;
	
	/**
	 * Maximal möglicher Pixelwert
	 */
	public static final int MAX_VALUE = (int) (Math.pow(2, 8)-1);
	
	/**
	 * Die Pixelwerte des Bildes
	 */
	private short[] pixels;
	
	/**
	 * Der Konstruktor erzeugt ein leeres UnsignedByte-Bild in der Größe width x height.
	 * 
	 * @param width Bildbreite
	 * @param height Bildhöhe
	 */
	public UnsignedByteImage(int width, int height){
		super(width, height);
		imageType = AImage.TYPE_BYTE_UNSIGNED;
		pixels = new short[width*height];
		//storedValues = new short[width*height];
	}
	
	/**
	 * Dieser Konstruktor wird bei der DICOM-Objekterzeugung einer Datei aufgerufen. 
	 * 
	 * @param width Bildbreite
	 * @param height Bildhöhe
	 * @param buffer Pixelwerte
	 * @param planarConfiguration Darstellung der RGB-Werte im DICOM-Element PixelData
	 */
	public UnsignedByteImage(int width, int height, DataBuffer buffer) {
		this(width, height);

		if(buffer instanceof DataBufferByte){
			for(int i = 0; i < buffer.getSize(); i++){
				//storedValues[i] = (short) buffer.getElem(i);
				pixels[i]= (short) buffer.getElem(i);
			}
		}
		else throw new IllegalArgumentException("expected buffer type DataBufferUShort, "+buffer.getClass().getName()+" given");
	}

	/**
	 * Der Konstruktor berechnet anhand m und b geräteunabhängige Pixelwerte({@link AImage#rescaleSlope}). Wird bei der Erzeugung eines DICOM-Objekts
	 * aufgerufen
	 * 
	 * @param width Bildbreite
	 * @param height Bildhöhe
	 * @param buffer Pixelwerte
	 * @param m Rescale Slope
	 * @param b Rescale Intercept
	 * @param planarConfiguration Darstellung der RGB-Werte im DICOM-Element PixelData
	 */
	public UnsignedByteImage(int width, int height, DataBuffer buffer, float m, float b){
		this(width, height);
		
		rescaleSlope = m;
		rescaleIntercept = b;
		
		if(buffer instanceof DataBufferByte){
			for(int i = 0; i < buffer.getSize(); i++){
				short value = (short) buffer.getElem(i);
				//storedValues[i] = value;
				pixels[i] = (short) (rescaleSlope * value + rescaleIntercept);
			}
		}
		else throw new IllegalArgumentException("expected buffer type DataBufferUShort, "+buffer.getClass().getName()+" given");
		
		this.determineMinMaxValues(pixels);
	}
	
	/**
	 * Der Konstruktor berechnet anhand m und b geräteunabhängige Pixelwerte({@link AImage#rescaleSlope}). Wird bei der Erzeugung eines DICOM-Objekts
	 * aufgerufen. Zusätzlich wird WindowWidth und WindowCenter aufgenommen.
	 * 
	 * @param width Bildbreite
	 * @param height Bildhöhe
	 * @param buffer Pixelwerte
	 * @param m Rescale Slope
	 * @param b Rescale Intercept
	 * @param planarConfiguration Darstellung der RGB-Werte im DICOM-Element PixelData
	 */
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

	@Override
	public void setPixel(int x, int y, int value) {
		short newValue = (short)value;
		pixels[y * width +x] = newValue;
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
