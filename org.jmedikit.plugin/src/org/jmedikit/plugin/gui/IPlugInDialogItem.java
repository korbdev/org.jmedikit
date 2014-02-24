package org.jmedikit.plugin.gui;

import org.eclipse.swt.widgets.Composite;

/**
 * Dieses Interface definiert die Methoden, die alle Formularelemente der Plug-in-Dialoge enthalten.
 * 
 * @author rkorb
 *
 */
public interface IPlugInDialogItem{
	
	/**
	 * Gibt den Namen des Elementes zurück
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Gibt den Wert des Elements zurück
	 * 
	 * @return
	 */
	public Object getValue();
	
	/**
	 * Setzt den Wert des Elements
	 * 
	 * @param o
	 */
	public void setValue(Object o);
	
	/**
	 * Gibt die grafische Repräsentation zurück
	 * 
	 * @param parent Elternelement, Container für dieses Element
	 */
	public void getSWTObject(Composite parent);
}
