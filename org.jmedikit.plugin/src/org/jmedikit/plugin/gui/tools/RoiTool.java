package org.jmedikit.plugin.gui.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Event;
import org.jmedikit.lib.image.ROI;
import org.jmedikit.lib.util.Point2D;
import org.jmedikit.plugin.gui.DicomCanvas;

public class RoiTool extends ATool{

	int xS;
	int yS;
	int xE;
	int yE;
	
	private Point2D<Integer> startNorm;
	private Point2D<Integer> endNorm;
	
	public RoiTool(DicomCanvas c) {
		super(c);
		
		Cursor cursor = new Cursor(canvas.getDisplay(), SWT.CURSOR_ARROW);
		canvas.setCursor(cursor);
		
		startNorm = new Point2D<Integer>(0, 0);
		endNorm = new Point2D<Integer>(0, 0);
	}

	@Override
	public void actionMouseMove(Event e) {
		if(mouseDown){
			xS = start.x;
			yS = start.y;
			xE = actual.x-start.x;
			yE = actual.y-start.y;
		}
	}

	@Override
	public void actionMouseDown(Event e) {
		xS = 0;
		yS = 0;
		xE = 0;
		yE = 0;
	}

	@Override
	public void actionMouseUp(Event e) {
		int x = canvas.imageCenter.x-canvas.imageDimension.width/2;
		int y = canvas.imageCenter.y-canvas.imageDimension.height/2;
		int width = x+canvas.imageDimension.width;
		int height = y+canvas.imageDimension.height;
		
		//Pr�fen, ob Start und Ende im Bild liegen
		if( 
				((start.x >= x && start.x < width) && (start.y >= y && start.y < height)) &&
				((end.x >= x && end.x < width) && (end.y >= y && end.y < height))
		){
			//Berechnung der Koordinaten, es werden x und y Werte des Originalbilds angezeit
			//dadurch Kovertierung von skalierten Koordinaten zu Originalkoordinaten 
			startNorm.x = (int)((((float)(start.x-x)/(float)canvas.imageDimension.width)*(float)canvas.sourceImage.getWidth())+0.5);
			startNorm.y = (int)((((float)(start.y-y)/(float)canvas.imageDimension.height)*(float)canvas.sourceImage.getHeight())+0.5);
			
			endNorm.x = (int)((((float)(end.x-x)/(float)canvas.imageDimension.width)*(float)canvas.sourceImage.getWidth())+0.5);
			endNorm.y = (int)((((float)(end.y-y)/(float)canvas.imageDimension.height)*(float)canvas.sourceImage.getHeight())+0.5);
			
			//Normalisieren
			float xStart = (float)startNorm.x/(float)canvas.sourceImage.getWidth();
			float yStart = (float)startNorm.y/(float)canvas.sourceImage.getHeight();
			
			float xEnd = (float)endNorm.x/(float)canvas.sourceImage.getWidth();
			float yEnd = (float)endNorm.y/(float)canvas.sourceImage.getHeight();
			System.out.println(startNorm.x + " "+ startNorm.y + " " + endNorm.x+ " "+ endNorm.y);
			canvas.sourceImage.addROI(new ROI(xStart, yStart, xEnd, yEnd));
		}
		else System.out.println("NOI");
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

	@Override
	public GC preCalculation(GC toDraw) {
		// TODO Auto-generated method stub
		return toDraw;
	}

	@Override
	public GC postCalculation(GC toDraw) {
		if(mouseDown){
			toDraw.drawRectangle(xS, yS, xE, yE);
		}
		return toDraw;
	}

}
