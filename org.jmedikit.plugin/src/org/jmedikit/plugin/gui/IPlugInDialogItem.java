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
	 * Gibt den Namen des Elementes zur�ck
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Gibt den Wert des Elements zur�ck
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
	 * Gibt die grafische Repr�sentation zur�ck
	 * 
	 * @param parent Elternelement, Container f�r dieses Element
	 */
	public void getSWTObject(Composite parent);
}
