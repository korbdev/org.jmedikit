package org.jmedikit.gui;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jmedikit.lib.core.DicomObject;
import org.jmedikit.lib.core.ImageWindowInterpolation;
import org.jmedikit.lib.image.AbstractImage;

public class ImagePanel_dep implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	/*private File f;
	private JFrame frame;
	public void run(File f, JFrame frame) {
		// TODO Auto-generated method stub
		this.f = f;
		this.frame = frame;
		run();
	}

	@Override
	public void run() {
		System.out.println(f.getPath());
		DicomObject obj = new DicomObject(f);
		AbstractImage img = obj.getImage(0);

		JLabel picLabel = new JLabel(new ImageIcon(ImageWindowInterpolation.interpolateImage(img, img.getWindowCenter(), img.getWindowWidth(), 0, 255)));
		frame.add(picLabel);
	}*/

}
