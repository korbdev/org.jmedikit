package org.jmedikit.plugin.gui.tools;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.jmedikit.plugin.gui.DicomCanvas;

public class ResizeTool extends ATool{

	float scale;
	
	float textScale;
	
	/*public ResizeTool(){
		super();
		scale = 0f;
	}*/
	
	public ResizeTool(DicomCanvas canvas) {
		super(canvas);
		Cursor cursor = new Cursor(canvas.getDisplay(), SWT.CURSOR_SIZENS);
		canvas.setCursor(cursor);
		scale = 0f;
		textScale = (float)canvas.imageDimension.width / (float)canvas.sourceImage.getWidth();
	}

	@Override
	public void actionMouseMove(Event e) {
		if(mouseDown){
			//textScale += scale/100;
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
		textScale = (float)canvas.imageDimension.width / (float)canvas.sourceImage.getWidth();
		
		String text = (int)(textScale*100)+"%";
		Point textDim = toDraw.textExtent(text);
		toDraw.drawText(text, canvas.canvasDimension.width-textDim.x-20, canvas.canvasDimension.height-textDim.y-20);
		return toDraw;
	}

	@Override
	public GC preCalculation(GC toDraw) {
		return toDraw;
	}

	@Override
	public void actionMouseEnter(Event e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionMouseExit(Event e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionMouseWheel(Event e) {
		if(e.count > 0){
			scale = (float)-0.08;
			
			float scaleNew = 1+scale;
			
			int newWidth = (int) (canvas.imageDimension.width * (scaleNew));
			int newHeight = (int) (canvas.imageDimension.height * (scaleNew));
			
			canvas.imageDimension.width = newWidth;
			canvas.imageDimension.height = newHeight;
		}
		else {
			scale = (float)0.08;
			
			float scaleNew = 1+scale;
			
			int newWidth = (int) (canvas.imageDimension.width * (scaleNew));
			int newHeight = (int) (canvas.imageDimension.height * (scaleNew));
			
			canvas.imageDimension.width = newWidth;
			canvas.imageDimension.height = newHeight;
		}
		
	}

	
}
