package org.jmedikit.plugin.gui.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Event;
import org.jmedikit.lib.util.Vector3D;
import org.jmedikit.plugin.gui.DicomCanvas;

public class DefaultTool extends ATool{

	ATool move;
	ATool resize;
	
	public DefaultTool(DicomCanvas c) {
		super(c);
		move = new MoveTool(c);
		resize = new ResizeTool(c);
		
		Cursor cursor = new Cursor(canvas.getDisplay(), SWT.CURSOR_ARROW);
		canvas.setCursor(cursor);
	}

	@Override
	public void actionMouseMove(Event e) {
		move.handleMouseMove(e);
	}

	@Override
	public void actionMouseDown(Event e) {
		if(e.button == 1){
			move.handleMouseDown(e);
		}
	}

	@Override
	public void actionMouseUp(Event e) {
		if(e.button == 3){
			int x = canvas.imageCenter.x-canvas.imageDimension.width/2;
			int y = canvas.imageCenter.y-canvas.imageDimension.height/2;
			int width = x+canvas.imageDimension.width;
			int height = y+canvas.imageDimension.height;

			Vector3D<Float> coordinates = new Vector3D<Float>(0f, 0f, 0f, 1f);
			
			if( (e.x >= x && e.x < width) && (e.y >= y && e.y < height)){
				coordinates.x = (((float)(e.x-x)/(float)canvas.imageDimension.width));
				coordinates.y = (((float)(e.y-y)/(float)canvas.imageDimension.height));
			}
			coordinates.z = (float) canvas.getIndex()/(float)canvas.getImages().size();
			
			//System.out.println(event.button);
			
			canvas.getContext().notifyObservers(coordinates.x, coordinates.y, coordinates.z);
		}
		else if(e.button == 1){
			move.handleMouseUp(e);
		}
		Cursor cursor = new Cursor(canvas.getDisplay(), SWT.CURSOR_ARROW);
		canvas.setCursor(cursor);
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
		resize.handleMouseWheel(e);
	}

	@Override
	public GC preCalculation(GC toDraw) {
		// TODO Auto-generated method stub
		return toDraw;
	}

	@Override
	public GC postCalculation(GC toDraw) {
		// TODO Auto-generated method stub
		return move.postCalculation(toDraw);
	}

}
