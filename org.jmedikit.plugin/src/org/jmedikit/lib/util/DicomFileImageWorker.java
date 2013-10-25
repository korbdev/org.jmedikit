package org.jmedikit.lib.util;

import javax.swing.SwingWorker;

import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.lib.image.AbstractImage;

public class DicomFileImageWorker extends SwingWorker<AbstractImage, Integer>{

	private DicomObject obj;
	
	public DicomFileImageWorker(DicomObject obj){
		this.obj = obj;
	}
	
	@Override
	protected AbstractImage doInBackground() throws Exception {
		System.out.println("WORKER STARTED");
		return obj.getImage(0);
	}

}
