package org.jmedikit.lib.io;

import java.util.ArrayList;

import org.jmedikit.lib.image.AImage;

public interface DicomImageData {
	
	public int getWidth(int index);
	public int getHeight(int index);
	public int getDepth();
	public int getSimplePixel(int x, int y, int z);
	public AImage getImage(int index);
	public ArrayList<AImage> getImages();
}
