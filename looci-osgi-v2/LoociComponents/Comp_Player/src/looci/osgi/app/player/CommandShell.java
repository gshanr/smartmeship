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
package looci.osgi.app.player;


import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import looci.osgi.gui.serv.LoociPanel;
import looci.osgi.gui.serv.PnlCst;
import looci.osgi.serv.util.XString;






public class CommandShell extends LoociPanel implements ActionListener {

	private static final long serialVersionUID = -3301612572937910662L;
	protected JTextField textField;
    protected JTextArea textArea;
    private JButton playCommandButton;
    private final static String newline = "\n";
    
    private int currentLine = 0;
    
    private ArrayList<String> commands = new ArrayList<String>();
    private String tmp = "";
    
    private boolean playing = false;
    private LoociTonePlayerComponent comp;
    
    
    public CommandShell(LoociTonePlayerComponent component) {
        super(new GridBagLayout());
        this.comp = component;
        textField = new JTextField(60);
        textField.addActionListener(this);
        
        
        KeyStroke upstroke = KeyStroke.getKeyStroke(KeyEvent.VK_UP,0);
        KeyStroke downStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0);
        
        
        Action upAction = new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doUpAction();
			}
		};
		
		Action downAction = new AbstractAction() {
						
			@Override
			public void actionPerformed(ActionEvent e) {
				doDownAction();
			}
		};
        
        textField.getInputMap().put(upstroke, upAction);
        textField.getInputMap().put(downStroke, downAction);
        
        textField.addKeyListener(new KeyListener() {
			
        	String keys = "qwertyuiopasdfghjklzxcvbnm";
        	
			@Override
			public void keyTyped(KeyEvent event) {

				if(playing){
					
					int index = keys.indexOf(event.getKeyChar());
					System.out.println("typed index"+ index);
					comp.sendKey(index);
				}
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        playCommandButton = new JButton("start play");
        
        playCommandButton.setText("startPlay");
        playCommandButton.addActionListener(this);
        playCommandButton.setActionCommand("togglePlay");
        playCommandButton.setBounds(PnlCst.xquart0, 90 , PnlCst.widthHalf , PnlCst.HEIGHT);
        this.add(playCommandButton);
        
        
        textArea = new JTextArea(20, 60);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        //Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        
        add(textField, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(scrollPane, c);
    }

    public void actionPerformed(ActionEvent evt) {
    	


        
    	if(evt.getActionCommand().equals("togglePlay")){
    		if(playing){
    			playCommandButton.setText("startPlay");
                textArea.append(">>" + "stop playing, can enter commands" + newline);    		
                playing = false;
    		} else{
    			playCommandButton.setText("stopPlay");
                textArea.append(">>" + "start playing, enter keys" + newline);    		
                playing = true;
    		}
    	} else{
        	
            String text = textField.getText();        
            
            textArea.append(">>" + text + newline);
            tmp = "";
            textField.setText(tmp);
            commands.add(text);
            currentLine = commands.size();
            
            String[] command = XString.split(text, " ");
            if(command[0].equals("setLen")){
            	int keyLen = Integer.parseInt(command[1]);
            	comp.setLen(keyLen);            	
            }
            
    	}
        
        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
    
    
    
    ////////////////////////////////////////
    // Printing of text
    ///////////////////////////////////////

	public void printLine(String line) {
        textArea.append("<<" + line + newline+newline);
	}
	
	public void doUpAction(){
		if(currentLine == commands.size()){
			tmp = textField.getText();
		} 
		if(currentLine > 0){
			currentLine -= 1;
		}		
		if(currentLine >= 0 && currentLine < commands.size()){
			textField.setText(commands.get(currentLine));
		}
	}

	
	public void doDownAction(){
		if(currentLine < commands.size()){
			currentLine += 1;
		} 
		if(currentLine < commands.size()){
			textField.setText(commands.get(currentLine));
		}else{
			textField.setText(tmp);
		}
	}


}
