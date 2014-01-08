package org.jmedikit.lib.core;

import java.util.ArrayList;

import org.jmedikit.lib.image.AImage;
import org.jmedikit.plugin.gui.ImageViewComposite;
import org.jmedikit.plugin.gui.ImageViewPart;

public abstract class APlugIn {

	public static final int OPTION_PROCESS_ALL = 1;
	
	private AImage processingImage;
	
	private int options;
	
	private ArrayList<AImage> images;
	
	protected abstract int options();
	
	public int initialize(){
		options = options();
		return options;
	}
	
	protected abstract AImage process(final AImage img);
	
	public AImage run(AImage img){
		processingImage = img;
		AImage result = process(processingImage);

		//System.out.println(result.getInitializedOrientation()+", "+img.getInitializedOrientation());
		
		//Pruefung ob vom Benutzer Bildorientierung gesetzt wurde
		//wenn nicht, wird die Orientierung des Ursprungsbild auf das Ergebnisbild uebertragen
		if(!result.getInitializedOrientation()){
			//System.out.println("COPY");
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

}