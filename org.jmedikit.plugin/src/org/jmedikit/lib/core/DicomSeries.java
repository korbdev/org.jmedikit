package org.jmedikit.lib.core;

import java.util.ArrayList;

public class DicomSeries {
	
	/**
	 * UID der DicomSerie
	 */
	private String seriesInstanceUid;
	
	private ArrayList<DicomObject> dicomObjects;
	
	public DicomSeries(String uid){
		seriesInstanceUid = uid;
		dicomObjects = new ArrayList<>();
	}
	
	public DicomObject getDicomObject(int index){
		return dicomObjects.get(index);
	}
	
	public ArrayList<DicomObject> getDicomObjects(){
		return dicomObjects;
	}
	
	public String getSeriesInstanceUid(){
		return seriesInstanceUid;
	}
	
	public boolean addDicomObject(DicomObject obj){
		if(dicomObjects.contains(obj)){
			return false;
		}
		else{
			return dicomObjects.add(obj);
		}
	}
	
	public boolean removeDicomObject(DicomObject obj){
		return dicomObjects.remove(obj);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((seriesInstanceUid == null) ? 0 : seriesInstanceUid
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DicomSeries other = (DicomSeries) obj;
		if (seriesInstanceUid == null) {
			if (other.seriesInstanceUid != null)
				return false;
		} else if (!seriesInstanceUid.equals(other.seriesInstanceUid))
			return false;
		return true;
	}

	
	
	/*@Override
	public boolean equals(Object object){
		boolean equ = false;
		if(object != null && object instanceof DicomSeries){
			DicomSeries other = (DicomSeries)object;
			equ = (seriesInstanceUid.equals(other.seriesInstanceUid));
		}
		return equ;
	}*/
	
	
}
