package org.jmedikit.lib.core;

import java.util.ArrayList;

public class Patient {
	
	private String patientName;
	
	ArrayList<DicomStudy> studies;
	
	public Patient(String name){
		patientName = name;
		studies = new ArrayList<DicomStudy>();
	}

	public String getPatientName() {
		return patientName;
	}

	public ArrayList<DicomStudy> getDicomStudies() {
		return studies;
	}
	
	public DicomStudy getDicomStudy(int index){
		return studies.get(index);
	}
	
	public boolean addDicomStudy(DicomStudy study){
		if(studies.contains(study)){
			return false;
		}
		else{
			return studies.add(study);
		}
	}
	
	public boolean removeDicomStudy(DicomStudy study){
		return studies.remove(study);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((patientName == null) ? 0 : patientName.hashCode());
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
		Patient other = (Patient) obj;
		if (patientName == null) {
			if (other.patientName != null)
				return false;
		} else if (!patientName.equals(other.patientName))
			return false;
		return true;
	}
}
