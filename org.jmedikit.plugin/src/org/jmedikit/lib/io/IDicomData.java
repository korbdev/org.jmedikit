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
	 * <p>Diese Methode dient zum Auslesen der DICOM-Tags aus den DICOM-Objekten. Die �bergabe des Tags erfolgt als String ohne Leerzeichen. 
	 * M�gliche R�ckgabetypen sind als Konstanten in IDicomData definiert.</p>
	 * 
	 * <ul><b>Beispiele</b>
	 *   <li><b>Auslesen des Patientennamens</b>
	 * <pre>
	 *DicomObject obj = ...
	 *String patientName  = (String) obj.getTagData("PatientName", IDicomData.RETURN_STRING);
	 *</pre>
	 *   </li>
	 *   <li><b>Auslesen der Pixel Repr�sentation</b>
	 * <pre>
	 *DicomObject obj = ...
	 *int signed = (int) obj.getTagData("PixelRepresentation", IDicomData.RETURN_INT);
	 *</pre>
	 *   </li>
	 * </ul>
	 * 
	 * <p>Unterst�tzt werden die R�ckgabetypen wie in {@link IDicomData} spezifiziert.</p>
	 * 
	 * 
	 * @param tag Stringrepr�sentation des DicomTags
	 * @param returnType Spezifiziert den R�ckgabetyp
	 */
	public Object getTagData(String tag, int returnType);
	
	/**
	 * <p>Gibt zur einem Tag die zugeh�rige Value Representation als String zur�ck. Die �bergabe des Tags erfolgt als String ohne Leerzeichen. </p>
	 * @param tag Stringrepr�sentation des Tags
	 */
	public String getVR(String tag);
	
	/**
	 * <p>Diese Methode dient zum Auslesen der DICOM-Tags aus den DICOM-Objekten als Array. Diese Methode wird bei Tags mit einer Value Multiplicity > 1 ben�tigt.
	 * Die �bergabe des Tags erfolgt als String ohne Leerzeichen. Aktuell wird nur der R�ckgabetyp RETURN_STRING unterst�tzt.
	 * M�gliche R�ckgabetypen sind als Konstanten in IDicomData definiert.</p>
	 * 
	 * @param tag Stringrepr�sentation des DicomTags
	 * @param returnType Spezifiziert den R�ckgabetyp
	 */
	public Object getTagArray(String tag, int returnType);
	
	/**
	 * <p>Gibt alle Tags eines DICOM-Objekts in einer Liste aus String-Arrays zur�ck. Ein String-Array besitzt folgende Informationen </p>
	 * <ul>String[5]
	 * 	<li>[0] => Tagname</li>
	 * <li>[1] => Tag - numerische Darstellung</li>
	 * <li>[2] => Value Representation</li>
	 * <li>[3] => Value Multiplicity</li>
	 * <li>[4] => Wert(nur die ersten Zeichen sind enthalten)</li>
	 * <li>[5] => L�nge des Wertes</li>
	 * </ul>
	 */
	public ArrayList<String[]> getTags();
	
	/**
	 * <p>Diese Methode dient zum Setzen der DICOM-Tags in den DICOM-Objekten.
	 * Die �bergabe des Tags und der Value Representation erfolgen als String ohne Leerzeichen. Unterst�tzt werden Integer- und String- Objekte im Parameter value
	 * </p>
	 * 
	 * @param tag Stringrepr�sentation des Tags
	 * @param vr Stringrepr�semtatopm der Value Representation
	 * @param value Der Wert, der gesetzt wird
	 */
	public void setTagData(String tag, String vr, Object value);
	
	/**
	 * <p>Diese Methode dient zum Setzen der DICOM-Tags in den DICOM-Objekten.
	 * Die �bergabe des Tags und der Value Representation erfolgen als String ohne Leerzeichen. 
	 * Unterst�tzt werden Integer-, Short- und String- Arrays im Parameter value.
	 * </p>
	 * 
	 * @param tag Stringrepr�sentation des Tags
	 * @param value Der Wert, der gesetzt wird
	 */
	public void setTagDataArray(String tag, Object value);
	
	@Override
	public boolean equals(Object object);
}