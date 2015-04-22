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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;

import looci.osgi.gui.serv.GuiInterface;
import looci.osgi.gui.serv.LoociPanel;
import looci.osgi.serv.util.XString;
import looci.osgi.servExt.mgt.ServiceClient;



public class CommandShell extends LoociPanel implements ActionListener {

	private static final long serialVersionUID = -3301612572937910662L;
	protected JTextField textField;
    protected JTextArea textArea;
    private final static String newline = "\n";
    
    private int currentLine = 0;
    
    private ArrayList<String> commands = new ArrayList<String>();
    private String tmp = "";
    
    private ServiceClient client;
    private boolean log = false;
    private FileWriter writer;
    
    public CommandShell(ServiceClient client) {
        super(new GridBagLayout());
        this.client = client;
        
        textField = new JTextField(60);
        textField.addActionListener(this);

        
        KeyStroke upstroke = KeyStroke.getKeyStroke(KeyEvent.VK_UP,0);
        KeyStroke downStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0);
        KeyStroke tabStroke = KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0);
        KeyStroke helpStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F1,0);
        
        Action upAction = new AbstractAction() {		
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doUpAction();
			}
		};
		
		Action downAction = new AbstractAction() {
			private static final long serialVersionUID = 6061813040884212955L;
			@Override
			public void actionPerformed(ActionEvent e) {
				doDownAction();
			}
		};
        
		Action tabAction = new AbstractAction(){
			private static final long serialVersionUID = 6061813040884212956L;
			@Override
			public void actionPerformed(ActionEvent e) {
				doTabAction();
			}
		};
		
		Action helpAction = new AbstractAction(){
			private static final long serialVersionUID = 6061813040884212956L;
			@Override
			public void actionPerformed(ActionEvent e) {
				doHelpAction();
			}
		};
		
        textField.getInputMap().put(upstroke, upAction);
        textField.getInputMap().put(downStroke, downAction);
        textField.getInputMap().put(tabStroke, tabAction);
        textField.getInputMap().put(helpStroke, helpAction);
        
        
        textField.setFocusTraversalKeysEnabled(false);
        
        textArea = new JTextArea(20, 60);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED,Font.PLAIN,11));

		textArea.setTabSize(30);
		textArea.setWrapStyleWord(true);
        
        MouseListener mouseListener = new MouseAdapter() {

            public void mousePressed(MouseEvent e) {}

            public void mouseClicked(MouseEvent e) {
                textField.requestFocusInWindow();
            }

            public void mouseReleased(MouseEvent e) {}
        };
        textArea.addMouseListener(mouseListener);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        //Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        
        add(textField, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        
        printLine(client.getWelcomeMessage());
        add(scrollPane, c);
    }

    public void actionPerformed(ActionEvent evt) {
        String text = textField.getText();        
        
        printLine(">>" + text);
        tmp = "";
        textField.setText(tmp);
        commands.add(text);
        currentLine = commands.size();
        
                
        printLine(client.process(text)+"\r\n");
        
        
        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
    
    
    
    ////////////////////////////////////////
    // Printing of text
    ///////////////////////////////////////

	public void printLine(String line) {
        textArea.append(line + newline + newline);
        if(writer != null){
        	
            try {
				writer.write(line+newline+ newline);
	            writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}   	
        }
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
	
	public void doTabAction(){
		List<String> options = client.getOptions(textField.getText());
		if(options.size() == 1){
			textField.setText(options.get(0));
		} else if (options.size() > 1){
			Collections.sort(options);
			printLine(XString.printString(options.toArray(new String[]{}),"\t"));
		}
	}


	private void doHelpAction() {
		String help = client.getHelp(textField.getText());
		if(help != null){
			printLine(help);
		}
	}
	
	@Override
	public void initPanel(GuiInterface handler) {
		try {
			if(log){
				writer = new FileWriter("looci/log.txt",true);
			}else{
				writer = null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void destroyPanel(GuiInterface handler) {
		if(writer != null){
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	


}
