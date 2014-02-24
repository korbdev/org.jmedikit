package org.jmedikit.lib.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.jmedikit.lib.image.AImage;

public abstract class APlugIn {

	public static final int PLUGIN_2D = 0;
	
	public static final int PLUGIN_3D = 1;
	
	/**
	 * Diese Konstante kann als Option der Methode {@link APlugIn#options()} zur�ckgegeben werden.
	 * Dadurch wird das Plug-in entsprechend der z-Koordinate ausgef�hrt. Das erm�glicht, dass zum Beispiel
	 * konkrete {@link APlugIn2D}s auf den ganzen Stapel angewendet werden und nicht separat als 3D-Plug-in entwickelt werden m�ssen.
	 */
	public static final int OPTION_PROCESS_ALL = 1;
	
	private int options;
	
	protected boolean outputChanged = false;
	
	/**
	 * Kopie der Standardausgabe
	 */
	private PrintStream stdout;
	
	/**
	 * Enth�lt die vom Anwender gew�hlte Ausgabe
	 */
	private PrintStream output;
	
	/**
	 * Diese Methode wird einmalig zum Initialisieren vor dem erten Aufruf des Plug-ins aufgerufen.
	 * 
	 * @return
	 */
	protected abstract int options();
	
	/**
	 * Gibt den Plug-in Typ zur�ck
	 * 
	 * @return
	 */
	public abstract int getPlugInType();
	
	
	/**
	 * Repr�sentiert die Schablonenmethode und muss von den Plug-in-Typen wie {@link APlugIn2D} und {@link APlugIn3D} implementiert werden
	 */
	public abstract List<AImage> run(List<AImage> images, int index) throws IllegalAccessException;
	
	public int initialize(){
		stdout = System.out;
		options = options();
		return options;
	}
	
	/**
	 * Diese Methode stellt die Standardausgabe von Java auf die im Parameter angegebene Datei. Existiert die Datei nicht, wird diese angelegt.
	 * <p>Wichtig! Die Anwendung braucht Schreibrechte auf die Datei.</p>
	 * Nach dem Plug-in-Durchlauf wird die Standardausgabe wieder hergestellt. setOutput dient vor allem zum Debuggen der Plug-ins, um Ausgaben f�r den Anwender zu erm�glichen.
	 * @param filename
	 */
	protected void setOutput(String filename){
		outputChanged = true;
		FileOutputStream f;
		try {
			File file = new File(filename);
			file.createNewFile();
			f = new FileOutputStream(file.getPath());
			output = new PrintStream(f);
			System.setOut(output);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gibt den gesetzten Optionswert zur�ck
	 * @return
	 */
	public int getOptions(){
		return options;
	}

	/**
	 * Pr�ft, ob die Standardausgabe in eine Dateiausgabe ge�ndert wurde
	 * 
	 * @return true wenn Dateiausgabe gew�hlt wurde
	 */
	public boolean isOutputChanged(){
		return outputChanged;
	}
	
	//public void cancelFileOutput(){
	//	outputChanged = false;
	//}
	
	/**
	 * Setzt die Ausgabe wieder auf die Standardausgabe von Java zur�ck
	 */
	public void restoreSystemOut(){
		outputChanged = false;
		System.setOut(stdout);
	}
	
	/**
	 * Diese Methode kopiert signifikatne Attribute eines Originalbilds in die Kopie. 
	 * Zu den signifikanten Werten geh�ren zum Beispiel ImagePosition, ImageOrientation 
	 * sowie die signifikanten Werte aus der Methode {@link AImage#copySignificantAttributes(AImage)}.
	 * @param result Kopie
	 * @param original Originalbild
	 * @return Kopie mit den signifikanten Werten des Originalbildes
	 */
	protected AImage copyValues(AImage result, AImage original){
		if(!result.getInitializedOrientation()){
			result.copySignificantAttributes(original);
			result.setColumnImageOrientation(original.getColumnImageOrientation());
			result.setRowImageOrientation(original.getRowImageOrientation());
			result.setImagePosition(original.getImagePosition());
		}
		return result;
	}
}
