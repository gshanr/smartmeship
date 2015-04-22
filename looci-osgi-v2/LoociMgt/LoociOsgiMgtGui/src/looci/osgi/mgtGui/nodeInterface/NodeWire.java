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
import java.net.UnknownHostException;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


import looci.osgi.gui.serv.GuiInterface;
import looci.osgi.gui.serv.LoociPanel;
import looci.osgi.gui.serv.PnlCst;
import looci.osgi.gui.serv.SelectionListener;
import looci.osgi.mgtGui.lib.NodeSelectionListener;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.servExt.appInfo.LoociNodeInfo;
import looci.osgi.servExt.mgt.ILoociAPI;




@SuppressWarnings("unused")
public class NodeWire extends LoociPanel implements ActionListener,SelectionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7617723493933121524L;
	ILoociAPI api;
	
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
	
	//wirelocal
    private JLabel wireLocalLabel;
    private JLabel wireLocalTypeLabel;
	private JTextField wireLocalTypeField;	
    private JLabel wireLocalSrcLabel;
	private JTextField wireLocalSrcField;
    private JLabel wireLocalDstLabel;
	private JTextField wireLocalDstField;	
	private JButton wireLocalBtn;
	
	//wireRemoteTo
    private JLabel wireRemoteToLabel;
    private JLabel wireRemoteToTypeLabel;
	private JTextField wireRemoteToTypeField;	
    private JLabel wireRemoteToSrcLabel;
	private JTextField wireRemoteToSrcField;
    private JLabel wireRemoteToDstNodeLabel;
	private JTextField wireRemoteToDstNodeField;	
	private JButton wireRemoteToBtn;
	private JButton wireRemoteToAllBtn;
	
	//wireRemoteFrom
    private JLabel wireRemoteFromLabel;
    private JLabel wireRemoteFromTypeLabel;
	private JTextField wireRemoteFromTypeField;	
    private JLabel wireRemoteFromSrcCmpLabel;
	private JTextField wireRemoteFromSrcCmpField;
    private JLabel wireRemoteFromDstCmpLabel;
	private JTextField wireRemoteFromDstCmpField;
    private JLabel wireRemoteToSrcNodeLabel;
	private JTextField wireRemoteFromSrcNodeField;	
	private JButton wireRemoteFromBtn;
	private JButton wireRemoteFromAllBtn;
		
	
	//wireRemoteFrom
	public NodeWire(ILoociAPI api, String[] nodeTypes){
		this.api = api;

		this.setPreferredSize(new Dimension(1024,800));
		
		int yIndex = PnlCst.BUFFERSPACE;		
    	this.setLayout(null);
		
    	//adress label
		nodeIpLabel = PnlCst.addLabel(this,	"Node IP address to connect:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
		nodeIpField = PnlCst.addTextField(this,"aaaa::11:22ff:fe33:4455", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
    	//name label
		nodeNameLabel = PnlCst.addLabel(this,	"Node name:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);                
        nodeNameField = PnlCst.addTextField(this,"021122fffe334455", PnlCst.xquart2, yIndex, PnlCst.widthHalf);		
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        //node owner 
		nodeOwnerLabel = PnlCst.addLabel(this,	"Name of the node owner:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);                 
        nodeOwnerField = PnlCst.addTextField(this,"myself", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        //node owner ip
		nodeOwnerIPLabel = PnlCst.addLabel(this,"IP address of the node owner:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
        nodeOwnerIPField = PnlCst.addTextField(this,"localhost", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
		
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        //the node type
        nodeTypeLabel = PnlCst.addLabel(this,"Type of the node to connect to:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
        
        
        nodetypeList = new JComboBox(nodeTypes);
        nodetypeList.setSelectedIndex(0);
        nodetypeList.addActionListener(this);
        nodetypeList.setBounds(PnlCst.xquart2, yIndex, PnlCst.widthHalf , PnlCst.HEIGHT);
        this.add(nodetypeList);
        
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        
        //WIRELOCAL
        
        wireLocalLabel = PnlCst.addLabel(this, "WIRE LOCAL", PnlCst.xquart0, yIndex, PnlCst.widthQuart);
        
        yIndex += PnlCst.HEIGHT;
        
        wireLocalTypeLabel = PnlCst.addLabel(this, "Type", PnlCst.xquart0, yIndex, PnlCst.widthQuart);
        wireLocalSrcLabel= PnlCst.addLabel(this, "Src Comp", PnlCst.xquart1, yIndex, PnlCst.widthQuart);
        wireLocalDstLabel = PnlCst.addLabel(this, "Dst Comp", PnlCst.xquart2, yIndex, PnlCst.widthQuart);;
  
        yIndex += PnlCst.HEIGHT;
        
        wireLocalTypeField = PnlCst.addTextField(this, "100",PnlCst.xquart0, yIndex, PnlCst.widthQuart);   	
        wireLocalSrcField= PnlCst.addTextField(this, "1",PnlCst.xquart1, yIndex, PnlCst.widthQuart);
        wireLocalDstField= PnlCst.addTextField(this, "2",PnlCst.xquart2, yIndex, PnlCst.widthQuart);	
        wireLocalBtn = PnlCst.addButton(this, this, "WireLocal", "wireLocal", PnlCst.xquart3, yIndex, PnlCst.widthQuart,PnlCst.HEIGHT);


        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE * 3;
        
    	//wireRemoteTo
        
        wireRemoteToLabel = PnlCst.addLabel(this, "WIRE REMOTE TO", PnlCst.xquart0, yIndex, PnlCst.widthQuart);
        yIndex += PnlCst.HEIGHT;        
        
        wireRemoteToDstNodeLabel = PnlCst.addLabel(this, "Destination node", PnlCst.xquart0, yIndex, PnlCst.widthQuart) ;
        yIndex += PnlCst.HEIGHT;        
        wireRemoteToDstNodeField = PnlCst.addTextField(this, "aaaa::11:22ff:fe33:4455",PnlCst.xquart0, yIndex, PnlCst.widthHalf);	
    	wireRemoteToBtn= PnlCst.addButton(this, this, "WireRemoteTo", "wireRemoteTo", PnlCst.xquart3, yIndex, PnlCst.widthQuart,PnlCst.HEIGHT);
        
    	yIndex += PnlCst.HEIGHT;    	
        wireRemoteToTypeLabel= PnlCst.addLabel(this, "Type", PnlCst.xquart0, yIndex, PnlCst.widthQuart);
        wireRemoteToSrcLabel = PnlCst.addLabel(this, "Src Comp", PnlCst.xquart1, yIndex, PnlCst.widthQuart);

        yIndex += PnlCst.HEIGHT;
        
    	wireRemoteToTypeField = PnlCst.addTextField(this, "100",PnlCst.xquart0, yIndex, PnlCst.widthQuart);	
    	wireRemoteToSrcField = PnlCst.addTextField(this, "1",PnlCst.xquart1, yIndex, PnlCst.widthQuart);
      
    	wireRemoteToAllBtn= PnlCst.addButton(this, this, "WireRemoteToAll", "wireRemoteToAll", PnlCst.xquart3, yIndex, PnlCst.widthQuart,PnlCst.HEIGHT);
    	

        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE * 3;
    	
    	//wireRemoteFrom
        
        wireRemoteToLabel = PnlCst.addLabel(this, "WIRE REMOTE FROM", PnlCst.xquart0, yIndex, PnlCst.widthQuart);
        
        yIndex += PnlCst.HEIGHT;
        
    	wireRemoteToSrcNodeLabel = PnlCst.addLabel(this, "Source node", PnlCst.xquart0, yIndex, PnlCst.widthQuart) ;
    	wireRemoteFromSrcCmpLabel = PnlCst.addLabel(this, "Src Comp", PnlCst.xquart2, yIndex, PnlCst.widthQuart);
    	
        yIndex += PnlCst.HEIGHT;
    	wireRemoteFromSrcNodeField = PnlCst.addTextField(this, "aaaa::11:22ff:fe33:4455",PnlCst.xquart0, yIndex, PnlCst.widthHalf);	
    	wireRemoteFromSrcCmpField = PnlCst.addTextField(this, "1",PnlCst.xquart2, yIndex, PnlCst.widthQuart);
     	wireRemoteFromBtn= PnlCst.addButton(this, this, "WireRemoteFrom", "wireRemoteFrom", PnlCst.xquart3, yIndex, PnlCst.widthQuart,PnlCst.HEIGHT);

        yIndex += PnlCst.HEIGHT;
    	
    	wireRemoteFromTypeLabel = PnlCst.addLabel(this, "Type", PnlCst.xquart0, yIndex, PnlCst.widthQuart);
    	wireRemoteFromDstCmpLabel = PnlCst.addLabel(this, "Dst Comp", PnlCst.xquart1, yIndex, PnlCst.widthQuart);
    	
        yIndex += PnlCst.HEIGHT;
    	
    	wireRemoteFromTypeField  = PnlCst.addTextField(this, "100",PnlCst.xquart0, yIndex, PnlCst.widthQuart);	
    	wireRemoteFromDstCmpField = PnlCst.addTextField(this, "1",PnlCst.xquart1, yIndex, PnlCst.widthQuart);
    	
    	wireRemoteFromAllBtn= PnlCst.addButton(this, this, "WireRemoteFromAll", "wireRemoteFromAll", PnlCst.xquart3, yIndex, PnlCst.widthQuart,PnlCst.HEIGHT);
	}

	
	@Override
	public void actionPerformed(ActionEvent action) {
		try{
			if(action.getActionCommand().equals("wireLocal")){			
				String nodeID = nodeIpField.getText();
				
				short eventType = Short.parseShort(wireLocalTypeField.getText());
				byte srcComp = Byte.parseByte(wireLocalSrcField.getText());
				byte dstComp = Byte.parseByte(wireLocalDstField.getText());
				api.wireLocal(eventType, srcComp, dstComp, nodeID);
			}
			if(action.getActionCommand().equals("wireRemoteTo")){			
				String nodeID = nodeIpField.getText();
				
				short eventType = Short.parseShort(wireRemoteToTypeField.getText());
				byte srcComp = Byte.parseByte(wireRemoteToSrcField.getText());
				String dstNode = wireRemoteToDstNodeField.getText();
				api.wireTo(eventType, srcComp, nodeID, dstNode);
				
			}
			if(action.getActionCommand().equals("wireRemoteFrom")){			
				String nodeID = nodeIpField.getText();
				
				short eventType = Short.parseShort(wireRemoteFromTypeField.getText());
				byte srcComp = Byte.parseByte(wireRemoteFromSrcCmpField.getText());
				byte dstComp = Byte.parseByte(wireRemoteFromDstCmpField.getText());
				String srcNode = wireRemoteFromSrcNodeField.getText();
				api.wireFrom(eventType, srcComp, srcNode, dstComp, nodeID);
			}
		} catch (LoociManagementException e) {
			e.printStackTrace();
		} catch (UnknownHostException e){
			e.printStackTrace();
		}


	}

	public void setNodeInfo(LoociNodeInfo info) {
		nodeIpField.setText(info.getNodeIP());
		nodeNameField.setText(info.getNodeMac());
		nodeOwnerIPField.setText(info.getFederationIp());
		nodeOwnerField.setText(info.getFederationName());
		nodetypeList.setSelectedItem(info.getNodeType());
	}
	
	public void notifySelection(String selection, Object object){
		if(selection.equals(SelectionListener.SELECT_NODE)){
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
