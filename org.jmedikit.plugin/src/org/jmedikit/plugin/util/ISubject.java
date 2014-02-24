package org.jmedikit.plugin.util;

/**
 * 
 * @author rkorb
 *
 */
public interface ISubject {
	/**
	 * Registriert einen neuen Beobachter
	 * @param o
	 */
	public void registerObserver(IObserver o);
	
	/**
	 * Entfernt einen Beobachter aus der Liste
	 * @param o
	 */
	public void removeObserver(IObserver o);
	
	/**
	 * Benachrichtigt alle Beobachter, dass ein Punkt auf der Zeichenfläche ausgewählt wurde.
	 * @param x normalisierte x-Koordinate
	 * @param y normalisierte y-Koordinate
	 * @param z normalisierte z-Koordinate
	 */
	public void notifyObservers(float x, float y, float z);
	
	/**
	 * Benachrichtigt alle Beobachter, dass sich der Index des Bildstapels geändert hat
	 * @param z neuer Index des Bildstapels
	 */
	public void notifyObservers(int z);
}
