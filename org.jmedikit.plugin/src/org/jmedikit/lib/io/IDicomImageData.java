package org.jmedikit.lib.io;

import java.util.ArrayList;

import org.jmedikit.lib.image.AImage;

public interface IDicomImageData {
	
	/**
	 * <p>Gibt die Breite der Bilddaten des gewählten Frames zurück</p>
	 */
	public int getWidth(int index);
	
	/**
	 * <p>Gibt die Höhe der Bilddaten des gewählten Frames zurück</p>
	 */
	public int getHeight(int index);
	
	/**
	 * <p>Gibt die Anzahl der Frames eines DICOM-Objekts zurück</p>
	 */
	public int getDepth();
	
	/**
	 * <p>Gibt den enizigen Pixelwert der Koordinaten x, y und z zurück. Die z-Koordinate kann nur größer 0 sein, wenn die Bilddaten des DICOM-Objekts mehrere Frames haben.</p>
	 * <p>Vorsicht! Diese Methode ist sehr langsam, da das ganze Bild eingelesen wird</p>
	 * 
	 * @param x x-Koordinate
	 * @param y y-Koordinate
	 * @param z z-Koordinate
	 */
	public int getSimplePixel(int x, int y, int z);
	
	/**
	 * <p>Die Methode liest ein Bild aus dem DICOM-Objekt</p>
	 * <p>Hat ein DICOM-Objekt mehrere Frames, kann ein index > 0 gewählt werden.</p>
	 * 
	 * @param index Index des Frames
	 */
	public AImage getImage(int index);
	
	/**
	 * <p>Gibt alle Bilder/Frames eines DICOM-Objekts zurück</p>
	 */
	public ArrayList<AImage> getImages();
}
