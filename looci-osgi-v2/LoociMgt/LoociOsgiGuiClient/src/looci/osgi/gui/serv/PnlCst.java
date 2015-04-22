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
package looci.osgi.gui.serv;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class PnlCst {

	public static final int xquart0 = 10;
	public static final int xquart1 = 210;
	public static final int xquart2 = 410;
	public static final int xquart3 = 610;
	
	public static final int widthFull  = 780;
	public static final int widthHalf  = 380;
	public static final int widthQuart = 180;	
	
	public static final int HEIGHT = 20;
	public static final int BUFFERSPACE = 10;
	
	public static JButton addButton(JPanel panel, ActionListener listener, String text, String action, int x, int y, int width, int height){
		JButton temp = new JButton();
		temp.setText(text);
		temp.addActionListener(listener);
		temp.setActionCommand(action);
		temp.setBounds(x,y,width,height);
		panel.add(temp);
		return temp;
	}
	
	public static JCheckBox addCheckbox(JPanel panel, String text, int x, int y, int width){
		JCheckBox temp = new JCheckBox();
		temp.setText(text);
		temp.setBounds(x,y,width,PnlCst.HEIGHT);
		panel.add(temp);
		return temp;
	}
	
	public static JLabel addLabel(JPanel panel, String text, int x, int y, int width ){
        //the node type
        JLabel temp = new JLabel();
        temp.setText(text);
        temp.setHorizontalAlignment(SwingConstants.LEFT);
        temp.setHorizontalTextPosition(SwingConstants.LEFT);
        temp.setBounds(x, y, width, PnlCst.HEIGHT);
        panel.add(temp);
        return temp;
	}
	
	public static JTextField addTextField(JPanel panel, String text, int x, int y, int width ){
		JTextField temp = new JTextField();
		temp.setEditable(true);        
		temp.setText(text);
		temp.setHorizontalAlignment(SwingConstants.CENTER);
		temp.setPreferredSize(new Dimension(width, PnlCst.HEIGHT));    
		temp.setBounds(x, y, width, PnlCst.HEIGHT);
        panel.add(temp);
        return temp;    
	}

	public static JComboBox addComboBox(JPanel panel, String[] text, int x, int y, int width) {
		JComboBox temp = new JComboBox(text);
		temp.setSelectedIndex(0);
		temp.setBounds(x, y, width, PnlCst.HEIGHT);
		panel.add(temp);
		return temp;
	}

	public static JTable addTable(JPanel panel, String[] columnNames, String[][] data, int x, int y, int width, int height){
    	
        for(int i = 0 ; i < data.length; i ++){
        	data[i][0] = ""+i;
        	for(int j = 1 ; j < data[i].length; j++){
        		data[i][j] = "";
        	}
        }
        
        
		
		JTable temp = new JTable(data, columnNames);
    	temp.getColumnModel().getColumn(0).setPreferredWidth(30);
    	temp.setBounds(x, y, width, height);
        JScrollPane scrollPane1 = new JScrollPane(temp);
        scrollPane1.setBounds(x, y, width, height);
        temp.setFillsViewportHeight(true);
        panel.add(scrollPane1);		
		return temp;
	}
	
	public static void updateIndexedTable(JTable table, String[][] vals){
	
		for(int i = 0; i < vals.length  ;i ++){
			for(int j = 0 ; j < vals[i].length ; j++){
				table.setValueAt(vals[i][j], i,j);
			}
			for(int j = vals[i].length; j < table.getColumnCount();j++){
				table.setValueAt("",i,j);				
			}
		}
		for(int i = vals.length ; i<table.getRowCount();i++){
			for(int j =0 ; j < table.getColumnCount(); j++){
				table.setValueAt("",i,j);
			}
		}
		
	}
}
