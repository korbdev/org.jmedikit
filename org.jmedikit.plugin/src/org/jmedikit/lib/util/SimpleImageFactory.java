package org.jmedikit.lib.util;

import org.jmedikit.lib.image.AbstractImage;
import org.jmedikit.lib.image.ShortImage;
import org.jmedikit.lib.image.UnsignedByteImage;
import org.jmedikit.lib.image.UnsignedShortImage;

public class SimpleImageFactory {
	public static AbstractImage getAbstractImage(int type, int width, int height){
		switch (type) {
		case AbstractImage.TYPE_BYTE_SIGNED:
			System.out.println("Not supported");
			break;
		case AbstractImage.TYPE_BYTE_UNSIGNED:
			return new UnsignedByteImage(width, height);
		case AbstractImage.TYPE_SHORT_SIGNED:
			return new ShortImage(width, height);
		case AbstractImage.TYPE_SHORT_UNSIGNED:
			return new UnsignedShortImage(width, height);
		default:
			throw new IllegalArgumentException("ImageType "+type+" not supported");
		}
		return null;
	}
}
