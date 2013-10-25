package org.jmedikit.lib.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.jmedikit.lib.io.DicomData;

public class DicomTreeRepository {
	
	DicomTreeItem root;
	
	public DicomTreeRepository(){
		root = new DicomTreeItem("/") {
		};
	}
	
	public DicomTreeItem getRoot(){
		return root;
	}
	
	public DicomTreeItem lookUpDicomTreeItem(DicomTreeItem item){
		Queue<DicomTreeItem> queue = new LinkedList<>();
		ArrayList<DicomTreeItem> visited = new ArrayList<>();
		
		queue.offer(root);
		visited.add(root);
		
		while(queue.size() > 0){
			DicomTreeItem actual = queue.poll();
			if(actual.getUid().equals(item.getUid())){
				return actual;
			}
			for(DicomTreeItem child : actual.getChildren()){
				if(!visited.contains(child)){
					queue.offer(child);
					visited.add(child);
				}
			}
		}
		return null;
	}
	
	public DicomTreeItem lookUpDicomTreeItem(String uid){
		Queue<DicomTreeItem> queue = new LinkedList<>();
		ArrayList<DicomTreeItem> visited = new ArrayList<>();
		
		queue.offer(root);
		visited.add(root);
		
		while(queue.size() > 0){
			DicomTreeItem actual = queue.poll();
			if(actual.getUid().equals(uid)){
				return actual;
			}
			for(DicomTreeItem child : actual.getChildren()){
				if(!visited.contains(child)){
					queue.offer(child);
					visited.add(child);
				}
			}
		}
		return null;
	}
	
	public void walkDicomTreeRepository(){
		Queue<DicomTreeItem> queue = new LinkedList<>();
		ArrayList<DicomTreeItem> visited = new ArrayList<>();
		
		queue.offer(root);
		visited.add(root);
		
		while(queue.size() > 0){
			DicomTreeItem actual = queue.poll();
			System.out.println(actual.getUid());
			for(DicomTreeItem child : actual.getChildren()){
				if(!visited.contains(child)){
					queue.offer(child);
					visited.add(child);
				}
			}
		}
	}
	
	public void insert(DicomObject item){
		String nameId = (String) item.getTagData("PatientName", DicomData.RETURN_STRING);
		String studyId = (String) item.getTagData("StudyInstanceUID", DicomData.RETURN_STRING);
		String seriesId = (String) item.getTagData("SeriesInstanceUID", DicomData.RETURN_STRING);
		
		nameId = nameId.equals("default") ? "default_name" : nameId;
		studyId = studyId.equals("default") ? "default_study" : studyId;
		seriesId = seriesId.equals("default") ? "default_series" : seriesId;
		
		if(studyId.equals(seriesId)){
			seriesId += "__duplicated";
		}
		
		DicomPatientItem sPatient = new DicomPatientItem(nameId);
		DicomStudyItem sStudy = new DicomStudyItem(studyId);
		DicomSeriesItem sSeries = new DicomSeriesItem(seriesId);
		
		DicomTreeItem lookUp = lookUpDicomTreeItem(sPatient);
		DicomTreeItem parent = root;
		
		if(lookUp == null){
			parent.addChild(sPatient);
			sPatient.addChild(sStudy);
			sStudy.addChild(sSeries);
			sSeries.addChild(item);
		}
		else{
			parent = lookUp;
			lookUp = lookUpDicomTreeItem(sStudy);
			if(lookUp == null){
				parent.addChild(sStudy);
				sStudy.addChild(sSeries);
				sSeries.addChild(item);
			}
			else{
				parent = lookUp;
				lookUp = lookUpDicomTreeItem(sSeries);
				if(lookUp == null){
					parent.addChild(sSeries);
					sSeries.addChild(item);
				}
				else{
					parent = lookUp;
					lookUp = lookUpDicomTreeItem(item);
					if(lookUp == null){
						parent.addChild(item);
					}
				}
			}
		}
	}
	/*public void insertDicomObject(DicomObject obj){
		String name = (String) obj.getTagData("PatientName", DicomData.RETURN_STRING);
		String studyId = (String) obj.getTagData("StudyInstanceUID", DicomData.RETURN_STRING);
		String seriesId = (String) obj.getTagData("SeriesInstanceUID", DicomData.RETURN_STRING);
		
		DicomSeries singleSeries = new DicomSeries(seriesId);
		DicomStudy study = new DicomStudy(studyId);
		DicomPatient p = new DicomPatient(name);
		
		if(!root.contains(p)){
			root.add(p);
		}
		
		//System.out.println(name+", "+studyId+", "+seriesId);
		
		for(DicomPatient patient : root){
			ArrayList<DicomStudy> std = patient.getDicomStudies();
			//System.out.println(f.getPath()+"  "+std.toString()+", "+patient.getPatientName());
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
	}*/
}