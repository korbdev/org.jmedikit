package org.jmedikit.lib.util;


import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Event;

public class ResizeTool extends ATransformationTool{

	float scale;
	
	public ResizeTool(DicomPaintingTool paintingTool) {
		super(paintingTool);
		scale = 0;
	}

	@Override
	void actionMouseMove(Event e) {
		if(paintingTool.mouseDown){
			float scaleOld = scale;
			scale = (float)(paintingTool.actual.y - paintingTool.start.y)/10;
			System.out.println("Scale "+(scale-scaleOld));
			if(scale > 0){
				float newScale = (float)Math.abs(scale);
				System.out.println("width "+(int) (paintingTool.imageDimension.width * 1+newScale));
				System.out.println("height "+(int) (paintingTool.imageDimension.height * 1+newScale));
				paintingTool.imageDimension.width = (int) (paintingTool.imageDimension.width * 1+newScale);
				paintingTool.imageDimension.height = (int) (paintingTool.imageDimension.height * 1+newScale);
			}
			else{
				float newScale = (float)Math.abs(scale);
				System.out.println("width "+(int) (paintingTool.imageDimension.width * 1-newScale));
				System.out.println("height "+(int) (paintingTool.imageDimension.height * 1-newScale));
				paintingTool.imageDimension.width = (int) (paintingTool.imageDimension.width * 1-newScale);
				paintingTool.imageDimension.height = (int) (paintingTool.imageDimension.height * 1-newScale);
			}
				
		}
		
	}

	@Override
	void actionMouseDown(Event e) {
		paintingTool.mouseDown = true;
	}

	@Override
	void actionMouseUp(Event e) {
		paintingTool.mouseDown = false;
	}

	@Override
	GC postCalculation(GC toDraw) {
		return toDraw;
	}

	
}
