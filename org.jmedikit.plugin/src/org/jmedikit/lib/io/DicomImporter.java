package org.jmedikit.lib.io;

import java.io.File;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;

import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.lib.core.DicomTreeRepository;

public class DicomImporter extends Job{
	
	@Inject
	IEventBroker broker;
	
	File importLocation;
	
	private int filenumber;
	
	IProgressMonitor monitor;
	
	private DicomTreeRepository tree;
	
	public DicomImporter(String jobName, File f){
		super(jobName);
		importLocation = f;
		filenumber = 0;

		System.out.println(filenumber);
		tree = new DicomTreeRepository();
	}
	
	private void countFiles(File f){
		if(f.isDirectory()){
			File[] files = f.listFiles();
			for(int i = 0; i < files.length; i++){
				if(files[i].isDirectory()){
					countFiles(files[i]);
				}
				else{
					filenumber++;
				}
			}
		}
		else{
			filenumber++;
		}
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
					monitor.worked(1);
				}
			}
		}
		else {
			importDicomObject(importLocation);
			monitor.worked(1);
		}
	}
	
	private void importDicomObject(File f){

		DicomObject obj = null;

		try {
			obj = new DicomObject(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(obj != null){
			tree.insert(obj);
		}
		
	}

	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		countFiles(importLocation);
		
		this.monitor = monitor;
		this.monitor.beginTask(this.getName(), filenumber);
		
		this.importDicomFiles();
		tree.walkDicomTreeRepository();
		monitor.done();

		return Status.OK_STATUS;
	}

	public DicomTreeRepository getTree() {
		return tree;
	}
}