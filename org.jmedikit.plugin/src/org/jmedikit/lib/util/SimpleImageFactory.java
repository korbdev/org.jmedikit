package org.jmedikit.lib.util;

import org.jmedikit.lib.image.AImage;
import org.jmedikit.lib.image.IntegerImage;
import org.jmedikit.lib.image.ShortImage;
import org.jmedikit.lib.image.UnsignedByteImage;
import org.jmedikit.lib.image.UnsignedShortImage;

/**
 * 
 * Die einfache Fabrik SimpleImageFactory wird zur impliziten Bilderzeugung eingesetzt. Ist der Bildtyp nicht explizit bekannt, kann das passende {@link AImage} erzeugt werden.
 * <p>Beispiel</p>
 * <pre>
 * <code>
 * AImage img = obj.getImage(0);
 * AImage emptyImg = SimpleImageFactory.getAbstractImage(img.getImageType, 512, 512);
 * </code>
 * </pre>
 * 
 *<p>Der Bildtyp von img ist nicht explizit bekannt, er könnte 8-, 16-Bit oder 32-Bit haben. Mit der Fabrik kann ein neues Bild mit identischem Typ erstellt werden.</p>
 * @author rkorb
 *
 */
public class SimpleImageFactory {
	
	/**
	 * Erzeugt ein leeres Bild {@link AImage} mit der Dimension width x height vom Typ des übergebenen Parameters type.
	 * 
	 * 
	 * @param type Zu erzeugender Bildtyp
	 * @param width Breite des neuen Bildes
	 * @param height Höhe des neuen Bildes
	 * @return leeres Bild vom Typ type
	 */
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
