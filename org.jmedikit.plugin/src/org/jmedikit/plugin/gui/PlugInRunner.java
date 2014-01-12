package org.jmedikit.plugin.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.jmedikit.lib.core.APlugIn;
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
					AImage result = plugin.run(images.get(i));
					images.remove(i);
					images.add(i, result);
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
				AImage result = plugin.run(images.get(canvas.getIndex()));
				images.remove(canvas.getIndex());
				images.add(canvas.getIndex(), result);
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
