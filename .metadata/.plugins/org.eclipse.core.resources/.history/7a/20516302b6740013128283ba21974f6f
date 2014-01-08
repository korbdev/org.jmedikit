package org.jmedikit.lib.core;

import java.util.ArrayList;

import org.jmedikit.lib.image.AImage;
import org.jmedikit.plugin.gui.SimpleDicomCanvas;

public class Visualizer {

	public static void show(String title, AImage img){
		ArrayList<AImage> images = new ArrayList<AImage>();
		images.add(img);
		show(title, images);
	}
	
	public static void show(String title, ArrayList<AImage> images){
		SimpleDicomCanvas simpleCanvas = new SimpleDicomCanvas(title);
		simpleCanvas.show(images);
	}
}
