package org.jmedikit.lib.io;

import java.util.ArrayList;

import org.jmedikit.lib.image.AbstractImage;

public interface DicomImageData {
	
	public int getWidth(int index);
	public int getHeight(int index);
	public int getDepth();
	public AbstractImage getImage(int index);
	public ArrayList<AbstractImage> getImages();
}
