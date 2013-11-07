package org.jmedikit.lib.util;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Event;

public abstract class ATransformationTool {

	protected DicomPaintingTool paintingTool;
	
	public ATransformationTool(DicomPaintingTool paintingTool){
		this.paintingTool = paintingTool;
	}
	
	abstract void actionMouseMove(Event e);
	
	abstract void actionMouseDown(Event e);

	abstract void actionMouseUp(Event e);
	
	abstract GC postCalculation(GC toDraw);
}
