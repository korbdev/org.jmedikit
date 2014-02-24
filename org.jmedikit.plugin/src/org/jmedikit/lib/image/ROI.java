package org.jmedikit.lib.image;

/**
 * <p>Ein ROI markiert eine <b>Region Of Interest</b></p>
 * <p>Die Koordinaten (x1, y1) repräsentieren Eckpunkt und (x2, y2) den diagolan gegenüberliegenden Eckpunkt.</p>
 * <p>Die Werte werden als müssen alsnormalisierte Koordinaten zwischen 0 und 1 gespeichert werden. Eine Normalisierung wird berechnet mit:</p>
 * 
 * <p><code>x_norm = x_index/width</code> und</p>
 * <p><code>y_norm = y_index/height</code></p>
 * 
 * <p>Die Berechnug der Indizes erfolgt über</p>
 * <p><code>x_index = x_norm*width</code> und</p>
 * <p><code>y_index = y_norm*height</code></p>
 * @author rkorb
 *
 */
public class ROI {
	
	/**
	 * Repräsentiert die x-Koordinate des Startpunkts
	 */
	public float x1;
	/**
	 * Repräsentiert die y-Koordinate des Startpunkts
	 */
	public float y1;
	
	/**
	 * Repräsentiert die x-Koordinate des Endpunkts. Liegt diagonal gegenüber vom Startpunkt.
	 */
	public float x2;
	
	/**
	 * Repräsentiert die y-Koordinate des Endpunkts. Liegt diagonal gegenüber vom Startpunkt.
	 */
	public float y2;
	
	/**
	 * Erzeugt eine leere Region Of Interest
	 */
	public ROI(){
		x1 = 0f;
		y1 = 0f;
		x2 = 0f;
		y2 = 0f;
	}
	
	/**
	 * Erzeugt eine Region Of Interest anhand der Parameter (x1,y1) und (x2, y2)
	 * 
	 * @param x1 
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public ROI(float x1, float y1, float x2, float y2){
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	@Override
	public String toString(){
		return "("+x1+", "+y1+")("+x2+", "+y2+")";
	}
}
