package org.jmedikit.lib.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.util.TagUtils;
import org.jmedikit.lib.core.ADicomObject;

/**
 * 
 * DicomData implementiert das Interface IDicomData und ist damit der konkrete Adapter für den Datenteil eines DICOM-Objekts.
 * Diese Klasse stellt die Schnittstelle zur externen Bibliothek zur Verarbeitung der DICOM-Tags bereit. Sie ist nicht für eine explizite Instantiierung
 * vorgesehen, sondern ist Teil eines Adapters und wird von der abstrakten Klasse {@link ADicomObject} adaptiert. Für eine Arbeit mit DICOM-Objekten können
 * Instanzen des konkreten Adapters {@link DicomObject} erzeugt werden. 
 * 
 * @author rkorb
 *
 */
public class DicomData implements IDicomData{
	
	private File input;
	private DicomInputStream dis;
	private DicomObject dcmobj;
	private DicomObject meta;
	
	private DicomData(DicomObject obj){
		dcmobj = obj;
	}
	
	public DicomData(File input) throws IOException {
		this.input = input;
		
		dis = new DicomInputStream(input);
		dcmobj = dis.readDicomObject();
		dis.readFileMetaInformation(meta);
	}
	
	@Override
	public Object getTagData(String tag, int returnType) {
		int dicomTag = Tag.toTag(tag);
		switch(returnType){
		case RETURN_INT :
			return dcmobj.getInt(dicomTag);
		case RETURN_FLOAT :
			return dcmobj.getFloat(dicomTag);
		case RETURN_DOUBLE :
			return dcmobj.getDouble(dicomTag);
		case RETURN_STRING :
			return (dcmobj.getString(dicomTag) != null) ? dcmobj.getString(dicomTag) : "default";
		case RETURN_NESTED_DICOM :
			DicomObject obj = dcmobj.getNestedDicomObject(dicomTag);
			return new DicomData(obj);
		default:
			throw new IllegalArgumentException("returnType " + returnType+" not supported");
		}
	}

	@Override
	public String getVR(String tag) {
		int dicomTag = Tag.toTag(tag);
		return dcmobj.vrOf(dicomTag).toString();
	}

	/*@Override
	public void writeDicomFile(String filename, BufferedImage bi) {

	}*/

	@Override
	public ArrayList<String[]> getTags(){
		ArrayList<String[]> tags = new ArrayList<String[]>();
		Iterator<DicomElement> iter = dcmobj.iterator();
		while(iter.hasNext()){
			
			String[] item = new String[6];
			
			DicomElement element = iter.next();
			
			String tagname = dcmobj.nameOf(element.tag());
			
			String tag = TagUtils.toString(element.tag());
			
			String vr = dcmobj.vrOf(element.tag()).toString();
			String vm = dcmobj.vm(element.tag())+"";
			
			String length = element.length()+"";
			
			String e = element.toString();
			int start = e.lastIndexOf("[");
			int end = e.lastIndexOf("]");
			String value = e.substring(start+1, end);

			item[0] = tagname; item[1] = tag; item[2]=vr; item[3]=vm+""; item[4]=value; item[5] = length;
			
			tags.add(item);
		}
		return tags;
	}
	
	@Override
	public Object getTagArray(String tag, int returnType) {
		int dicomTag = Tag.toTag(tag);
		switch(returnType){
		case RETURN_STRING :
			//return dcmobj.getStrings(dicomTag);
			return (dcmobj.getStrings(dicomTag) != null) ? dcmobj.getStrings(dicomTag) : new String[]{"default"};
		default:
			throw new IllegalArgumentException("returnType " + returnType+" not supported");
		}
	}

	@Override
	public void setTagData(String tag, String valueRepresentation, Object value) {
		
		int dicomTag = Tag.toTag(tag);
		VR vr = getVRClass(valueRepresentation);
		
		if(value instanceof Integer)
			dcmobj.putInt(dicomTag, vr, (int) value);
		else if(value instanceof String)
			dcmobj.putString(dicomTag, vr, (String)value);
		else throw new IllegalArgumentException("type of value not supportet. "+value.getClass().getName()+" given");
	}

	@Override
	public void setTagDataArray(String tag, Object value) {
		int dicomTag = Tag.toTag(tag);
		
		if(value instanceof short[]){
			dcmobj.putShorts(dicomTag, dcmobj.vrOf(dicomTag), (short[])value);
		}
		else if(value instanceof int[]){
			dcmobj.putInts(dicomTag, dcmobj.vrOf(dicomTag), (int[])value);
		}
		else if(value instanceof String[]){
			dcmobj.putStrings(dicomTag, dcmobj.vrOf(dicomTag), (String[])value);
		}
		else throw new IllegalArgumentException("type of value not supportet. "+value.getClass().getName()+" given");
	}

	private static VR getVRClass(String valueRepresentation){
		VR vr = null;
		Class<?> c = null;
		try {
			c = Class.forName("org.dcm4che2.data.VR");
			vr = (VR) c.getField(valueRepresentation).get(null);
		} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		return vr;
	}

	@Override
	public boolean equals(Object obj) {
		DicomData other = (DicomData) obj;
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (dcmobj == null) {
			if (other.dcmobj != null)
				return false;
		} else if (!input.getPath().equals(other.input.getPath()))
			return false;
		return true;
	}
	
	
}
