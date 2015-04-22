/**
 * LooCI Copyright (C) 2013 KU Leuven.
 * All rights reserved.
 *
 * LooCI is an open-source software development kit for developing and 
 * maintaining networked embedded applications;
 * it is distributed under a dual-use software license model:
 *
 * 1. Non-commercial use:
 * Non-Profits, Academic Institutions, and Private Individuals can redistribute 
 * and/or modify LooCI code under the terms of the GNU General Public License 
 * version 3, as published by the Free Software Foundation
 * (http://www.gnu.org/licenses/gpl.html).
 *
 * 2. Commercial use:
 * In order to apply LooCI in commercial code, a dedicated software license must 
 * be negotiated with KU Leuven Research & Development.
 *
 * Contact information:
 *  Administrative Contact: Sam Michiels, sam.michiels@cs.kuleuven.be
 *  Technical Contact:           Danny Hughes, danny.hughes@cs.kuleuven.be
 * Address:
 *  iMinds-DistriNet, KU Leuven
 *  Celestijnenlaan 200A - PB 2402,
 *  B-3001 Leuven,
 *  BELGIUM. 
 **/

package looci.osgi.tester;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author klaas
 */
public class GUI extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = -525572542957508995L;
	private JTextArea area;
    private JScrollPane scrollPane;
    
    private JButton startButton;
    //private JButton stopButton;
    
    
    public GUI(Activator activator) {
        initGUI();
    }
    

    public final void initGUI() {
        JPanel panel = new JPanel(new GridLayout());

        area = new JTextArea("");
        area.setEditable(false);
        area.setLineWrap(true);
        area.append("[LooCI Standard Test] Initiated.\n");
        
        scrollPane = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(550, 700));
        
    
        
        panel.add(scrollPane);
        
        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        
        
        add(panel);
        pack();
        setTitle("LooCI Standard Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    public void print(String string) {
        area.append(string + "\n");
        area.setCaretPosition(area.getDocument().getLength()); // autoscrolling
    }

}
