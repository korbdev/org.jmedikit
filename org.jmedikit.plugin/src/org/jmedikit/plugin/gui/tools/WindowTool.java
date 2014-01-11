package org.jmedikit.plugin.gui.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Event;
import org.jmedikit.plugin.gui.DicomCanvas;

public class WindowTool extends ATool{

	private float wc;
	private float ww;
	
	private int x_move;
	private int y_move;
	
	public WindowTool(DicomCanvas c){
		super(c);
		Cursor cursor = new Cursor(canvas.getDisplay(), SWT.CURSOR_CROSS);
		canvas.setCursor(cursor);
		x_move = 0;
		y_move = 0;
		wc = canvas.windowCenter;
		ww = canvas.windowWidth;
	}
	
	@Override
	public void actionMouseMove(Event e) {
		if(mouseDown){
			x_move = actual.x - start.x;
			y_move = actual.y - start.y;
			
			canvas.windowCenter = wc+x_move;
			canvas.windowWidth = ww+y_move;
		}
	}

	@Override
	public void actionMouseDown(Event e) {
		x_move = 0;
		y_move = 0;
		wc = canvas.windowCenter;
		ww = canvas.windowWidth;
	}

	@Override
	public void actionMouseUp(Event e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GC postCalculation(GC toDraw) {
		return toDraw;
	}

	@Override
	public GC preCalculation(GC toDraw) {
		//canvas.windowCenter = wc+x_move;
		//canvas.windowWidth = ww+y_move;
		
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
		// TODO Auto-generated method stub
		
	}

}
