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
package looci.osgi.guiRuntime;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import looci.osgi.gui.serv.GuiInterface;
import looci.osgi.gui.serv.LoociPanel;
import looci.osgi.gui.serv.SelectionListener;




/**
 * A class that handles all User Interface interactions
 * It creates a user interface when created.
 * It removes the user interface when end() is called
 * 
 * @author jef
 */
public class GuiHandler implements GuiInterface {

	
	private static final long serialVersionUID = -8682004977594728915L;
	//The tabbed UI that is shown
	private TabbedPaneUI tabUI;
	//The frame that is shown
	private JFrame frame;
	
	private HashMap<String, ArrayList<SelectionListener>> listeners;
	
	public GuiHandler(){
		
		listeners = new HashMap<String, ArrayList<SelectionListener>>();
		 
		//pen the frame
        openFrame("LooCI Management Console");


	}
	
	/**
	 * Open the frame with a tabUI and make it visible
	 */
	private void openFrame(String title){
        //Create and set up the window.
        frame = new JFrame(title);
        frame.setSize(1024, 800);
        frame.setPreferredSize(new Dimension(1024,800));

		//Creating and opening a TabUI
        tabUI = new TabbedPaneUI();       
        
        //Add content to the window.
        frame.add(tabUI, BorderLayout.CENTER);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
	}
	
	/**
	 * Close the frame and dispose of the frame and its elements
	 */
	private void closeFrame(){
		//make invisible
		frame.setVisible(false);
		//clean up frame and subcomponents
		frame.dispose();
		//cleanup of variables
		frame = null;
		tabUI = null;
	}
	
	/**
	 * Add a component to the GUI
	 * Currently no checks in place
	 */
	@Override
	public void addComponent(LoociPanel newPanel, String title, String subtext) {
		newPanel.initPanel(this);
		tabUI.addComponent(newPanel, title, subtext);
	}

	/**
	 * Remove a component to the GUI
	 * Currently no checks in place
	 */
	@Override
	public void removeComponent(LoociPanel panel) {
		panel.destroyPanel(this);
		tabUI.removeComponent(panel);
	}

	public void showError(int errorcode, String errorMsg){
		JOptionPane.showMessageDialog(frame,
			    errorMsg,
			    "Error "+ errorcode,
			    JOptionPane.ERROR_MESSAGE);
	}
	
	
	/**
	 * End the gui
	 * Removes the interface in the registry
	 * Closes the GUI frame and disposes of it
	 */
	public void end() {
		//close frame
		closeFrame();
	}

	@Override
	public void selectComponent(LoociPanel panel) {
		tabUI.setComponent(panel);
	}

	@Override
	public void addSelectionListener(String listenerId,
			SelectionListener listener) {
		ArrayList<SelectionListener> listenerList = listeners.get(listenerId);
		if(listenerList!=null){
			listenerList.add(listener);
		} else{
			listenerList = new ArrayList<SelectionListener>();
			listenerList.add(listener);
			listeners.put(listenerId, listenerList);
		}
	}

	@Override
	public void removeSelectionListener(String listenerId,
			SelectionListener listener) {
		ArrayList<SelectionListener> listenerList = listeners.get(listenerId);
		if(listenerList!=null){
			listenerList.remove(listener);
		}
	}

	@Override
	public void notifySelectionListener(String listenerId, Object object) {
		ArrayList<SelectionListener> listenerList = listeners.get(listenerId);
		if(listenerList!=null){
			for(int i = 0 ; i < listenerList.size() ; i ++){
				try{
					listenerList.get(i).notifySelection(listenerId, object);
				} catch (Exception e) {
					
				}
			}
		}
	}

}
