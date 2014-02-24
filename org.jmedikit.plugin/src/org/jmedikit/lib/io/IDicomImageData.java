package org.jmedikit.lib.io;

import java.util.ArrayList;

import org.jmedikit.lib.image.AImage;

public interface IDicomImageData {
	
	/**
	 * <p>Gibt die Breite der Bilddaten des gew�hlten Frames zur�ck</p>
	 */
	public int getWidth(int index);
	
	/**
	 * <p>Gibt die H�he der Bilddaten des gew�hlten Frames zur�ck</p>
	 */
	public int getHeight(int index);
	
	/**
	 * <p>Gibt die Anzahl der Frames eines DICOM-Objekts zur�ck</p>
	 */
	public int getDepth();
	
	/**
	 * <p>Gibt den enizigen Pixelwert der Koordinaten x, y und z zur�ck. Die z-Koordinate kann nur gr��er 0 sein, wenn die Bilddaten des DICOM-Objekts mehrere Frames haben.</p>
	 * <p>Vorsicht! Diese Methode ist sehr langsam, da das ganze Bild eingelesen wird</p>
	 * 
	 * @param x x-Koordinate
	 * @param y y-Koordinate
	 * @param z z-Koordinate
	 */
	public int getSimplePixel(int x, int y, int z);
	
	/**
	 * <p>Die Methode liest ein Bild aus dem DICOM-Objekt</p>
	 * <p>Hat ein DICOM-Objekt mehrere Frames, kann ein index > 0 gew�hlt werden.</p>
	 * 
	 * @param index Index des Frames
	 */
	public AImage getImage(int index);
	
	/**
	 * <p>Gibt alle Bilder/Frames eines DICOM-Objekts zur�ck</p>
	 */
	public ArrayList<AImage> getImages();
}
