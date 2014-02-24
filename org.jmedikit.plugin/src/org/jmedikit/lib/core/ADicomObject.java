package org.jmedikit.lib.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jmedikit.lib.image.AImage;
import org.jmedikit.lib.io.DicomData;
import org.jmedikit.lib.io.DicomImageData;
import org.jmedikit.lib.io.IDicomData;
import org.jmedikit.lib.io.IDicomImageData;

/**
 * <p>Die Abstrakte Klasse ADicomObject symbolisiert den Adapter für den Bild- und Datenteil von DICOM-Objekten.</p>
 * <p>Es werden Methoden zum Auslesen der Tags, den Werten und den Bilddaten bereitgestellt. Für eine konkrete Implementierung und Instantiierung muss ein {@link DicomObject} verwendet werden.</p>
 * <p>Neben der Funktion als Adapter, stellt diese Klasse gleichzeitig einen Knoten im DICOM-Baum dar.</p>
 * @author rkorb
 *
 */
public abstract class ADicomObject extends ADicomTreeItem implements IDicomData, IDicomImageData{

	/**
	 * Adapter des DICOM-Datenteils
	 */
	private IDicomData data;
	
	/**
	 * Adapter des DICOM-Bildteils
	 */
	private IDicomImageData imagedata;
	
	/**
	 * Pfad zur DICOM-Datei
	 */
	private File f;
	
	/**
	 * <p>Konkrete DICOM-Objekte stellen immer Blattknoten im Baum dar und erhalten den UniqueIdentifier aus dem DICOM-Tag SOPInstanceUID</p>
	 * 
	 * @param input
	 * @throws IOException
	 */
	public ADicomObject(File input) throws IOException{
		super(input.getPath());

		f = input;
		if(f.length() < 1){
			throw new IllegalArgumentException(f.getPath()+" filesize is zero");
		}
		
		data = new DicomData(f);
		imagedata = new DicomImageData(f, data);
		
		String objectUid = (String) getTagData("SOPInstanceUID", ADicomObject.RETURN_STRING);
		this.setUid(objectUid);
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
		return data.getTagArray(tag, returnType);
	}

	
	@Override 
	public ArrayList<String[]> getTags(){
		return data.getTags();
	}
	
	
	@Override
	public void setTagDataArray(String tag, Object value){
		data.setTagDataArray(tag, value);
	}
	
	
	
	@Override
	public String getVR(String tag){
		return data.getVR(tag);
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
