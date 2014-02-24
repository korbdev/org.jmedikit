package org.jmedikit.lib.image;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;

/**
 * <p>Ein IntegerImage repräsentiert Farbbilddarstellungen in RGB</p>
 * <p>Es können Werte zwischen Integer.MIN_VALUE und Integer.MAX_VALUE zugewiesen werden.</p>
 * <p>Das Auslesen der einzelnen Kanäle erfolgt folgendermaßen:</p>
 * <ul>
 * <li><code>r   = (value & 0xff0000) >> 16</code></li>
 * <li><code>g = (value & 0xff00) >> 8</code></li>
 * <li><code>b  = value & 0xff</code></li>
 * </ul>
 * <p>Das Setzen der Kanäle erfolgt mittels <code>int value = b + (g << 8) + (r << 16)</code> mit Werte zwischen 0 - 255 für [rgb]
 * 
 * @author rkorb
 *
 */
public class IntegerImage extends AImage{

	/**
	 * Minimal möglicher Pixelwert
	 */
	public static final int MIN_VALUE = Integer.MIN_VALUE;
	
	/**
	 * Maximal möglicher Pixelwert
	 */
	public static final int MAX_VALUE = Integer.MAX_VALUE;
	
	/**
	 * Pixelwerte des Bildes
	 */
	private int[] pixels;
	
	/**
	 * Der Konstruktor erzeugt ein leeres IntegerBild in der Größe width x height.
	 * 
	 * @param width Bildbreite
	 * @param height Bildhöhe
	 */
	public IntegerImage(int width, int height) {
		super(width, height);
		imageType = AImage.TYPE_INT_SIGNED;
		samplesPerPixel = 3;
		pixels = new int[width*height];
	}

	/**
	 * Dieser Konstruktor wird bei der DICOM-Objekterzeugung einer Datei aufgerufen.
	 * 
	 * @param width Bildbreite
	 * @param height Bildhöhe
	 * @param buffer Pixelwerte
	 * @param planarConfiguration Darstellung der RGB-Werte im DICOM-Element PixelData
	 */
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
			//aktuell keine Unterstützung durch dcm4che und einer planarConfiguration = 1
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
	
	/**
	 * Setzt einen Pixel mit den Koordinaten (x, y) mit den Farbwerten (r, g, b)
	 * 
	 * @param x x-Koordinate
	 * @param y y-Koordinate
	 * @param r Roter Kanal zwischen 0 und 255
	 * @param g Grüner Kanal zwischen 0 und 255
	 * @param b Blauer Kanal zwischen 0 und 255
	 */
	public void setPixel(int x, int y, int r, int g, int b){
		int value = b + (g << 8) + (r << 16) ;
		pixels[y * width + x] = value;
	}
	
	@Override
	public Object getPixels() {
		return pixels;
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
