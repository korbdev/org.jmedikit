package org.jmedikit.lib.util;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Event;


public class MoveTool extends ATransformationTool{

	public MoveTool(DicomPaintingTool paintingTool) {
		super(paintingTool);
		// TODO Auto-generated constructor stub
	}

	@Override
	void actionMouseMove(Event e) {
		if(paintingTool.mouseDown){
			
			Point2D<Integer> translationOld = new Point2D<>(paintingTool.translation.x, paintingTool.translation.y);
			
			paintingTool.translation.x = paintingTool.actual.x - paintingTool.start.x;
			paintingTool.translation.y = paintingTool.actual.y - paintingTool.start.y;
			//System.out.println(translation.toString());
			paintingTool.imageCenter = new Point2D<Integer>(paintingTool.imageCenter.x+(paintingTool.translation.x - translationOld.x), paintingTool.imageCenter.y+(paintingTool.translation.y - translationOld.y));
		}
	}

	@Override
	void actionMouseDown(Event e) {
		paintingTool.mouseDown = true;
		paintingTool.translation.x = 0;
		paintingTool.translation.y = 0;
	}

	@Override
	void actionMouseUp(Event e) {
		paintingTool.mouseDown = false;
	}

	@Override
	GC postCalculation(GC toDraw) {
		if(paintingTool.mouseDown){
			int rectWidth = (int) (paintingTool.imageDimension.width);
			int rectHeight = (int) (paintingTool.imageDimension.height);

			toDraw.drawRectangle(paintingTool.imageCenter.x-rectWidth/2, paintingTool.imageCenter.y - rectHeight/2, rectWidth, rectHeight);
			toDraw.setAlpha(50);
			toDraw.fillRectangle(paintingTool.imageCenter.x-rectWidth/2, paintingTool.imageCenter.y - rectHeight/2, rectWidth, rectHeight);
		}
		return toDraw;
	}

	

}
