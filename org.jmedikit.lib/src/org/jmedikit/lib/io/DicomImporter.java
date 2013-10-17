package org.jmedikit.lib.io;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.lib.core.DicomSeries;
import org.jmedikit.lib.core.DicomStudy;
import org.jmedikit.lib.core.Patient;

public class DicomImporter {
	
	File importLocation;
	private ArrayList<Patient> patients;
	
	public DicomImporter(File f){
		importLocation = f;
		patients = new ArrayList<>();
		
		importDicomFiles();
	}
	
	
	private void importDicomFiles(){
		if(importLocation.isDirectory()){
			File[] files = importLocation.listFiles();
			for(File f : files){
				if(f.isDirectory()){
					importLocation = f;
					importDicomFiles();
				}
				else{
					importDicomObject(f);
				}
			}
		}
		else importDicomObject(importLocation);
	}
	
	private void importDicomObject(File f){
		try {
			DicomObject obj = new DicomObject(f);
			
			String name = (String) obj.getTagData("PatientName", DicomData.RETURN_STRING);
			String studyId = (String) obj.getTagData("StudyInstanceUID", DicomData.RETURN_STRING);
			String seriesId = (String) obj.getTagData("SeriesInstanceUID", DicomData.RETURN_STRING);
			
			DicomSeries singleSeries = new DicomSeries(seriesId);
			DicomStudy study = new DicomStudy(studyId);
			Patient p = new Patient(name);
			
			if(!patients.contains(p)){
				patients.add(p);
			}
			
			for(Patient patient : patients){
				ArrayList<DicomStudy> std = patient.getDicomStudies();
				if(!std.contains(study) && (patient.getPatientName().equals(p.getPatientName()))){
					patient.addDicomStudy(study);
				}
				for(DicomStudy dcmstudy : std){
					ArrayList<DicomSeries> srs = dcmstudy.getDicomSeries();
					if(!srs.contains(singleSeries) && (dcmstudy.getStudyInstanceUid().equals(study.getStudyInstanceUid()))){
						dcmstudy.addDicomSeries(singleSeries);
					}
					for(DicomSeries dcmsrs : srs){
						ArrayList<DicomObject> dcmobjs = dcmsrs.getDicomObjects();
						if(!dcmobjs.contains(obj) && dcmsrs.getSeriesInstanceUid().equals(singleSeries.getSeriesInstanceUid())){
							dcmsrs.addDicomObject(obj);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void printImportedLocation(){
		for(Patient p : patients){
			System.out.println("Patient "+p.getPatientName());
			for(DicomStudy stdy : p.getDicomStudies()){
				System.out.println("\tStudy "+stdy.getStudyInstanceUid());
				for(DicomSeries srs : stdy.getDicomSeries()){
					System.out.println("\t\tSeries "+srs.getSeriesInstanceUid());
					//System.out.println("\t\t\t"+srs.getDicomObjects().size());
					for(DicomObject obj : srs.getDicomObjects()){
						System.out.println("\t\t\t"+obj.getFile().getName()+", Frames: "+obj.getDepth());
					}
				}
			}
		}
	}
}
