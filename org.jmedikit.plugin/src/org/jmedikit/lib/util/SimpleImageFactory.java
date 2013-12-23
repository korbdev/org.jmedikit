package org.jmedikit.lib.util;

import org.jmedikit.lib.image.AImage;
import org.jmedikit.lib.image.IntegerImage;
import org.jmedikit.lib.image.ShortImage;
import org.jmedikit.lib.image.UnsignedByteImage;
import org.jmedikit.lib.image.UnsignedShortImage;

public class SimpleImageFactory {
	public static AImage getAbstractImage(int type, int width, int height){
		switch (type) {
		case AImage.TYPE_BYTE_SIGNED:
			System.out.println("Not supported");
			break;
		case AImage.TYPE_BYTE_UNSIGNED:
			return new UnsignedByteImage(width, height);
		case AImage.TYPE_SHORT_SIGNED:
			return new ShortImage(width, height);
		case AImage.TYPE_SHORT_UNSIGNED:
			return new UnsignedShortImage(width, height);
		case AImage.TYPE_INT_SIGNED:
			return new IntegerImage(width, height);
		default:
			throw new IllegalArgumentException("ImageType "+type+" not supported");
		}
		return null;
	}
}
