package org.jmedikit.plugin.tools;


import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Event;
import org.jmedikit.plugin.gui.DicomCanvas;

public class ResizeTool extends ATool{

	float scale;
	
	/*public ResizeTool(){
		super();
		scale = 0f;
	}*/
	
	public ResizeTool(DicomCanvas canvas) {
		super(canvas);
		scale = 0f;
	}

	@Override
	public void actionMouseMove(Event e) {
		if(mouseDown){
			float scaleOld = scale;
			scale = (float)(actual.y - start.y)/100;
			
			float scaleNew = 1+scale-scaleOld;
			
			int newWidth = (int) (canvas.imageDimension.width * (scaleNew));
			int newHeight = (int) (canvas.imageDimension.height * (scaleNew));
			
			canvas.imageDimension.width = newWidth;
			canvas.imageDimension.height = newHeight;
		}
		
	}

	@Override
	public void actionMouseDown(Event e) {
		scale = 0f;
	}

	@Override
	public void actionMouseUp(Event e) {

	}

	@Override
	public GC postCalculation(GC toDraw) {
		return toDraw;
	}

	@Override
	public GC preCalculation(GC toDraw) {
		return toDraw;
	}

	
}
