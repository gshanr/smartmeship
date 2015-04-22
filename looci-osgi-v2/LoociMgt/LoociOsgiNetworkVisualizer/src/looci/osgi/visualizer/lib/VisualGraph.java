/**
LooCI Copyright (C) 2013 KU Leuven.
All rights reserved.

LooCI is an open-source software development kit for developing and maintaining networked embedded applications;
it is distributed under a dual-use software license model:

1. Non-commercial use:
Non-Profits, Academic Institutions, and Private Individuals can redistribute and/or modify LooCI code under the terms of the GNU General Public License version 3, as published by the Free Software Foundation
(http://www.gnu.org/licenses/gpl.html).

2. Commercial use:
In order to apply LooCI in commercial code, a dedicated software license must be negotiated with KU Leuven Research & Development.

Contact information:
  Administrative Contact: Sam Michiels, sam.michiels@cs.kuleuven.be
  Technical Contact:           Danny Hughes, danny.hughes@cs.kuleuven.be
Address:
  iMinds-DistriNet, KU Leuven
  Celestijnenlaan 200A - PB 2402,
  B-3001 Leuven,
  BELGIUM. 
 */
package looci.osgi.visualizer.lib;
/**
 * This class creates a visual display of the current component configuration inside a particular
 * node that is running LooCI middleware.
 *
 * @author  Based in Visualization functions at OpenCOM (java) made by Musbah Sagar 
 *          (Oxford Brookes University) and Paul Grace. Modified by Barry Porter.
 * @version 1.3.5
 */

import java.awt.*;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Display Frame for graph visualization.
 */
public class VisualGraph extends Frame{

	static final long serialVersionUID = 200010003000L;

	/* Reference to the panel */
	public GraphPanel panel = new GraphPanel();

	public String frameworkName;

	/* Create an instance of the graph. */
	public VisualGraph(String FrameworkTitle){
		try{
			jbInit(FrameworkTitle);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/* 
	 * Initialize the panel with the default values.
	 */
	private void jbInit(String FrameworkTitle) throws Exception{
		this.setTitle(FrameworkTitle);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {setVisible(false);}});
		this.setSize(new Dimension(600, 500));
		this.add(panel);
		panel.setSize(getSize());
		panel.start();
	}
}

