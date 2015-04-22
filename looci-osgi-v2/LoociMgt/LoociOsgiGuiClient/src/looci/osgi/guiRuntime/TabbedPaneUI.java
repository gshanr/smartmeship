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

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingUtilities;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that creates a Tabbed Panel UI
 *  setRequestFocusEnabled
 *  setFocusable
 * @author jef
 */
public class TabbedPaneUI extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//The actual tab component
	JTabbedPane tabbedPane;
	
	Map<JComponent, JScrollPane> scrollpanels = new HashMap<JComponent, JScrollPane>();
	
	/**
	 * Creates a new Tabcomponent and adds it to this panel
	 */
    public TabbedPaneUI() {
        super(new GridLayout());
        
        tabbedPane = new JTabbedPane();  
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        //Add the tabbed pane to this panel.
        add(tabbedPane);        
    }
    
    /**
     * Adds a new component with given title and subtext
     * 
     * Note:
     * Error from addComponent index out of bounds, may result from the fact
     * That the size of the added component is too small
     * Expected size currently 1024 800
     * 
     */
    public void addComponent(JComponent newPanel, String title, String subtext){
    	
    	
    	JScrollPane temp = new JScrollPane(newPanel);
    	temp.setLayout(new ScrollPaneLayout());
    	scrollpanels.put(newPanel, temp);
    	temp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    	TabAdder adder = new TabAdder(title, temp, subtext);
    	
    	SwingUtilities.invokeLater(adder);    	
    }
    
    
    private class TabAdder implements Runnable{

    	private String title;
    	private JScrollPane temp;
    	private String subText;
    	
    	public TabAdder(String title, JScrollPane pane, String subText){
    		this.title = title;
    		this.temp = pane;
    		this.subText = subText;
    	}
    	
		@Override
		public void run() {
    		tabbedPane.insertTab(title,null, temp,subText,tabbedPane.getTabCount());
		}
    	
    }
    
    /**
     * Removes the given panel
     */
    public void removeComponent(JComponent panel){
    	JScrollPane temp = scrollpanels.get(panel);
    	if(temp!=null){
        	tabbedPane.remove(temp);
    		scrollpanels.remove(panel);
    	}
    }
    
    public void setComponent(JComponent panel){
    	JScrollPane temp = scrollpanels.get(panel);
    	tabbedPane.setSelectedComponent(temp);
    }
}
