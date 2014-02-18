package org.jmedikit.plugin.gui.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.jmedikit.lib.core.ImageWindowInterpolation;
import org.jmedikit.lib.util.Point2D;
import org.jmedikit.plugin.gui.DicomCanvas;

public class PointTool extends ATool{

	private int imageValue;
	
	private int interpolatedValue;
	
	private Point2D<Integer> coordinates;
	
	private boolean mouseEntered;
	
	private boolean mouseExited;
	
	private boolean isInImage;
	
	public PointTool(DicomCanvas c) {
		super(c);
		Cursor cursor = new Cursor(canvas.getDisplay(), SWT.CURSOR_ARROW);
		canvas.setCursor(cursor);
		coordinates = new Point2D<Integer>(0, 0);
		mouseEntered = false;
		mouseExited = true;
		isInImage = false;
	}

	@Override
	public void actionMouseMove(Event e) {
		int x = canvas.imageCenter.x-canvas.imageDimension.width/2;
		int y = canvas.imageCenter.y-canvas.imageDimension.height/2;
		int width = x+canvas.imageDimension.width;
		int height = y+canvas.imageDimension.height;

		//Prüfen, ob Cursor im Bild liegt
		if( (e.x >= x && e.x < width) && (e.y >= y && e.y < height)){
			isInImage = true;
			
			//Berechnung der Koordinaten, es werden x und y Werte des Originalbilds angezeit
			//dadurch Kovertierung von skalierten Koordinaten zu Originalkoordinaten 
			coordinates.x = (int)((((float)(e.x-x)/(float)canvas.imageDimension.width)*(float)canvas.sourceImage.getWidth())+0.5);
			coordinates.y = (int)((((float)(e.y-y)/(float)canvas.imageDimension.height)*(float)canvas.sourceImage.getHeight())+0.5);
			
			imageValue = canvas.sourceImage.getPixel(coordinates.x, coordinates.y);
			interpolatedValue = ImageWindowInterpolation.interpolatePixel(imageValue, (int)canvas.windowCenter, (int)canvas.windowWidth, 0, 255);
		}
		else {
			isInImage = false;
			imageValue = 0;
			interpolatedValue = 0;
			coordinates.x = 0;
			coordinates.y = 0;
		}
	}

	@Override
	public void actionMouseDown(Event e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionMouseUp(Event e) {
		if(isInImage){
			
			//der gewählte Punkt wird in normalisierten Koordinaten gespeichert
			float x = (float)coordinates.x/(float)canvas.sourceImage.getWidth();
			float y = (float)coordinates.y/(float)canvas.sourceImage.getHeight();
			Point2D<Float> p = new Point2D<Float>(x, y);
			canvas.sourceImage.addPoint(p);
			//canvas.sourceImage.setTitle("POINTTHING");
			System.out.println(x+" x "+y+", "+canvas.sourceImage.getWidth()+" x "+canvas.sourceImage.getHeight()+", "+canvas.sourceImage.getTitle());
		}
	}

	@Override
	public GC preCalculation(GC toDraw) {
		// TODO Auto-generated method stub
		return toDraw;
	}

	@Override
	public GC postCalculation(GC toDraw) {
		
		Color background = toDraw.getBackground();
		Color foreground = toDraw.getForeground();
		Font backupFont = toDraw.getFont();
		if(mouseEntered){
			toDraw.setBackground(canvas.white);
			toDraw.setForeground(canvas.black);
			
			Font font = new Font(Display.getCurrent(), "Verdana", 8, SWT.NONE); 
			toDraw.setFont(font);
			
			String output = coordinates.x+" x "+ coordinates.y + ", Pixel Value: "+imageValue+", interpolated: "+interpolatedValue;
			Point  p = toDraw.textExtent(output);
			
			int xPos = p.x;
			int yPos = p.y;
			
			if(actual.x+p.x > canvas.canvasDimension.width && actual.y+p.y+30 > canvas.canvasDimension.height){
				//toDraw.fillRectangle(actual.x, actual.y, -xPos, -yPos);
				toDraw.drawText(output, actual.x-xPos, actual.y-yPos);
			}
			else if(actual.x+p.x > canvas.canvasDimension.width && actual.y+p.y+30 < canvas.canvasDimension.height){
				//toDraw.fillRectangle(actual.x, actual.y+30, -xPos, yPos);
				toDraw.drawText(output, actual.x-xPos, actual.y+30);
			}
				
			else if(actual.x+p.x < canvas.canvasDimension.width && actual.y+p.y+30 > canvas.canvasDimension.height){
				//toDraw.fillRectangle(actual.x, actual.y, xPos, -yPos);
				toDraw.drawText(output, actual.x, actual.y-yPos);
			}
			else {
				//toDraw.fillRectangle(actual.x, actual.y+30, xPos, yPos);
				toDraw.drawText(output, actual.x, actual.y+30);
			}
			
			font.dispose();
			
			Color color = new Color(canvas.getDisplay(), new RGB(interpolatedValue, interpolatedValue, interpolatedValue));
			
			toDraw.setBackground(color);
			toDraw.fillRectangle(0, canvas.canvasDimension.height-10, canvas.canvasDimension.width, canvas.canvasDimension.height);
		}
		
		
		toDraw.setBackground(background);
		toDraw.setForeground(foreground);
		toDraw.setFont(backupFont);
		return toDraw;
	}

	@Override
	public void actionMouseEnter(Event e) {
		mouseEntered = true;
		mouseExited = false;
	}

	@Override
	public void actionMouseExit(Event e) {
		mouseEntered = false;
		mouseExited = true;
	}

	@Override
	public void actionMouseWheel(Event e) {
		// TODO Auto-generated method stub
		
	}

}
