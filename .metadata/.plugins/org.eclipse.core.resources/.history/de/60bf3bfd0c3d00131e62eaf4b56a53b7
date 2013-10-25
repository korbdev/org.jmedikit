package org.jmedikit.lib.io;

import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;

import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che2.imageioimpl.plugins.dcm.DicomImageReader;
import org.jmedikit.lib.image.AbstractImage;
import org.jmedikit.lib.image.ShortImage;
import org.jmedikit.lib.image.UnsignedByteImage;
import org.jmedikit.lib.image.UnsignedShortImage;
//import org.eclipse.swt.awt.SWT_AWT;

public class BasicDicomImageData implements DicomImageData{
	
	private File f;
	private DicomImageReader dir;
	private ImageInputStream iis;
	private DicomImageReadParam param;
	private DicomData data;
	
	public BasicDicomImageData(File input, DicomData data) throws IOException{
		f = input;
		//dir = (DicomImageReader) ImageIO.getImageReadersByFormatName("DICOM").next();
		//param = (DicomImageReadParam) dir.getDefaultReadParam();
		this.data = data;
		iis = ImageIO.createImageInputStream(f);
		dir = (DicomImageReader) ImageIO.getImageReadersByFormatName("dicom").next();
		param = (DicomImageReadParam) dir.getDefaultReadParam();
		
		System.out.println(dir.toString()+", "+iis.toString()+", "+f.getPath());
		dir.setInput(iis);
	}
	
	@Override
	public AbstractImage getImage(int index) {
		AbstractImage img = null;
		
		int width = 0;
		int height = 0;
		
		int signed = (int) data.getTagData("PixelRepresentation", DicomData.RETURN_INT);
		
		String windowCenter = (String) data.getTagData("WindowCenter", DicomData.RETURN_STRING);
		String windowWidth = (String) data.getTagData("WindowWidth", DicomData.RETURN_STRING);
		
		String rescaleSlope = (String) data.getTagData("RescaleSlope", DicomData.RETURN_STRING);
		String rescaleIntercept = (String) data.getTagData("RescaleIntercept", DicomData.RETURN_STRING);
		
		System.out.println(windowCenter+", "+windowWidth+", "+rescaleIntercept+", "+rescaleSlope);
		
		float wc = (windowCenter != null && !windowCenter.equals("default")) ? Float.parseFloat(windowCenter) : 0 ;
		float ww = (windowWidth != null && !windowWidth.equals("default")) ? Float.parseFloat(windowWidth) : 0 ;
		
		float m = (rescaleSlope != null && !rescaleSlope.equals("default")) ? Float.parseFloat(rescaleSlope) : 1f ;
		float b = (rescaleIntercept != null && !rescaleIntercept.equals("default")) ? Float.parseFloat(rescaleIntercept) : 0f ;
		
		System.out.println("Load Images with m = "+m+", b = "+b+", "+f.getPath()+" readRaster "+dir.canReadRaster());
		try {
			width = dir.getWidth(index);
			height = dir.getHeight(index);
			
			
			/*byte[] bytes = dir.readBytes(index, param);
			System.out.println("BYTE "+bytes.length);
			for(int i = 0; i < bytes.length; i++){
				System.out.println(bytes[i]);
			}*/
			Raster r = dir.readRaster(index, null );
			//iis.close();
			DataBuffer buffer = r.getDataBuffer();
			
			int bufferType = buffer.getDataType();
			
			//System.out.println(buffer.getElem(50, 199));
			
			switch(bufferType){
			case DataBuffer.TYPE_BYTE:
				if(signed == AbstractImage.TYPE_SIGNED){
					System.out.println("TODO: SIGNED BYTE IMAGE");
				}
				else{
					img = new UnsignedByteImage(width, height, buffer, m, b, wc, ww);
					return img;
				}
			case DataBuffer.TYPE_USHORT:
				if(signed == AbstractImage.TYPE_SIGNED){
					img = new ShortImage(width, height, buffer, m, b, wc, ww);
					return img;
				}
				else{
					img = new UnsignedShortImage(width, height, buffer, m, b, wc, ww);
					return img;
				}		
			default:
				throw new IllegalArgumentException("DataBuffer " + buffer.getDataType() + " not supported");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ArrayList<AbstractImage> getImages() {
		ArrayList<AbstractImage> images;
		try {
			int frames = dir.getNumImages(false);
			images = new ArrayList<AbstractImage>(frames);
			for(int i = 0; i < frames; i++){
				AbstractImage img = this.getImage(i);
				images.add(img);
			}
			return images;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getWidth(int index) {
		try {
			return dir.getWidth(index);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public int getHeight(int index) {
		try {
			return dir.getHeight(index);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public int getDepth() {
		try {
			return dir.getNumImages(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	
}
