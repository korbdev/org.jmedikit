package org.jmedikit.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class JMediKit implements Runnable{

	/*private JFrame frame;
	
	private Toolkit toolkit;
	private Dimension screensize;
	private Rectangle bounds;
	
	public JMediKit(String title){
		frame = new JFrame(title);
		
		toolkit = Toolkit.getDefaultToolkit();
		GraphicsConfiguration gc = frame.getGraphicsConfiguration();
		Insets s = toolkit.getScreenInsets(gc);
		
		screensize = toolkit.getScreenSize();
		bounds = gc.getBounds();
		
		bounds.x += s.left;
		bounds.y += s.top;
		bounds.width -= s.right;
		bounds.height -= s.bottom;
		
		screensize.setSize(bounds.width-bounds.x, bounds.height-bounds.y);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(screensize);

		frame.pack();
	}
	
	private void init(){
		//Container c = getContentPane();
		//System.out.println(c.getLayout() instanceof BorderLayout);
		JPanel panel = new JPanel();
		panel.setBackground(new Color(125, 0, 0));
		panel.setPreferredSize(new Dimension(200, screensize.height));
		JButton button = new JButton("Auswahl");
		panel.add(button);
		
		frame.setJMenuBar(new JMediKitMenubar());
		frame.setVisible(true);
		
		frame.add(panel, BorderLayout.WEST);
	}*/

	@Override
	public void run() {

	}
}
