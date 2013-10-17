package org.jmedikit.gui;

import java.awt.EventQueue;
import java.io.File;

//import org.apache.log4j.BasicConfigurator;

import org.jmedikit.lib.io.DicomImporter;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String ct = "CT-MONO2-16-brain";
		String cr = "CR-MONO1-10-chest";
		String ct8 = "CT-MONO2-8-abdo";
		String mr = "MR-MONO2-12-angio-an1";
		String dc = "dicom2.dcm";
		String fr = "0004.dcm";
		String rgb = "3639YPUT";
		String tet = "IM-0001-0010.dcm";
		String m = "Image.2007.09.11.12.31.37.421 2.dcm";
		//BasicConfigurator.configure();
		
		//String path = "Samples/";
		String path = "C:\\Users\\rkorb\\Downloads\\CEREBRIX\\";
		DicomImporter imp = new DicomImporter(new File(path));
		imp.printImportedLocation();
		//JMediKit kit = new JMediKit(path);
		//JMediKitController controller = new JMediKitController();
		//EventQueue.invokeLater(kit);
	}

}
