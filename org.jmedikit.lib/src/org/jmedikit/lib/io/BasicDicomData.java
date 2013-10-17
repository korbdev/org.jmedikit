package org.jmedikit.lib.io;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomInputStream;

public class BasicDicomData implements DicomData{
	
	private File input;
	private DicomInputStream dis;
	private DicomObject dcmobj;
	private DicomObject meta;
	
	private BasicDicomData(DicomObject obj){
		dcmobj = obj;
	}
	
	public BasicDicomData(File input) {
		this.input = input;
		
		/*DicomObject obj = null;
		DicomObject nest = new BasicDicomObject();
		if(input.isDirectory()){
			File[] f = input.listFiles();
			
			dis = new DicomInputStream(f[0]);
			obj = dis.readDicomObject();
			dis.close();

			for(int i = 1; i < f.length; i++){
				dis = new DicomInputStream(f[i]);
				DicomObject compare = dis.readDicomObject();
				dis.close();
				System.out.println(obj.getString(Tag.NumberOfFrames));
				System.out.println();
				if(obj.getString(Tag.SeriesInstanceUID).equals(compare.getString(Tag.SeriesInstanceUID))){
					System.out.println(f[i-1].getName()+" x "+f[i].getName());
					System.out.println("same");
					System.out.println();
				}
				else{
					System.out.println(f[i-1].getName()+" : "+obj.getString(Tag.SeriesInstanceUID));
					System.out.println(f[i].getName()+" : "+compare.getString(Tag.SeriesInstanceUID));
					System.out.println();
				}
				obj = compare;
			}
		}*/
		try {
			dis = new DicomInputStream(input);
			dcmobj = dis.readDicomObject();
			dis.readFileMetaInformation(meta);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			//String result = dcmobj.getString(dicomTag);
			return (dcmobj.getString(dicomTag) != null) ? dcmobj.getString(dicomTag) : "emptyTag";
		case RETURN_NESTED_DICOM :
			DicomObject obj = dcmobj.getNestedDicomObject(dicomTag);
			return new BasicDicomData(obj);
		default:
			throw new IllegalArgumentException("returnType " + returnType+" not supported");
		}
	}

	@Override
	public String getVR(String tag, int returnType) {
		int dicomTag = Tag.toTag(tag);
		return dcmobj.vrOf(dicomTag).toString();
	}

	/*@Override
	public void writeDicomFile(String filename, BufferedImage bi) {

	}*/

	@Override
	public Object getTagArray(String tag, int returnType) {
		// TODO Auto-generated method stub
		return null;
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicDicomData other = (BasicDicomData) obj;
		if (dcmobj == null) {
			if (other.dcmobj != null)
				return false;
		} else if (!dcmobj.matches(other.dcmobj, false))
			return false;
		return true;
	}
	
	
}
