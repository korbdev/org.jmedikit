package org.jmedikit.lib.io;

public interface DicomData {
	
	public static final int RETURN_BYTE = 0;
	public static final int RETURN_SHORT = 1;
	public static final int RETURN_USHORT = 2;
	public static final int RETURN_INT = 3;
	public static final int RETURN_FLOAT = 4;
	public static final int RETURN_DOUBLE = 5;
	public static final int RETURN_STRING = 6;
	public static final int RETURN_NESTED_DICOM = 7;
	
	public Object getTagData(String tag, int returnType);
	public String getVR(String tag, int returnType);
	public Object getTagArray(String tag, int returnType);
	
	public void setTagData(String tag, String vr, Object value);
	public void setTagDataArray(String tag, Object value);
	
	public boolean equals(Object object);
	//public void writeDicomFile(String filename, BufferedImage bi);
}