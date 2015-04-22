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
package looci.osgi.app.buttonSender;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ButtonSenderWindow  extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4874001954009573414L;
	
	private ButtonSenderComponent comp;
	private JTextArea textArea;
	
	public ButtonSenderWindow(ButtonSenderComponent component){
		this.comp = component;
		
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        
        JButton button = new JButton("send button press");
        button.addActionListener(this);
        button.setActionCommand("button");
        
        JLabel label = new JLabel("temperature reading to send");
        textArea = new JTextArea("0");
        JButton tempButton = new JButton("send temp event"); 
        tempButton.addActionListener(this);
        tempButton.setActionCommand("temp");
        
        panel.add(button);
        panel.add(label);
        panel.add(textArea);
        panel.add(tempButton);
        

        getContentPane().add(panel,java.awt.BorderLayout.SOUTH);
        
        pack();
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getActionCommand().equals("button")){
			comp.buttonPressed();
		} else{
			try{
				byte temp = Byte.parseByte(textArea.getText());
				comp.tempReadingDone(temp);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
