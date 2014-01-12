package org.jmedikit.lib.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jmedikit.lib.image.AImage;
import org.jmedikit.lib.io.BasicDicomData;
import org.jmedikit.lib.io.BasicDicomImageData;
import org.jmedikit.lib.io.DicomData;
import org.jmedikit.lib.io.DicomImageData;

public abstract class ADicomObject extends ADicomTreeItem implements DicomData, DicomImageData{

	private DicomData data;
	private DicomImageData imagedata;
	private File f;
	
	public ADicomObject(File input) throws IOException{
		super(input.getPath());
		f = input;
		if(f.length() < 1){
			throw new IllegalArgumentException(f.getPath()+" filesize is zero");
		}
		
		data = new BasicDicomData(f);
		imagedata = new BasicDicomImageData(f, data);
		/*try {
			data = new BasicDicomData(f);
			imagedata = new BasicDicomImageData(f, data);
		} catch (Exception e) {
			throw new IllegalArgumentException(f.getPath()+" is not a DicomStream");
		}*/
	}

	public File getFile(){
		return f;
	}
	
	@Override
	public Object getTagData(String tag, int returnType){
		return data.getTagData(tag, returnType);
	}
	
	@Override
	public void setTagData(String tag, String vr, Object value){
		data.setTagData(tag, vr, value);
	}
	
	@Override
	public Object getTagArray(String tag, int returnType){
		return null;
	}
	
	@Override 
	public ArrayList<String> getTags(){
		return data.getTags();
	}
	
	@Override
	public void setTagDataArray(String tag, Object value){
		data.setTagDataArray(tag, value);
	}
	
	@Override
	public String getVR(String tag, int returnType){
		return data.getVR(tag, returnType);
	}
	
	@Override
	public int getSimplePixel(int x, int y, int z){
		return imagedata.getSimplePixel(x, y, z);
	}
	
	@Override
	public AImage getImage(int index){
		return imagedata.getImage(index);
	}
	
	@Override
	public ArrayList<AImage> getImages(){
		return imagedata.getImages();
	}
	
	@Override
	public int getWidth(int index){
		return imagedata.getWidth(index);
	}
	
	@Override
	public int getHeight(int index){
		return imagedata.getHeight(index);
	}
	
	@Override
	public int getDepth(){
		return imagedata.getDepth();
	}

	/*@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractDicomObject other = (AbstractDicomObject) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}*/
	
	
}
