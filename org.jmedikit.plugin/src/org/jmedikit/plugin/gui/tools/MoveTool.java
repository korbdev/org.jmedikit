package org.jmedikit.plugin.gui.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Event;
import org.jmedikit.lib.util.Point2D;
import org.jmedikit.plugin.gui.DicomCanvas;


public class MoveTool extends ATool{

	private Point2D<Integer> translation;
	
	//private ATool resize;
	
	/*public MoveTool(){
		super();
		translation = new Point2D<Integer>(0, 0);
	}*/
	
	public MoveTool(DicomCanvas canvas) {
		super(canvas);
		Cursor cursor = new Cursor(canvas.getDisplay(), SWT.CURSOR_SIZEALL);
		canvas.setCursor(cursor);
		translation = new Point2D<Integer>(0, 0);
		
		//resize = new ResizeTool(canvas);
	}

	@Override
	public void actionMouseMove(Event e) {
		if(mouseDown){
			
			Point2D<Integer> translationOld = new Point2D<>(translation.x, translation.y);
			
			translation.x = actual.x - start.x;
			translation.y = actual.y - start.y;
			
			canvas.imageCenter = new Point2D<Integer>(canvas.imageCenter.x+(translation.x - translationOld.x), canvas.imageCenter.y+(translation.y - translationOld.y));

		}
		//if(mouseDown){
		//	resize.handleMouseMove(e);
		//}
	}

	@Override
	public void actionMouseDown(Event e) {
		translation.x = 0;
		translation.y = 0;
		
		//resize.handleMouseDown(e);
	}

	@Override
	public void actionMouseUp(Event e) {
		//resize.handleMouseUp(e);
	}

	@Override
	public GC postCalculation(GC toDraw) {
		if(mouseDown){
			int rectWidth = (int) (canvas.imageDimension.width);
			int rectHeight = (int) (canvas.imageDimension.height);

			toDraw.drawRectangle(canvas.imageCenter.x-rectWidth/2, canvas.imageCenter.y - rectHeight/2, rectWidth, rectHeight);
			toDraw.setAlpha(50);
			toDraw.fillRectangle(canvas.imageCenter.x-rectWidth/2, canvas.imageCenter.y - rectHeight/2, rectWidth, rectHeight);
		}
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
		int index = canvas.getIndex();
		if(e.count < 0){
			index = index + 1;
		}
		else {
			index = index - 1;
		}
		if(index >= 0 && index < canvas.getImages().size()){
			canvas.getContext().setSliderSelection(index);
		}
		
	}

	

}
