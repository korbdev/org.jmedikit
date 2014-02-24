package org.jmedikit.plugin.util;

/**
 * 
 * @author rkorb
 *
 */
public interface IObserver {
	
	/**
	 * Aktualisiert den Index des Bildstapels auf den Wert aus dem Parameter index
	 * 
	 * @param index Index des Bildstapels
	 */
	public void update(int index);

	/**
	 * Aktualisiert die Orientierungslinien abhängig eines im Subjekt gewählten normalisierten Punktes  und des Rekonstruktionstyps des Subjekts.
	 * 
	 * @param x normalisierte x-Koordinate
	 * @param y normalisierte y-Koordinate
	 * @param z normalisierte z-Koordinate
	 * @param mprType Typ der Ebenenrekostruktion[AXIAL|CORONAL|SAGITTAL]
	 */
	public void updateScoutingLine(float x, float y, float z, String mprType);
	
	/**
	 * Aktualisiert die Orientierungslinien abhängig von dem Index der Bildschicht des Subjekts(Parameter z) und des Rekonstruktionstyps des Subjekts.
	 * 
	 * @param z z-Koordinate, Index des Bildstapels
	 * @param mprType Typ der Ebenenrekostruktion[AXIAL|CORONAL|SAGITTAL]
	 */
	public void updateScoutingLine(int z, String mprType);
}
