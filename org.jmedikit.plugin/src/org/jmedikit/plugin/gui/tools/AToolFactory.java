package org.jmedikit.plugin.gui.tools;

import org.jmedikit.plugin.gui.DicomCanvas;

/**
 * Eine Abstrakte Fakrik, die das Grundger�st der konkreten Fabriken zur Verf�gung stellt
 * 
 * @author rkorb
 *
 */
public abstract class AToolFactory {
	
	/**
	 * 
	 * 
	 * @param toolname Name des Werkzeugtyps
	 * @param c Zeichenfl�che f�r das Werkzeug
	 * @return
	 */
	public ATool createTool(String toolname, DicomCanvas c){
		ATool tool = produce(toolname, c);
		return tool;
	}
	
	/**
	 * Diese Methode definiert die Vorgehensweise zur Objekterzeugung der Werkzeuge anhand des Namens des Werkzeugtyps
	 * 
	 * @param toolname
	 * @param c
	 * @return
	 */
	protected abstract ATool produce(String toolname, DicomCanvas c);
}
