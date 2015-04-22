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
package looci.osgi.mgtGui.nodeInterface;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


import looci.osgi.gui.serv.GuiInterface;
import looci.osgi.gui.serv.LoociPanel;
import looci.osgi.gui.serv.PnlCst;
import looci.osgi.gui.serv.SelectionListener;
import looci.osgi.mgtGui.lib.NodeSelectionListener;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.constants.LoociRuntimes;
import looci.osgi.servExt.appInfo.LoociNodeInfo;
import looci.osgi.servExt.mgt.IDeployerAPI;
import looci.osgi.servExt.mgt.IDeploymentAPI;
import looci.osgi.servExt.mgt.ILoociAPI;
import looci.osgi.servExt.mgt.TextObserver;





@SuppressWarnings("unused")
public class NodeDeployPanel extends LoociPanel implements ActionListener,SelectionListener, TextObserver{

	private static final long serialVersionUID = -6261538458020879105L;
	ILoociAPI api;
	IDeployerAPI deplApis;

	private JLabel msgInfoLabel;
    private JTable textArea;
    private String[][] debugInfo;
	
    private JLabel nodeIpLabel;
	private JTextField nodeIpField;
	
    private JLabel nodeNameLabel;
	private JTextField nodeNameField;
	
    private JLabel nodeOwnerLabel;
	private JTextField nodeOwnerField;
	
    private JLabel nodeOwnerIPLabel;
	private JTextField nodeOwnerIPField;
	
	private JLabel nodeTypeLabel;
	private JComboBox nodetypeList;
		
	private JLabel componentLabel;
	private JTextField componentField;
	private JButton componentButton;

	private JButton deployButton;
	

	private File defaultFolder;
	
	public NodeDeployPanel(ILoociAPI api, IDeployerAPI deplApis) {
		this.api = api;		
		this.deplApis = deplApis;

		String[] names = LoociRuntimes.RUNTIME_NAMES;
		int yIndex = PnlCst.BUFFERSPACE;		
    	this.setLayout(null);
		this.setPreferredSize(new Dimension(1024,800));
		
		defaultFolder = new File("looci/comps/");
		
		int textAreaHeight = 200;
		
		//status label
	    msgInfoLabel = PnlCst.addLabel(this, "info: ", PnlCst.xquart0, yIndex,	PnlCst.widthQuart);
	    debugInfo = new String[10][3];
	    textArea = PnlCst.addTable(this, new String[]{"index","time","info"}, debugInfo, PnlCst.xquart0, yIndex, PnlCst.widthFull, textAreaHeight);
	    setStatus("started");
		
		yIndex += textAreaHeight + PnlCst.BUFFERSPACE;
	    
    	//adress label
		nodeIpLabel = new javax.swing.JLabel();
		nodeIpLabel.setText("Node IP address to connect:");
		nodeIpLabel.setHorizontalAlignment(SwingConstants.LEFT);
		nodeIpLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		nodeIpLabel.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthHalf , PnlCst.HEIGHT);
        this.add(nodeIpLabel);
                
