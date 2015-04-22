package looci.osgi.chatApp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;


import looci.osgi.servExt.mgt.TextObserver;


public class ChatPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 189072747116162410L;
	
	protected JTextField textField;
    protected JTextArea textArea;
    private final static String newline = "\n";
    
    private int currentLine = 0;
    
    private ArrayList<String> commands = new ArrayList<String>();
    private String tmp = "";
	
    private TextObserver observer;
    private JFrame myFrame;
	
	public ChatPanel(TextObserver observer){
		 	super(new GridBagLayout());
		 	this.observer = observer;
	        
	        textField = new JTextField(60);
	        textField.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					textFieldAction();
				}
		
			});

	        
	        KeyStroke upstroke = KeyStroke.getKeyStroke(KeyEvent.VK_UP,0);
	        KeyStroke downStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0);
	        
	        Action upAction = new AbstractAction() {			
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent arg0) {
					doUpAction();
				}
			};
			
			Action downAction = new AbstractAction() {
				private static final long serialVersionUID = 1L;
							
				@Override
				public void actionPerformed(ActionEvent e) {
					doDownAction();
				}
			};
	        
	        textField.getInputMap().put(upstroke, upAction);
	        textField.getInputMap().put(downStroke, downAction);
	        
	        textArea = new JTextArea(20, 60);
	        textArea.setEditable(false);
	        textArea.setFont(new Font(Font.MONOSPACED,Font.PLAIN,11));
	        MouseListener mouseListener = new MouseAdapter() {

	            public void mousePressed(MouseEvent e) {}

	            public void mouseClicked(MouseEvent e) {
	                textField.requestFocusInWindow();
	            }

	            public void mouseReleased(MouseEvent e) {}
	        };
	        textArea.addMouseListener(mouseListener);
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

	   
    ////////////////////////////////////////
    // Printing of text
    ///////////////////////////////////////

	public void printLine(String line) {
        textArea.append(line + newline);
  
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
	
	private void textFieldAction() {
		
		String text = textField.getText();        
	        
		printLine(">>" + text);
	    tmp = "";
	    textField.setText(tmp);
	    commands.add(text);
	    currentLine = commands.size();

	    //Make sure the new text is visible, even if there
	    //was a selection in the text area.
	    textArea.setCaretPosition(textArea.getDocument().getLength());
	    
	     
	    observer.notifyTextMessage(text);
	}
	
	public void showFrame(){
		if(myFrame == null){
		     //Create and set up the window.
	        myFrame = new JFrame("chatPanel");
	        myFrame.setSize(1024, 800);
	        myFrame.setPreferredSize(new Dimension(1024,800));
	    
	        
	        //Add content to the window.
	        myFrame.add(this, BorderLayout.CENTER);
	        
	        //Display the window.

	        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        myFrame.pack();
		}

        myFrame.setVisible(true);
	}
	
	public void hideFrame(){
		myFrame.setVisible(false);
		myFrame.dispose();
		myFrame =null;
	}
}
