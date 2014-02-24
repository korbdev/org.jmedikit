 
package org.jmedikit.plugin.gui;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.tools.services.IResourcePool;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.jmedikit.plugin.gui.events.EventConstants;
import org.jmedikit.plugin.util.ImageProvider;

public class DicomTagView {
	
	private TableViewer viewer;
	
	private Table table;
	
	@Inject
	private IResourcePool pool;
	
	@Inject
	public DicomTagView() {

	}
	
	/**
	 * Erzeugt die Tabelle für die Ausgabe der DICOM-Tags
	 * 
	 * @param parent
	 */
	@PostConstruct
	public void createGui(Composite parent){
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		viewer = new TableViewer(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		
		createColumns();
		
		table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true); 

		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(ArrayContentProvider.getInstance());
	}
	
	@Inject
	@Optional
	public void getNotifiedDicomTreeSelection(@UIEventTopic(EventConstants.DICOM_TAGS_CHANGED) ArrayList<String[]> tags){
		//list.removeAll();
		table.removeAll();
		viewer.setInput(tags);
	}

	private void createColumns(){
		TableViewerColumn colTagname = new TableViewerColumn(viewer, SWT.NONE);
		colTagname.getColumn().setWidth(400);
		colTagname.getColumn().setText("Tagname");
		
		colTagname.getColumn().setImage(pool.getImageUnchecked(ImageProvider.TABLE_0));
		colTagname.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String[] item = (String[]) element;
				return item[0];
			}
		});
		
		
		TableViewerColumn colTag = new TableViewerColumn(viewer, SWT.NONE);
		colTag.getColumn().setWidth(100);
		colTag.getColumn().setText("Tag");
		colTag.getColumn().setImage(pool.getImageUnchecked(ImageProvider.TABLE_1));
		colTag.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String[] item = (String[]) element;
				return item[1];
			}
		});
		
		TableViewerColumn colVR = new TableViewerColumn(viewer, SWT.NONE);
		colVR.getColumn().setWidth(100);
		colVR.getColumn().setText("VR");
		colVR.getColumn().setImage(pool.getImageUnchecked(ImageProvider.TABLE_2));
		colVR.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String[] item = (String[]) element;
				return item[2];
			}
		});
		
		TableViewerColumn colLen = new TableViewerColumn(viewer, SWT.NONE);
		colLen.getColumn().setWidth(100);
		colLen.getColumn().setText("Länge");
		colLen.getColumn().setImage(pool.getImageUnchecked(ImageProvider.TABLE_3));
		colLen.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String[] item = (String[]) element;
				return item[5];
			}
		});
		
		TableViewerColumn colValue = new TableViewerColumn(viewer, SWT.NONE);
		colValue.getColumn().setWidth(400);
		colValue.getColumn().setText("Wert");
		colValue.getColumn().setImage(pool.getImageUnchecked(ImageProvider.TABLE_4));
		colValue.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String[] item = (String[]) element;
				return item[4];
			}
		});
		
		TableViewerColumn colVM = new TableViewerColumn(viewer, SWT.NONE);
		colVM.getColumn().setWidth(100);
		colVM.getColumn().setText("VM");
		colVM.getColumn().setImage(pool.getImageUnchecked(ImageProvider.TABLE_5));
		colVM.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String[] item = (String[]) element;
				return item[3];
			}
		});
		
	}
}