package org.jmedikit.lib.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jmedikit.lib.image.AImage;

/*public abstract class APlugIn {

	public static final int OPTION_PROCESS_ALL = 1;
	
	//public static final int RUN_2D = 2;
	
	public static final int RUN_3D = 4;
	
	private int currentIndex;
	
	private int options;
	
	protected abstract int options();
	
	protected boolean outputChanged = false;
	
	private PrintStream stdout;
	
	private PrintStream output;
	
	public int initialize(){
		stdout = System.out;
		options = options();
		return options;
	}
	
	protected abstract List<AImage> process(final List<AImage> claims);
	
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
	
	public ArrayList<AImage> run(ArrayList<AImage> images, int currentIndex) throws IllegalAccessException{
		this.currentIndex = currentIndex;

		List<AImage> claims;
		
		if((options & APlugIn.RUN_3D) == APlugIn.RUN_3D){
			claims = Collections.unmodifiableList(images);
		}
		else{
			ArrayList<AImage> temp = new ArrayList<AImage>();
			temp.add(images.get(currentIndex));
			claims = Collections.unmodifiableList(temp);
		}
		

		ArrayList<AImage> result = new ArrayList<AImage>();
		List<AImage> list_result = 	process(claims);

		for(AImage img : list_result){
			result.add(img);
		}
		
		//Dimension pr�fen
		boolean sameDimensions = false;
		for(int i = 0; i < result.size(); i++){
			AImage original = claims.get(i);
			AImage copy = result.get(i);
			
			if(original.getWidth() == copy.getWidth() && original.getHeight() == copy.getHeight()){
				sameDimensions = true;
			}
		}

		if(result.size() != claims.size() ){
			throw new IllegalAccessException("RESULT SIZE DOES NOT MATCH CLAIM SIZE");
		}
		else if(!sameDimensions){
			throw new IllegalAccessException("IMAGE DIMENSION DOES NOT MATCH SOURCE DIMENSION");
		}
		else{
			if((options & APlugIn.RUN_3D) == APlugIn.RUN_3D){
				for(int i = 0; i < images.size(); i++){
					AImage original = images.get(i);
					AImage resultImage = result.get(i);
					copyValues(resultImage, original);
				}
			}
			else{
				AImage original = images.get(currentIndex);
				AImage resultImage = result.get(0);
				copyValues(resultImage, original);
				
			}
		}
		return result;
	}
	
	private AImage copyValues(AImage result, AImage original){
		if(!result.getInitializedOrientation()){
			result.copySignificantAttributes(original);
			result.setColumnImageOrientation(original.getColumnImageOrientation());
			result.setRowImageOrientation(original.getRowImageOrientation());
			result.setImagePosition(original.getImagePosition());
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
	
	public int getCurrentImageIndex(){
		return currentIndex;
	}
}*/
