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
	
	public static final int OPTION_PROCESS_ALL = 1;
	
	private int options;
	
	protected boolean outputChanged = false;
	
	private PrintStream stdout;
	
	private PrintStream output;
	
	protected abstract int options();
	
	public abstract int getPlugInType();
	
	public abstract List<AImage> run(List<AImage> images, int index) throws IllegalAccessException;
	
	public int initialize(){
		stdout = System.out;
		options = options();
		return options;
	}
	
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
