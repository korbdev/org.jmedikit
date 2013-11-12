package org.jmedikit.plugin.tools;

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
		x_move = 0;
		y_move = 0;
		System.out.println("WINDOW TOOl KONSTR");
		wc = canvas.windowCenter;
		ww = canvas.windowWidth;
	}
	
	@Override
	public void actionMouseMove(Event e) {
		if(mouseDown){
			x_move = actual.x - start.x;
			y_move = actual.y - start.y;
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
		canvas.windowCenter = wc+x_move;
		canvas.windowWidth = ww+y_move;
		
		return toDraw;
	}

}
