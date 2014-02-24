package org.jmedikit.lib.util;

/**
 * 
 * Diese Klasse stellt einen zweidimensionalen Punkt dar.
 * 
 * @author rkorb
 *
 * @param <T> numerischer Typ
 */
public class Point2D<T> {

	public T x;
	
	public T y;
	
	public Point2D(){
		
	}
	
	/**
	 * 
	 * Erstellt einen Punkt aus den übergebenen Werten
	 * 
	 * @param x_value
	 * @param y_value
	 */
	public Point2D(T x_value, T y_value){
		this.x = x_value;
		this.y = y_value;
	}

	/**
	 * 
	 * Setzt einen Punkt entsprechend der Parameter
	 * 
	 * @param x
	 * @param y
	 */
	public void setPoint(T x, T y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString(){
		return "("+x+", "+y+")";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
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
		@SuppressWarnings("unchecked")
		Point2D<T> other = (Point2D<T>) obj;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}
	
	
}
