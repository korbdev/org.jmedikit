package org.jmedikit.plugin.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.jmedikit.lib.core.APlugIn;
import org.jmedikit.lib.core.APlugIn3D;
import org.jmedikit.lib.core.Visualizer;
import org.jmedikit.lib.image.AImage;

public class PlugInRunner implements IRunnableWithProgress{

	private APlugIn plugin;
	
	private String name;
	
	private ArrayList<AImage> images;
	
	private DicomCanvas canvas;
	
	PlugInRunner(String name, DicomCanvas canvas, APlugIn plugin, ArrayList<AImage> images){
		this.name = name;
		this.canvas = canvas;
		this.plugin = plugin;
		this.images = images;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		
		String initializeError = "Error in options()\n";
		
		try {
			plugin.initialize();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			initializeError  += sw.toString();
			canvas.getContext().postErrorEvent(initializeError+"\n");
		}
		
		Integer options = plugin.getOptions();

		int iterations = (options & APlugIn.OPTION_PROCESS_ALL) == APlugIn.OPTION_PROCESS_ALL ? images.size() : 1;
		monitor.beginTask(name, iterations);
		
		if((options & APlugIn.OPTION_PROCESS_ALL) == APlugIn.OPTION_PROCESS_ALL){
			for(int i = 0; i < images.size(); i++){
				String processError= "Error in process() at image index " + i + " \n";
				try {
					List<AImage> result = plugin.run(images, i);
					//if((options & APlugIn.RUN_3D) == APlugIn.RUN_3D){
					//	images.clear();
					//	images.addAll(result);
					//}
					if(plugin.getPlugInType() == APlugIn.PLUGIN_3D){
						if(images.equals(result)){
							images = (ArrayList<AImage>) result;
						}
						else{
							images.clear();
							images.addAll(result);
						}
					}
					else if(plugin.getPlugInType() == APlugIn.PLUGIN_2D){
						AImage img = result.get(0);
						images.remove(i);
						images.add(i, img);
					}
					//AImage result = plugin.run(images, i);
					//images.remove(i);
					//images.add(i, result);
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					processError  += sw.toString();
					canvas.getContext().postErrorEvent(processError+"\n");
				}
				monitor.worked(1);
				monitor.subTask((i+1)+"/ "+iterations);
			}
		}
		else{
			String processError= "Error in process() at image index " + canvas.getIndex() + " \n";
			try {
				List<AImage> result = plugin.run(images, canvas.getIndex());
				//if((options & APlugIn.RUN_3D) == APlugIn.RUN_3D){
				//	System.out.println("CHANGE IMG");
				//	images.clear();
				//	images.addAll(result);
				//}
				//System.out.println(plugin.getClass().getSuperclass().getName()+" / "+APlugIn3D.class.getName());
				if(plugin.getPlugInType() == APlugIn.PLUGIN_3D){
					if(images.equals(result)){
						images = (ArrayList<AImage>) result;
					}
					else{
						images.clear();
						images.addAll(result);
					}
				}
				else if(plugin.getPlugInType() == APlugIn.PLUGIN_2D){
					AImage img = result.get(0);
					images.remove(canvas.getIndex());
					images.add(canvas.getIndex(), img);
				}
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				processError  += sw.toString();
				canvas.getContext().postErrorEvent(processError+"\n");
			}
			monitor.worked(1);
		}
		
		plugin.restoreSystemOut();
		monitor.done();
	}

}
