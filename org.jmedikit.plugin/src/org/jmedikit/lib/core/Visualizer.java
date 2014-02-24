package org.jmedikit.lib.core;

import java.util.ArrayList;

import org.jmedikit.lib.image.AImage;
import org.jmedikit.plugin.gui.DicomCanvas;
import org.jmedikit.plugin.gui.SimpleDicomCanvas;

/**
 * Der Visualizer dient zum visuellen Debugging innerhalb der Plug-ins. Damit können Bilder angezeigt werden, ohne auf dem {@link DicomCanvas} 
 * gezeichnet zu werden. Es ist zu beachten, dass Werte wie WindowWidth und WindowCenter gesetzt werden, um eine korrekte Anzeige zu ermöglichen.
 * Hier können zum Beispiel die Werte des Originalbilds aus den Parametern der Plug-ins mit der Methode {@link AImage#copySignificantAttributes(AImage)}
 * gesetzt werden.
 * 
 * @author rkorb
 *
 */
public class Visualizer {

	/**
	 * Zeigt dem Anwender beim Aufruf  das Bild aus dem Paramter img in einem unabhängigen Fenster in einer Größe der x- und y-Dimensionen des Bildes
	 * 
	 * @param title Titel
	 * @param img 
	 */
	public static void show(String title, AImage img){
		ArrayList<AImage> images = new ArrayList<AImage>();
		images.add(img);
		show(title, images);
	}
	
	/**
	 * Zeigt dem Anwender beim Aufruf die Bilder der Liste images in einem unabhängigen Fenster in einer Größe der x- und y-Dimensionen des Bildes.
	 * Der Anwender kann gewohnt durch die Bildschichten der Liste scrollen.
	 * 
	 * @param title Titel
	 * @param images Liste der Bilder
	 */
	public static void show(String title, ArrayList<AImage> images){
		SimpleDicomCanvas simpleCanvas = new SimpleDicomCanvas(title);
		simpleCanvas.show(images);
	}
}
