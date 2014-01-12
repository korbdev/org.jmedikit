package org.jmedikit.lib.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jmedikit.lib.image.AImage;

public abstract class APlugIn {

	public static final int OPTION_PROCESS_ALL = 1;
	
	private AImage processingImage;
	
	private int options;
	
	protected abstract int options();
	
	protected boolean outputChanged = false;
	
	private PrintStream stdout;
	
	public int initialize(){
		stdout = System.out;
		options = options();
		return options;
	}
	
	protected abstract AImage process(final AImage img);
	
	protected void setOutput(String filename){
		outputChanged = true;
		FileOutputStream f;
		try {
			File file = new File(filename);
			file.createNewFile();
			f = new FileOutputStream(file.getPath());
			System.setOut(new PrintStream(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public AImage run(AImage img){
		processingImage = img;
		AImage result = process(processingImage);
		
		//Pruefung ob vom Benutzer Bildorientierung gesetzt wurde
		//wenn nicht, wird die Orientierung des Ursprungsbild auf das Ergebnisbild uebertragen
		if(!result.getInitializedOrientation()){
			result.copySignificantAttributes(img);
			result.setColumnImageOrientation(img.getColumnImageOrientation());
			result.setRowImageOrientation(img.getRowImageOrientation());
			result.setImagePosition(img.getImagePosition());
		}
		
		return result;
	}
	
	public int getOptions(){
		return options;
	}

	public boolean isOutputChanged(){
		return outputChanged;
	}
	
	public void cancelFileOutput(){
		outputChanged = false;
	}
	
	public void restoreSystemOut(){
		System.setOut(stdout);
	}
}
