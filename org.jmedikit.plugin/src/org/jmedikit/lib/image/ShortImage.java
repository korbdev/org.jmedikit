package org.jmedikit.lib.image;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferUShort;


/**
 * ShortImages repräsentieren Grauwertbilder und können Werte zwischen Short.MIN_VALUE 
 * uns Short.MAX_VALUE enthalten.
 * 
 * @author rkorb
 *
 */
public class ShortImage extends AImage{

	/**
	 * Minimal möglicher Pixelwert
	 */
	public static final int MIN_VALUE = Short.MIN_VALUE;
	
	/**
	 * Maximal möglicher Pixelwert
	 */
	public static final int MAX_VALUE = Short.MAX_VALUE;
	
	private short[] pixels;
	
	/**
	 * Der Konstruktor erzeugt ein leeres Short-Bild in der Größe width x height.
	 * 
	 * @param width Bildbreite
	 * @param height Bildhöhe
	 */
	public ShortImage(int width, int height){
		super(width, height);
		imageType = AImage.TYPE_SHORT_SIGNED;
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
	public ShortImage(int width, int height, DataBuffer buffer) {
		this(width, height);	
		
		if(buffer instanceof DataBufferUShort){
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
	public ShortImage(int width, int height, DataBuffer buffer, float m, float b) {
		this(width, height);

		rescaleSlope = m;
		rescaleIntercept = b;
		
		if(buffer instanceof DataBufferUShort){
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
	
	@Override
	public int getMinValue() {
		return MIN_VALUE;
	}

	@Override
	public int getMaxValue() {
		return MAX_VALUE;
	}

}
