package org.jmedikit.lib.core;

import java.util.ArrayList;

public class DicomStudy {
	public String studyInstanceUid;
	
	public ArrayList<DicomSeries> series;
	
	public DicomStudy(String uid){
		studyInstanceUid = uid;
		series = new ArrayList<>();
	}

	public DicomSeries getDicomSeries(int index){
		return series.get(index);
	}
	
	public ArrayList<DicomSeries> getDicomSeries(){
		return series;
	}
	
	public String getStudyInstanceUid(){
		return studyInstanceUid;
	}
	
	public boolean addDicomSeries(DicomSeries series){
		if(this.series.contains(series)){
			return false;
		}
		else{
			return this.series.add(series);
		}
	}
	
	public boolean removeDicomObject(DicomSeries series){
		return this.series.remove(series);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((studyInstanceUid == null) ? 0 : studyInstanceUid.hashCode());
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
		DicomStudy other = (DicomStudy) obj;
		if (studyInstanceUid == null) {
			if (other.studyInstanceUid != null)
				return false;
		} else if (!studyInstanceUid.equals(other.studyInstanceUid))
			return false;
		return true;
	}

	
	
	/*@Override
	public boolean equals(Object object){
		boolean equ = false;
		if(object != null && object instanceof DicomStudy){
			DicomStudy other = (DicomStudy)object;
			equ = (studyInstanceUid.equals(other.studyInstanceUid));
		}
		return equ;
	}*/
	
	
}
