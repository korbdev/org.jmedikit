package org.jmedikit.lib.util;

/**
 * Mit dieser Klasse k�nnen 2-Dimensionale Strukturen repr�sentiert werden. Zum Beispiel die zwei Dimensionen Breite und H�he.
 * Als generische Klasse k�nnen die Parameter flexibel gew�hlt werden.
 * @author rkorb
 *
 * @param <T>
 */
public class Dimension2D<T> {
	public T width;
	
	public T height;
	
	public Dimension2D(){
		
	}
	
	/**
	 * Erzeugt ein Dimensionsobjekt mit den als Parameter �bergebenen Werten
	 * 
	 * @param width
	 * @param height
	 */
	public Dimension2D(T width, T height){
		this.width = width;
		this.height = height;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((height == null) ? 0 : height.hashCode());
		result = prime * result + ((width == null) ? 0 : width.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		Dimension2D other = (Dimension2D) obj;
		if (height == null) {
			if (other.height != null)
				return false;
		} else if (!height.equals(other.height))
			return false;
		if (width == null) {
			if (other.width != null)
				return false;
		} else if (!width.equals(other.width))
			return false;
		return true;
	}
	
	
}
