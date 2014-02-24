package org.jmedikit.lib.util;

import org.itk.simple.Image;
import org.itk.simple.PixelIDValueEnum;
import org.itk.simple.VectorUInt32;
import org.jmedikit.lib.image.AImage;

/**
 * 
 * Die einfache SimpleITK-Fabrik konvertiert Bilder vom jMediKit-Format in das SimpleITK-Format und umgekehrt.
 * 
 * @author rkorb
 *
 */
public class SimpleITKFactory {
	/**
	 * 
	 * Konvertiert das jMediKit-Format in das SimpleITK-Format
	 * 
	 * @param img zu konvertierendes Bild
	 * @return konvertiertes Bild im SimpleITK-Format
	 */
	public static Image getITKImage(AImage img){
		switch (img.getImageType()) {
		case AImage.TYPE_BYTE_SIGNED:
			return produceITKImage(PixelIDValueEnum.sitkInt8, img);
		case AImage.TYPE_BYTE_UNSIGNED:
			return produceITKImage(PixelIDValueEnum.sitkUInt8, img);
		case AImage.TYPE_SHORT_SIGNED:
			return produceITKImage(PixelIDValueEnum.sitkInt16, img);
		case AImage.TYPE_SHORT_UNSIGNED:
			return produceITKImage(PixelIDValueEnum.sitkUInt16, img);
		case AImage.TYPE_INT_SIGNED:
			return produceITKImage(PixelIDValueEnum.sitkInt32, img);
		case AImage.TYPE_INT_UNSIGNED:
			return produceITKImage(PixelIDValueEnum.sitkUInt32, img);
		default:
			return null;
		}
	}
	
	/**
	 * Konvertiert das SimpleITK-Format ind das jMediKit-Format
	 * 
	 * @param itkImage zu konvertierendes Bild
	 * @return konvertiertes Bild im jMediKit-Format
	 */
	public static AImage getAImage(Image itkImage){
		int imageType = itkImage.getPixelIDValue();
		if(imageType == PixelIDValueEnum.sitkInt8.swigValue()){
			return produceAImage(AImage.TYPE_BYTE_SIGNED, itkImage);
		}
		else if(imageType == PixelIDValueEnum.sitkUInt8.swigValue()){
			return produceAImage(AImage.TYPE_BYTE_UNSIGNED, itkImage);
		}
		else if(imageType == PixelIDValueEnum.sitkInt16.swigValue()){
			return produceAImage(AImage.TYPE_SHORT_SIGNED, itkImage);
		}
		else if(imageType == PixelIDValueEnum.sitkUInt16.swigValue()){
			return produceAImage(AImage.TYPE_SHORT_UNSIGNED, itkImage);
		}
		else if(imageType == PixelIDValueEnum.sitkInt32.swigValue()){
			return produceAImage(AImage.TYPE_INT_SIGNED, itkImage);
		}
		else if(imageType == PixelIDValueEnum.sitkUInt32.swigValue()){
			return produceAImage(AImage.TYPE_INT_UNSIGNED, itkImage);
		}
		return null;
	}
	
	private static Image produceITKImage(PixelIDValueEnum itkImageType, AImage img){
		int width = img.getWidth();
		int height = img.getHeight();
		
		Image itkImage = new Image(width, height, itkImageType);
		VectorUInt32 v = new VectorUInt32(2);
		
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				v.set(0, x);
				v.set(1, y);
				//itkImage.setPixelAsUInt16(v, img.getPixel(x, y));
				if(itkImageType.swigValue() == PixelIDValueEnum.sitkInt8.swigValue()){
					itkImage.setPixelAsInt8(v, (byte) img.getPixel(x, y));
				}
				else if(itkImageType.swigValue() == PixelIDValueEnum.sitkUInt8.swigValue()){
					itkImage.setPixelAsUInt8(v, (short) img.getPixel(x, y));
				}
				else if(itkImageType.swigValue() == PixelIDValueEnum.sitkInt16.swigValue()){
					itkImage.setPixelAsInt16(v, (short) img.getPixel(x, y));
				}
				else if(itkImageType.swigValue() == PixelIDValueEnum.sitkUInt16.swigValue()){
					itkImage.setPixelAsUInt16(v, img.getPixel(x, y));
				}
				else if(itkImageType.swigValue() == PixelIDValueEnum.sitkInt32.swigValue()){
					itkImage.setPixelAsInt32(v, img.getPixel(x, y));
				}
				else if(itkImageType.swigValue() == PixelIDValueEnum.sitkUInt32.swigValue()){
					//return itkImage.getPixelAsUInt32(v);
				}
			}
		}
		return itkImage;
	}
	
	private static AImage produceAImage(int imageType, Image itkImage){
		
		int width = (int) itkImage.getWidth();
		int height = (int) itkImage.getHeight();
		
		AImage img = SimpleImageFactory.getAbstractImage(imageType, width, height);
		VectorUInt32 v = new VectorUInt32(2);
		
		for(int y = 0; y < img.getHeight(); y++){
			for(int x = 0; x < img.getWidth(); x++){
				v.set(0, x);
				v.set(1, y);
				int value = getITKPixelValue(itkImage, v);
				img.setPixel(x, y, value);
			}
		}
		return img;
	}
	
	/**
	 * Diese Methode gibt den Pixelwert eines SimpleITK-Bildes zurück, ohne den Bildtyp kennen zu müssen.
	 * Es werden folgende SimpleITK-Typen unterstützt
	 * <ul>
	 * <li>sitkInt8</li>
	 * <li>sitkUInt8</li>
	 * <li>sitkInt16</li>
	 * <li>sitkUInt16</li>
	 * <li>sitkInt32</li>
	 * </ul>
	 * Es wird null zurückgegeben, wenn der Bildtyp nicht unterstützt wird.
	 * <p>Ein Vektor wird wie folgt erzeugt.</p>
	 * <pre>
	 * <code>
	 * VectorUInt32 v = new VectorUInt32(2); // 2 Dimensionen
	 * v.set(0, x); //setzt den x-Index
	 * v.set(1, y); //setzt den y-Index
	 * </code>
	 * </pre>
	 * @param itkImage Bild des Pixelwerts
	 * @param v Vector mit den Koordinaten
	 * @return den Pixelwert als Integer, null wenn Bildtyp nicht unterstützt
	 */
	public static Integer getITKPixelValue(Image itkImage, VectorUInt32 v){
		int imageType = itkImage.getPixelIDValue();
		if(imageType == PixelIDValueEnum.sitkInt8.swigValue()){
			return (int) itkImage.getPixelAsInt8(v);
		}
		else if(imageType == PixelIDValueEnum.sitkUInt8.swigValue()){
			return (int) itkImage.getPixelAsUInt8(v);
		}
		else if(imageType == PixelIDValueEnum.sitkInt16.swigValue()){
			return (int) itkImage.getPixelAsInt16(v);
		}
		else if(imageType == PixelIDValueEnum.sitkUInt16.swigValue()){
			return itkImage.getPixelAsUInt16(v);
		}
		else if(imageType == PixelIDValueEnum.sitkInt32.swigValue()){
			return itkImage.getPixelAsInt32(v);
		}
		else if(imageType == PixelIDValueEnum.sitkUInt32.swigValue()){
			//return itkImage.getPixelAsUInt32(v);
		}
		return null;
	}
}
