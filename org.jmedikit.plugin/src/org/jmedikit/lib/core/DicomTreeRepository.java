package org.jmedikit.lib.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.jmedikit.lib.image.AImage;
import org.jmedikit.lib.io.DicomData;

public class DicomTreeRepository {
	
	private ADicomTreeItem root;
	
	private int countObjects;
	
	public DicomTreeRepository(){
		countObjects = 0;
		root = new ADicomTreeItem("/") {
		};
	}
	
	public ADicomTreeItem getRoot(){
		return root;
	}
	
	public ADicomTreeItem lookUpDicomTreeItem(ADicomTreeItem item){
		Queue<ADicomTreeItem> queue = new LinkedList<>();
		ArrayList<ADicomTreeItem> visited = new ArrayList<>();
		
		queue.offer(root);
		visited.add(root);
		
		while(queue.size() > 0){
			ADicomTreeItem actual = queue.poll();
			if(actual.getUid().equals(item.getUid())){
				return actual;
			}
			for(ADicomTreeItem child : actual.getChildren()){
				if(!visited.contains(child)){
					queue.offer(child);
					visited.add(child);
				}
			}
		}
		return null;
	}
	
	public ADicomTreeItem lookUpDicomTreeItem(String uid){
		Queue<ADicomTreeItem> queue = new LinkedList<>();
		ArrayList<ADicomTreeItem> visited = new ArrayList<>();
		
		queue.offer(root);
		visited.add(root);
		
		while(queue.size() > 0){
			ADicomTreeItem actual = queue.poll();
			System.out.println(actual.getUid());
			if(actual.getUid().equals(uid)){
				return actual;
			}
			for(ADicomTreeItem child : actual.getChildren()){
				if(!visited.contains(child)){
					queue.offer(child);
					visited.add(child);
				}
			}
		}
		return null;
	}
	
	public void walkDicomTreeRepository(){
		Queue<ADicomTreeItem> queue = new LinkedList<>();
		ArrayList<ADicomTreeItem> visited = new ArrayList<>();
		
		queue.offer(root);
		visited.add(root);
		
		while(queue.size() > 0){
			ADicomTreeItem actual = queue.poll();
			//System.out.println(actual.getUid());
			for(ADicomTreeItem child : actual.getChildren()){
				if(!visited.contains(child)){
					queue.offer(child);
					visited.add(child);
					System.out.println("Visited "+child.getUid());
				}
			}
		}
	}
	
	public void insert(DicomObject item){
		//System.out.println("INSERT "+item.getUid());
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
		
		ADicomTreeItem lookUp = lookUpDicomTreeItem(sPatient);
		ADicomTreeItem parent = root;
		
		countObjects++;
		
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
	
	public int getNumberOfItems(){
		return countObjects;
	}
	
	public ArrayList<AImage> extractImages(String seriesUid){
		ArrayList<AImage> images = new ArrayList<AImage>();
		ADicomTreeItem item = null;
		
		//item muss eine Serie sein, damit Alle Bilder der Serie extrahiert werden können
		if((item = lookUpDicomTreeItem(seriesUid)) != null && item.getLevel() == ADicomTreeItem.TREE_SERIES_LEVEL){
			for(ADicomTreeItem child : item.getChildren()){
				DicomObject obj = (DicomObject) child;
				images.add(obj.getImage(0));
			}
		}
		return images;
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