        nodeIpField = new javax.swing.JTextField();
        nodeIpField.setEditable(true);        
        nodeIpField.setText(LoociConstants.ADDR_LOCAL);
        nodeIpField.setHorizontalAlignment(SwingConstants.CENTER);
        nodeIpField.setPreferredSize(new Dimension(PnlCst.widthHalf, PnlCst.HEIGHT));    
        nodeIpField.setBounds(PnlCst.xquart2, yIndex, PnlCst.widthHalf , PnlCst.HEIGHT);
        this.add(nodeIpField);
		
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
    	//name label
		nodeNameLabel = new javax.swing.JLabel();
		nodeNameLabel.setText("Node name:");
		nodeNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		nodeNameLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		nodeNameLabel.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthHalf , PnlCst.HEIGHT);
        this.add(nodeNameLabel);
                
        nodeNameField = new javax.swing.JTextField();
        nodeNameField.setEditable(true);        
        nodeNameField.setText("021122fffe334455");
        nodeNameField.setHorizontalAlignment(SwingConstants.CENTER);
        nodeNameField.setPreferredSize(new Dimension(PnlCst.widthHalf, PnlCst.HEIGHT));    
        nodeNameField.setBounds(PnlCst.xquart2, yIndex, PnlCst.widthHalf , PnlCst.HEIGHT);
        this.add(nodeNameField);
		
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        //node owner label
        //node owner 
		nodeOwnerLabel = PnlCst.addLabel(this,	"Name of the node owner:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);                 
        nodeOwnerField = PnlCst.addTextField(this,"myself", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        //node owner ip
		nodeOwnerIPLabel = PnlCst.addLabel(this,"IP address of the node owner:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
        nodeOwnerIPField = PnlCst.addTextField(this,"localhost", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
		
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        //the node type
        nodeTypeLabel = new javax.swing.JLabel();
        nodeTypeLabel.setText("Type of the node to connect to:");
        nodeTypeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        nodeTypeLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        nodeTypeLabel.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthHalf , PnlCst.HEIGHT);
        this.add(nodeTypeLabel);
        
        
        nodetypeList = new JComboBox(names);
        nodetypeList.setSelectedIndex(0);
        nodetypeList.addActionListener(this);
        nodetypeList.setBounds(PnlCst.xquart2, yIndex, PnlCst.widthHalf , PnlCst.HEIGHT);
        this.add(nodetypeList);
        
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        //component file screen
       
    	//adress label
        componentLabel = new javax.swing.JLabel();
        componentLabel.setText("Component XML file:");
        componentLabel.setHorizontalAlignment(SwingConstants.LEFT);
        componentLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        componentLabel.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthHalf , PnlCst.HEIGHT);
        this.add(componentLabel);
                
        componentField = new javax.swing.JTextField();
        componentField.setEditable(true);        
        componentField.setText("");
        componentField.setHorizontalAlignment(SwingConstants.CENTER);
        componentField.setPreferredSize(new Dimension(PnlCst.widthHalf, PnlCst.HEIGHT));    
        componentField.setBounds(PnlCst.xquart2, yIndex, PnlCst.widthHalf , PnlCst.HEIGHT);
        this.add(componentField);
		
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        
        //comp
        
        
        //deployment and select button button
        componentButton = new javax.swing.JButton();
        componentButton.setText("Select component");
        componentButton.addActionListener(this);
        componentButton.setActionCommand("select");
        componentButton.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthHalf , PnlCst.HEIGHT);
        this.add(componentButton);
        
        
        
        deployButton = new javax.swing.JButton();
        deployButton.setText("deploy selected component");
        deployButton.addActionListener(this);
        deployButton.setActionCommand("deploy");
        deployButton.setBounds(PnlCst.xquart2, yIndex, PnlCst.widthHalf , PnlCst.HEIGHT);
        this.add(deployButton);
        
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
	}
	


	@Override
	public void actionPerformed(ActionEvent action) {
		if(action.getActionCommand().equals("select")){			
			System.out.println("selecting file");
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(defaultFolder);
			int returnVal = fc.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				if(file.isFile()){
					componentField.setText(file.getAbsolutePath());
					setStatus("component selected");
					try{
						defaultFolder = file.getParentFile();
					} catch (Exception e){}
					
				}
			}
		}
		
		if(action.getActionCommand().equals("deploy")){			
			System.out.println("deploying item");
			String filename = componentField.getText();
			if(!filename.equals("")){
				LoociNodeInfo info = getNodeInfo();
				
				String nodeType = (String)nodetypeList.getSelectedItem();
				
				System.out.println("nodetype: "+nodeType);
				
				byte b = 0;
				
				try {
					b = deplApis.deploy(nodeType,filename, info,this,null); 
					setStatus("deployed component with id:"+b);
				} catch (Exception e) {

					setStatus("failed to deploy component: "+e.getMessage());
				}
				
			}
		}
	}
	
	public LoociNodeInfo getNodeInfo(){
		LoociNodeInfo info = new LoociNodeInfo();
		info.setNodeIP(nodeIpField.getText());
		info.setFederationIP(nodeOwnerIPField.getText());	
		info.setFederationName(nodeOwnerField.getText());
		info.setNodeMac(nodeNameField.getText());
		return info;
	}
	
	public void setNodeInfo(LoociNodeInfo info){
		nodeIpField.setText(info.getNodeIP());
		nodeNameField.setText(info.getNodeMac());
		nodeOwnerIPField.setText(info.getFederationIp());
		nodeOwnerField.setText(info.getFederationName());
		nodetypeList.setSelectedItem(info.getNodeType());
	}
	

	public void setStatus(String text){
		Date todaysDate = new java.util.Date();
		SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss");
		String time = formatter.format(todaysDate);
		for(int i = debugInfo.length-1 ; i > 0; i--){
			debugInfo[i][1] = debugInfo[i-1][1];
			debugInfo[i][2] = debugInfo[i-1][2];
		}
		debugInfo[0][1] = time;
		debugInfo[0][2] = text;
		
		PnlCst.updateIndexedTable(textArea, debugInfo);
	}

	
	
	@Override
	public void notifyTextMessage(String text) {
		setStatus(text);
	}



	@Override
	public void notifySelection(String selection, Object object) {
		if(selection.equals(SELECT_NODE)){
			setNodeInfo((LoociNodeInfo)object);
		}
	}


	public void initPanel(GuiInterface handler){
		handler.addSelectionListener(SELECT_NODE, this);
	}
	
	public void destroyPanel(GuiInterface handler){
		handler.removeSelectionListener(SELECT_NODE, this);
	}
	
	
}
