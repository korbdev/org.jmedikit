package org.jmedikit.plugin.gui.tools;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Event;
import org.jmedikit.lib.util.Point2D;
import org.jmedikit.plugin.gui.DicomCanvas;

public abstract class ATool {

	protected DicomCanvas canvas;
	
	protected Point2D<Integer> start;
	protected Point2D<Integer> actual;
	protected Point2D<Integer> end;
	
	protected boolean mouseDown;
	protected boolean mouseUp;
	
	/*public ATool(){
		System.out.println("Tool created");
		
		start = new Point2D<Integer>(0, 0);
		actual = new Point2D<Integer>(0, 0);
		end = new Point2D<Integer>(0, 0);
		
		mouseDown = false;
		mouseUp = true;
	}*/
	
	public ATool(DicomCanvas c){
		start = new Point2D<Integer>(0, 0);
		actual = new Point2D<Integer>(0, 0);
		end = new Point2D<Integer>(0, 0);
		
		mouseDown = false;
		mouseUp = true;
		canvas = c;
	}
	
	public void setCanvas(DicomCanvas c){
		canvas = c;
	}
	
	public void handleMouseMove(Event e){
		actual.setPoint(e.x, e.y);
		actionMouseMove(e);
	}
	
	public void handleMouseDown(Event e){
		mouseDown = true;
		mouseUp = false;
		start.setPoint(e.x, e.y);
		actionMouseDown(e);
	}
	
	public void handleMouseUp(Event e){
		mouseDown = false;
		mouseUp = true;
		end.setPoint(e.x, e.y);
		actionMouseUp(e);
	}
	
	public abstract void actionMouseMove(Event e);
	
	public abstract void actionMouseDown(Event e);

	public abstract void actionMouseUp(Event e);
	
	public abstract GC preCalculation(GC toDraw);
	
	public abstract GC postCalculation(GC toDraw);
}