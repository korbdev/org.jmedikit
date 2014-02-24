package org.jmedikit.lib.io;

import java.util.ArrayList;

public interface IDicomData {
	
	public static final int RETURN_BYTE = 0;
	public static final int RETURN_SHORT = 1;
	public static final int RETURN_USHORT = 2;
	public static final int RETURN_INT = 3;
	public static final int RETURN_FLOAT = 4;
	public static final int RETURN_DOUBLE = 5;
	public static final int RETURN_STRING = 6;
	public static final int RETURN_NESTED_DICOM = 7;
	
	/**
	 * <p>Diese Methode dient zum Auslesen der DICOM-Tags aus den DICOM-Objekten. Die Übergabe des Tags erfolgt als String ohne Leerzeichen. 
	 * Mögliche Rückgabetypen sind als Konstanten in IDicomData definiert.</p>
	 * 
	 * <ul><b>Beispiele</b>
	 *   <li><b>Auslesen des Patientennamens</b>
	 * <pre>
	 *DicomObject obj = ...
	 *String patientName  = (String) obj.getTagData("PatientName", IDicomData.RETURN_STRING);
	 *</pre>
	 *   </li>
	 *   <li><b>Auslesen der Pixel Repräsentation</b>
	 * <pre>
	 *DicomObject obj = ...
	 *int signed = (int) obj.getTagData("PixelRepresentation", IDicomData.RETURN_INT);
	 *</pre>
	 *   </li>
	 * </ul>
	 * 
	 * <p>Unterstützt werden die Rückgabetypen wie in {@link IDicomData} spezifiziert.</p>
	 * 
	 * 
	 * @param tag Stringrepräsentation des DicomTags
	 * @param returnType Spezifiziert den Rückgabetyp
	 */
	public Object getTagData(String tag, int returnType);
	
	/**
	 * <p>Gibt zur einem Tag die zugehörige Value Representation als String zurück. Die Übergabe des Tags erfolgt als String ohne Leerzeichen. </p>
	 * @param tag Stringrepräsentation des Tags
	 */
	public String getVR(String tag);
	
	/**
	 * <p>Diese Methode dient zum Auslesen der DICOM-Tags aus den DICOM-Objekten als Array. Diese Methode wird bei Tags mit einer Value Multiplicity > 1 benötigt.
	 * Die Übergabe des Tags erfolgt als String ohne Leerzeichen. Aktuell wird nur der Rückgabetyp RETURN_STRING unterstützt.
	 * Mögliche Rückgabetypen sind als Konstanten in IDicomData definiert.</p>
	 * 
	 * @param tag Stringrepräsentation des DicomTags
	 * @param returnType Spezifiziert den Rückgabetyp
	 */
	public Object getTagArray(String tag, int returnType);
	
	/**
	 * <p>Gibt alle Tags eines DICOM-Objekts in einer Liste aus String-Arrays zurück. Ein String-Array besitzt folgende Informationen </p>
	 * <ul>String[5]
	 * 	<li>[0] => Tagname</li>
	 * <li>[1] => Tag - numerische Darstellung</li>
	 * <li>[2] => Value Representation</li>
	 * <li>[3] => Value Multiplicity</li>
	 * <li>[4] => Wert(nur die ersten Zeichen sind enthalten)</li>
	 * <li>[5] => Länge des Wertes</li>
	 * </ul>
	 */
	public ArrayList<String[]> getTags();
	
	/**
	 * <p>Diese Methode dient zum Setzen der DICOM-Tags in den DICOM-Objekten.
	 * Die Übergabe des Tags und der Value Representation erfolgen als String ohne Leerzeichen. Unterstützt werden Integer- und String- Objekte im Parameter value
	 * </p>
	 * 
	 * @param tag Stringrepräsentation des Tags
	 * @param vr Stringrepräsemtatopm der Value Representation
	 * @param value Der Wert, der gesetzt wird
	 */
	public void setTagData(String tag, String vr, Object value);
	
	/**
	 * <p>Diese Methode dient zum Setzen der DICOM-Tags in den DICOM-Objekten.
	 * Die Übergabe des Tags und der Value Representation erfolgen als String ohne Leerzeichen. 
	 * Unterstützt werden Integer-, Short- und String- Arrays im Parameter value.
	 * </p>
	 * 
	 * @param tag Stringrepräsentation des Tags
	 * @param value Der Wert, der gesetzt wird
	 */
	public void setTagDataArray(String tag, Object value);
	
	@Override
	public boolean equals(Object object);
}