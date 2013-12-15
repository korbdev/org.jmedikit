package org.jmedikit.plugin.gui;

import java.io.File;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.ProgressProvider;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tools.services.IResourcePool;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.jmedikit.lib.core.DicomTreeItem;
import org.jmedikit.lib.core.DicomTreeRepository;
import org.jmedikit.lib.io.DicomImporter;
import org.jmedikit.lib.util.ImageProvider;
import org.jmedikit.plugin.gui.events.EventConstants;


public class DicomBrowserPart {
	
	@Inject
	IEventBroker broker;
	
	@Inject
	EModelService service;
	
	@Inject
	MApplication app;
	
	@Inject
	IResourcePool imageProvider;
	
	private Image rootIcon;
	private Image patientIcon;
	private Image studyIcon;
	private Image seriesIcon;
	private Image objectIcon;
	
	private Tree tree;
	//private TreeItem root;
	
	private DicomTreeRepository treeRepository;
	
	@PostConstruct
	public void createGui(Composite parent){
		
		rootIcon = imageProvider.getImageUnchecked(ImageProvider.DICOM_TREE_ROOT);
		patientIcon = imageProvider.getImageUnchecked(ImageProvider.DICOM_TREE_PATIENT);
		studyIcon = imageProvider.getImageUnchecked(ImageProvider.DICOM_TREE_STUDY);
		seriesIcon = imageProvider.getImageUnchecked(ImageProvider.DICOM_TREE_SERIES);
		objectIcon = imageProvider.getImageUnchecked(ImageProvider.DICOM_TREE_OBJECT);
		
		tree = new Tree(parent, SWT.BORDER);
		
		tree.addListener(SWT.MouseDoubleClick, new Listener() {
			@Override
			public void handleEvent(Event event) {
				TreeItem[] selectedItems = tree.getSelection();
				if(selectedItems.length == 1){
					String uid = selectedItems[0].getText();
					
					DicomTreeItem clickedTreeItem = treeRepository.lookUpDicomTreeItem(uid);
					//System.out.println(clickedTreeItem);
					broker.send(EventConstants.DICOMBROWSER_ITEM_SELECTION, clickedTreeItem);
				}
				
			}
		});
		tree.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				TreeItem[] selectedItems = tree.getSelection();
				if(selectedItems.length == 1){
					String uid = selectedItems[0].getText();
					
					DicomTreeItem clickedTreeItem = treeRepository.lookUpDicomTreeItem(uid);
					//System.out.println(clickedTreeItem);
					broker.send(EventConstants.DICOMBROWSER_ITEM_SELECTION, clickedTreeItem);
				}
			}
		});
	}
	
	@Inject
	@Optional
	public void getNotifiedOpenLocation(@UIEventTopic(EventConstants.FILE_OPEN_LOCATION) String location){
		System.out.println(location);
		
		DicomImporter importer = null;
		IJobManager manager = null;
		try {
			importer = new DicomImporter("Test", new File(location));
			manager = Job.getJobManager();
			
			MToolControl toolcontrol = (MToolControl) service.find("org.jmedikit.plugin.toolcontrol.status", app);
			
			final IProgressMonitor monitor = (IProgressMonitor) toolcontrol.getObject();
			
			ProgressProvider provider = new ProgressProvider() {	
				@Override
				public IProgressMonitor createMonitor(Job job) {
					return monitor;
				}
			};
			manager.setProgressProvider(provider);
			importer.schedule();
			treeRepository = importer.getTree();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Inject
	@Optional
	public void getNotifiedImportFinished(@UIEventTopic(EventConstants.FILE_IMPORT_FINISHED) boolean done){
		buildTree(tree, treeRepository);
	}
	
	private void buildTree(Tree guiTree, DicomTreeRepository dicomTree){
		DicomTreeItem root = dicomTree.getRoot();
		TreeItem guiRoot = new TreeItem(guiTree, SWT.DEFAULT);
		guiRoot.setText("/");
		guiRoot.setImage(rootIcon);
		buildTreeItems(guiRoot, root);
	}
	
	private void buildTreeItems(TreeItem parent, DicomTreeItem item){
		if(!item.isLeaf()){
			ArrayList<DicomTreeItem> children = item.getChildren();
			for(DicomTreeItem child : children){
				if(!child.isLeaf()){
					TreeItem childParent = new TreeItem(parent, SWT.DEFAULT);
					childParent.setText(child.getUid());
					setTreeIcon(childParent, child.getLevel());
					buildTreeItems(childParent, child);
				}
				else{	
					/////////////////////////////////////////////////////
					//Blaetter sollen im Baum nicht angezeigt werden   //
					//zum Anzeigen folgende Zeilen den Kommentar entfernen //
					/////////////////////////////////////////////////////
					
					//TreeItem guiItem = new TreeItem(parent, SWT.DEFAULT);
					//guiItem.setText(child.getUid());
					//setTreeIcon(guiItem, child.getLevel());
				}
			}
		}
		else{
			TreeItem guiItem = new TreeItem(parent, SWT.DEFAULT);
			guiItem.setText(item.getUid());
			setTreeIcon(guiItem, item.getLevel());
		}
	}
	
	private void setTreeIcon(TreeItem item, int level){
		switch (level) {
		case DicomTreeItem.TREE_ROOT_LEVEL:
			item.setImage(rootIcon);
			break;
		case DicomTreeItem.TREE_PATIENT_LEVEL:
			item.setImage(patientIcon);
			break;
		case DicomTreeItem.TREE_STUDY_LEVEL:
			item.setImage(studyIcon);
			break;
		case DicomTreeItem.TREE_SERIES_LEVEL:
			item.setImage(seriesIcon);
			break;
		case DicomTreeItem.TREE_OBJECT_LEVEL:
			item.setImage(objectIcon);
			break;
		default:
			break;
		}
	}
}
