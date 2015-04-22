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
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;


import looci.osgi.gui.serv.GuiInterface;
import looci.osgi.gui.serv.LoociPanel;
import looci.osgi.gui.serv.PnlCst;
import looci.osgi.gui.serv.SelectionListener;
import looci.osgi.mgtGui.lib.InstanceSelectionListener;
import looci.osgi.mgtGui.lib.NodeSelectionListener;
import looci.osgi.serv.bindings.LocalBinding;
import looci.osgi.serv.bindings.RemoteFromBinding;
import looci.osgi.serv.bindings.RemoteToBinding;
import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.constants.LoociRuntimes;
import looci.osgi.serv.util.Utils;
import looci.osgi.servExt.appInfo.ComponentInfo;
import looci.osgi.servExt.appInfo.InstanceInfo;
import looci.osgi.servExt.appInfo.LoociNodeInfo;
import looci.osgi.servExt.mgt.ILoociAPI;





@SuppressWarnings("unused")
public class NodePanel extends LoociPanel implements ActionListener,SelectionListener{

	private static final int MAX_NR_CODEBASES = 8;
	private static final int MAX_NR_COMPONENTS = 15;
	private static final int MAX_NR_WIRINGS = 8;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8133621445426419852L;

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
	
	
	private ILoociAPI api;
	
	

	private JButton btnInspectNodeType;

	
	String[] columnNames = {"Nr",
			"CodebaseID",
            "Type"};
	private String[][] codebaseTableData = new String[MAX_NR_CODEBASES][3];
	private JTable codebaseTable;

	

	private JButton btnInspectComponent;
	
	
	
	

	private JButton btnInstantiateComp;
	private JButton btnremoveComp;
	
	String[] componentTableNames = {"Nr",
			"ComponentID",
			"CodebaseID",
            "Receptacles",
            "Interfaces",
           	"State"
	};
	private String[][] componentTableData = new String[MAX_NR_COMPONENTS][6];
	private JTable componentTable;
	

	private JButton btnInspectInstance;
	
	private JButton btnInspectInstanceState;
	private JButton btnActivateInst;
	private JButton btnDeactivateInst;
	private JButton btnDestroyInst;
		
	private JButton btnSetSourceInstance;
	private JButton btnSetDestinationInstance;
	private JButton btnDoWire;
	private JButton btnSelectInstance;
	
	private JButton btnInspectWireLocal;
	private JButton btnInspectWireRemoteTo;
	private JButton btnInspectWireRemoteFrom;
	
	private JButton btnRemoveWireLocal;
	private JButton btnRemoveWireRemoteTo;
	private JButton btnRemoveWireRemoteFrom;
	
	private GuiInterface manager;
	
	String[] columnWireLocalNames = {"Nr",
			"EventType",
            "source comp",
            "destination comp"};
	private String[][] dataWireLocal = new String[MAX_NR_WIRINGS][4];
	private JTable wireLocalTable;
	
	String[] columnWireRemoteToNames = {"Nr",
            "Event type",
            "source comp",
			"Destination node"};
	private String[][] dataWireRemoteTo = new String[MAX_NR_WIRINGS][4];
	private JTable wireRemoteToTable;
	
	String[] columnWireRemoteFromNames = {"Nr",
            "type",
			"src node",
            "src comp",
            "dst comp"};
	private String[][] dataWireRemoteFrom = new String[MAX_NR_WIRINGS][5];
	private JTable wireRemoteFromTable;
	
	private NodeWireFrame wireFrame;
	
	private void initGui(){
		
		int yIndex = PnlCst.BUFFERSPACE;		
    	this.setLayout(null);
    	
		wireFrame = new NodeWireFrame(api);
    	//adress label
		nodeIpLabel = PnlCst.addLabel(this,	"Node IP address to connect:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
		nodeIpField = PnlCst.addTextField(this,LoociConstants.ADDR_LOCAL, PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
    	//name label
		nodeNameLabel =  PnlCst.addLabel(this,	"Node name:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);                
        nodeNameField = PnlCst.addTextField(this,"021122fffe334455", PnlCst.xquart2, yIndex, PnlCst.widthHalf);		
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        //node owner 
		nodeOwnerLabel = PnlCst.addLabel(this,	"Name of the node owner:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);                 
        nodeOwnerField =PnlCst.addTextField(this,"myself", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        //node owner ip
		nodeOwnerIPLabel = PnlCst.addLabel(this,"IP address of the node owner:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
        nodeOwnerIPField =PnlCst.addTextField(this,"localhost", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
		
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        //the node type
        nodeTypeLabel = PnlCst.addLabel(this,"Type of the node to connect to:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
                
        nodetypeList = new JComboBox(LoociRuntimes.RUNTIME_NAMES);
        nodetypeList.setSelectedIndex(1);
        nodetypeList.addActionListener(this);
        nodetypeList.setBounds(PnlCst.xquart2, yIndex, PnlCst.widthHalf , PnlCst.HEIGHT);
        this.add(nodetypeList);
        
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        btnInspectNodeType = PnlCst.addButton(this,this,"Inspect platfrom type","inspectPT",PnlCst.xquart3,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
      
        //Component table
        int componentTableHeight = 75;
        for(int i = 0 ; i < MAX_NR_CODEBASES; i ++){
        	codebaseTableData[i][0] = ""+i;
        }
    	codebaseTable = new JTable(codebaseTableData, columnNames);
    	codebaseTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        codebaseTable.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthFull, componentTableHeight);
        JScrollPane scrollPane1 = new JScrollPane(codebaseTable);
        scrollPane1.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthFull, componentTableHeight);
        codebaseTable.setFillsViewportHeight(true);
        this.add(scrollPane1);        
        yIndex += componentTableHeight + PnlCst.BUFFERSPACE;
        

        
        //component buttons
        btnInspectComponent = PnlCst.addButton(this,this,"Inspect codebases","inspectComp",PnlCst.xquart0,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);
        btnInstantiateComp = PnlCst.addButton(this, this, "Instantiate", "instantiate",PnlCst.xquart1,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);
        btnremoveComp = PnlCst.addButton(this,this,"Remove ","remove",PnlCst.xquart2,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);
        
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        // instance table
        for(int i = 0 ; i < MAX_NR_CODEBASES; i ++){
        	componentTableData[i][0] = ""+i;
        }
    	componentTable = new JTable(componentTableData, componentTableNames);
    	componentTable.getColumnModel().getColumn(0).setPreferredWidth(30);
    	componentTable.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthFull, componentTableHeight);
        scrollPane1 = new JScrollPane(componentTable);
        scrollPane1.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthFull, componentTableHeight);
        codebaseTable.setFillsViewportHeight(true);
        this.add(scrollPane1);        
        
        yIndex += componentTableHeight + PnlCst.BUFFERSPACE;
        
 
        //component buttons
        btnInspectInstance = PnlCst.addButton(this, this, "get comps", "inspectInst",PnlCst.xquart0, yIndex, PnlCst.widthQuart , PnlCst.HEIGHT);
    	btnActivateInst = PnlCst.addButton(this,this,"Activate ","activate",PnlCst.xquart1,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);
        btnDeactivateInst = PnlCst.addButton(this,this,"Deactivate ","deactivate",PnlCst.xquart2,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);
        btnDestroyInst = PnlCst.addButton(this,this,"Destroy ", "destroy",PnlCst.xquart3,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);

        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        btnSetSourceInstance = PnlCst.addButton(this,this,"Set source inst","setSource",PnlCst.xquart0,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);
    	btnSetDestinationInstance = PnlCst.addButton(this,this,"Set dest inst ","setDest",PnlCst.xquart1,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);
    	btnDoWire = PnlCst.addButton(this,this,"Do wire", "doWire",PnlCst.xquart2,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);
    	btnSelectInstance = PnlCst.addButton(this,this,"Select instance", "selectInst",PnlCst.xquart3,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);

        yIndex += componentTableHeight + PnlCst.BUFFERSPACE;
        
        
        //inspect buttons
    	btnInspectWireLocal = PnlCst.addButton(this,this,"Inspect localWire","inspectWireLocal",PnlCst.xquart1,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);
    	btnInspectWireRemoteTo = PnlCst.addButton(this,this,"Inspect wireTo","inspectWireTo",PnlCst.xquart2,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);
    	btnInspectWireRemoteFrom = PnlCst.addButton(this,this,"Inspect wireFrom","inspectWireFrom",PnlCst.xquart3,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);
        
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        
        //wiring tables
        for(int i = 0 ; i < MAX_NR_WIRINGS; i ++){
        	dataWireLocal[i][0] = ""+i;
        	dataWireRemoteTo[i][0] = ""+i;
        	dataWireRemoteFrom[i][0] = ""+i;
        }
        //wire local table
    	wireLocalTable = new JTable(dataWireLocal, columnWireLocalNames);
    	wireLocalTable.getColumnModel().getColumn(0).setPreferredWidth(30);
    	wireLocalTable.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthFull, componentTableHeight);
        JScrollPane scrollPane1Local = new JScrollPane(wireLocalTable);
        scrollPane1Local.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthFull, componentTableHeight);
        wireLocalTable.setFillsViewportHeight(true);
        this.add(scrollPane1Local);        
        yIndex += componentTableHeight + PnlCst.BUFFERSPACE;
        
        
        //wire remote To table
    	wireRemoteToTable = new JTable(dataWireRemoteTo, columnWireRemoteToNames);
    	wireRemoteToTable.getColumnModel().getColumn(0).setPreferredWidth(30);
    	wireRemoteToTable.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthFull, componentTableHeight);
        JScrollPane scrollPane1RemoteTo = new JScrollPane(wireRemoteToTable);
        scrollPane1RemoteTo.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthFull, componentTableHeight);
        wireRemoteToTable.setFillsViewportHeight(true);
        this.add(scrollPane1RemoteTo);        
        yIndex += componentTableHeight + PnlCst.BUFFERSPACE;
      

        //wire remote from table
    	wireRemoteFromTable = new JTable(dataWireRemoteFrom, columnWireRemoteFromNames);
    	wireRemoteFromTable.getColumnModel().getColumn(0).setPreferredWidth(30);
    	wireRemoteFromTable.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthFull, componentTableHeight);
        JScrollPane scrollPane1RemoteFrom = new JScrollPane(wireRemoteFromTable);
        scrollPane1RemoteFrom.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthFull, componentTableHeight);
        wireRemoteFromTable.setFillsViewportHeight(true);
        this.add(scrollPane1RemoteFrom);        
        yIndex += componentTableHeight + PnlCst.BUFFERSPACE;
        
        
    	btnRemoveWireLocal = PnlCst.addButton(this,this,"Remove localWire","removeWireLocal",PnlCst.xquart1,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);
    	btnRemoveWireRemoteTo = PnlCst.addButton(this,this,"Remove wireTo","removeWireTo",PnlCst.xquart2,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);
    	btnRemoveWireRemoteFrom = PnlCst.addButton(this,this,"Remove wireFrom","removeWireFrom",PnlCst.xquart3,yIndex,PnlCst.widthQuart,PnlCst.HEIGHT);   
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
    	
        PnlCst.addButton(this, this, "Remove wireToAll", "removeWireToAll", PnlCst.xquart2, yIndex, PnlCst.widthQuart, PnlCst.HEIGHT);
        PnlCst.addButton(this, this, "Remove wireFromAll", "removeWireFromAll", PnlCst.xquart3, yIndex, PnlCst.widthQuart, PnlCst.HEIGHT);
    	
	}
	
	public NodePanel(GuiInterface manager,ILoociAPI api, String[] nodeTypes, InstanceSelectionListener listener){
		this.manager = manager;
		manager.addSelectionListener(SelectionListener.SELECT_NODE, this);		
		this.api = api;
		this.setPreferredSize(new Dimension(1024,800));		
		initGui();
	}
	
	public void cleanUp(){
		manager.removeSelectionListener(SelectionListener.SELECT_NODE,this);
	}

	@Override
	public void actionPerformed(ActionEvent action) {
		System.out.println("ActionCommand: "+action.getActionCommand());
		
		try{
			
			if(action.getActionCommand().equals("inspectComp")){		
				inspectComponents();			
			} else if(action.getActionCommand().equals("inspectPT")){		
				inspectPlatfromType();		
			} else if(action.getActionCommand().equals("inspectInst")){		
				inspectInstances();		
			} else if(action.getActionCommand().equals("activate")){
				int row = componentTable.getSelectedRow();
				int instanceId = Integer.parseInt((String)componentTable.getValueAt(row, 1));
				api.activate((byte)instanceId, nodeIpField.getText());
				componentTable.setValueAt(""+api.getState((byte)instanceId, nodeIpField.getText()),row,3);
			} else if(action.getActionCommand().equals("deactivate")){
				System.out.println("deactivating node");
				int row = componentTable.getSelectedRow();
				int instanceId = Integer.parseInt((String)componentTable.getValueAt(row, 1));
				api.deactivate((byte)instanceId, nodeIpField.getText());
				componentTable.setValueAt(""+api.getState((byte)instanceId, nodeIpField.getText()),row,3);
			} else if(action.getActionCommand().equals("instantiate")){
				instantiateComp();
			} else if(action.getActionCommand().equals("destroy")){
				destroyInstance();
			} else if(action.getActionCommand().equals("remove")){
				removeComp();
			} else if(action.getActionCommand().equals("inspectWireLocal")){
				inspectWireLocal();
			} else if(action.getActionCommand().equals("inspectWireTo")){
				inspectWireRemoteTo();
			} else if(action.getActionCommand().equals("inspectWireFrom")){
				inspectWireRemoteFrom();
			} else if(action.getActionCommand().equals("removeWireLocal")){
				removeWireLocal();
			} else if(action.getActionCommand().equals("removeWireTo")){
				removeWireRemoteTo();
			} else if(action.getActionCommand().equals("removeWireFrom")){
				removeWireRemoteFrom();
			} 	else if(action.getActionCommand().equals("setSource")){
				setSourceInstance();
			}	else if(action.getActionCommand().equals("setDest")){
				setDestinationInstance();
			}	else if(action.getActionCommand().equals("doWire")){
				doWire();
			}	else if(action.getActionCommand().equals("selectInst")){
				selectInstance();
			}
		
		} catch(LoociManagementException e){
			System.out.println("ERROR MGT EXCEPTiON"+ e.getErrorCode());
		} catch(UnknownHostException e){

			System.out.println("Host not found exception");
		}
		
	}
	

	private void inspectPlatfromType() throws LoociManagementException,UnknownHostException{
		byte type = api.getPlatformType(nodeIpField.getText());
		nodetypeList.setSelectedIndex(type);
	}

	private void selectInstance(){
		InstanceInfo info = getSelectedInstanceInfo();
		if(info != null){
			manager.notifySelectionListener(SelectionListener.SELECT_INSTANCE, info);
		}
	}
	
	
	private ComponentInfo getComponentInfo(byte componentId){
		for(int i = 0; i < MAX_NR_CODEBASES ; i++){
			if(Byte.parseByte((String)codebaseTable.getValueAt(i, 1))==componentId){
				String type = (String)codebaseTable.getValueAt(i, 2);
				return new ComponentInfo(nodeIpField.getText(),componentId,type);				
			}			
		}
		return null;
	}
	
	private InstanceInfo getSelectedInstanceInfo(){
		int selection = componentTable.getSelectedRow();
		byte instanceId = Byte.parseByte((String)componentTable.getValueAt(selection, 1));
		byte componentId = Byte.parseByte((String)componentTable.getValueAt(selection, 2));
		short[] receptacles = Utils.parseShortArray((String)componentTable.getValueAt(selection, 3));
		short[] interfaces = Utils.parseShortArray((String)componentTable.getValueAt(selection, 4));
		byte state = Byte.parseByte((String)componentTable.getValueAt(selection, 5));
		ComponentInfo info = getComponentInfo(componentId);
		return new InstanceInfo(instanceId,info,state,receptacles,interfaces);
	}
	
	private void setSourceInstance(){
		InstanceInfo info = getSelectedInstanceInfo();
		if(info != null && info.getComponentInfo() != null){
			wireFrame.setSourceInstance(info);
		} else{
			JOptionPane.showMessageDialog(this, "Error","Could not find instance or component in list",JOptionPane.ERROR_MESSAGE);				
		}
	}
	
	private void setDestinationInstance(){
		InstanceInfo info = getSelectedInstanceInfo();
		if(info != null && info.getComponentInfo() != null){
			wireFrame.setDestinationInstance(info);
		} else{
			JOptionPane.showMessageDialog(this, "Error","Could not find instance or component in list",JOptionPane.ERROR_MESSAGE);				
		}
	}
	
	private void doWire(){
		wireFrame.showFrame();
	}
	
	private void destroyInstance() throws LoociManagementException,UnknownHostException {
		System.out.println("destroying instance");
		int row = componentTable.getSelectedRow();
		int instanceId = Integer.parseInt((String)componentTable.getValueAt(row, 1));
		api.destroy((byte)instanceId,  nodeIpField.getText());
		//inspectNode();
	}

	private void instantiateComp() throws LoociManagementException,UnknownHostException{
		System.out.println("instantiating component");
		int row = codebaseTable.getSelectedRow();
		int componentId = Integer.parseInt((String)codebaseTable.getValueAt(row, 1));
		api.instantiate((byte)componentId,  nodeIpField.getText());
		//inspectNode();
	}

	private void removeComp()throws LoociManagementException,UnknownHostException{
		System.out.println("removing component");
		int row = codebaseTable.getSelectedRow();
		int componentId = Integer.parseInt((String)codebaseTable.getValueAt(row, 1));
		api.remove((byte)componentId,  nodeIpField.getText());
		//inspectNode();
	}
	

	private void removeWireRemoteFrom() throws LoociManagementException,UnknownHostException{
		String nodeID = nodeIpField.getText();
		int selection = wireRemoteFromTable.getSelectedRow();
		
		short event = Short.parseShort((String)wireRemoteFromTable.getValueAt(selection, 1));
		String src_node = (String)wireRemoteFromTable.getValueAt(selection, 2);
		byte src_comp = Byte.parseByte((String)wireRemoteFromTable.getValueAt(selection, 3));
		byte dst = Byte.parseByte((String)wireRemoteFromTable.getValueAt(selection, 4));
		
		api.unwireFrom(event,src_comp, src_node, dst, nodeID);
		//inspectWireRemoteFrom();		
	}

	private void removeWireRemoteTo()throws LoociManagementException,UnknownHostException {
		String nodeID = nodeIpField.getText();
		int selection = wireRemoteToTable.getSelectedRow();
		
		short event = Short.parseShort((String)wireRemoteToTable.getValueAt(selection, 1));
		byte src = Byte.parseByte((String)wireRemoteToTable.getValueAt(selection, 2));
		String dstNode = (String)wireRemoteToTable.getValueAt(selection, 3);
		
		api.unwireTo(event, src, nodeID, dstNode);
		//inspectWireRemoteTo();
	}

	private void removeWireLocal() throws LoociManagementException,UnknownHostException{
		String nodeID = nodeIpField.getText();
		int selection = wireLocalTable.getSelectedRow();
		
		short event = Short.parseShort((String)wireLocalTable.getValueAt(selection, 1));
		byte src = Byte.parseByte((String)wireLocalTable.getValueAt(selection, 2));
		byte dst = Byte.parseByte((String)wireLocalTable.getValueAt(selection, 3));
		
		api.unwireLocal(event, src, dst, nodeID);
		//inspectWireLocal();
	}

	private void inspectWireLocal()throws LoociManagementException,UnknownHostException{
		String nodeID = nodeIpField.getText();
		
		for(int i = 0 ; i < 8 ; i++){
			for(int j = 1 ; j < 4 ; j ++){
				wireLocalTable.setValueAt("",i ,j);
			}
		}
		LocalBinding[] wirings = api.getLocalWires(
				EventTypes.ANY_EVENT,
				LoociConstants.COMPONENT_WILDCARD,
				LoociConstants.COMPONENT_WILDCARD,
				nodeID);
		
		if(wirings != null){
			System.out.println(wirings.length);
			for(int i = 0 ; i < wirings.length ; i ++){
				wireLocalTable.setValueAt(wirings[i].getEventID()+"",i,1);
				wireLocalTable.setValueAt(wirings[i].getSourceComponentID(),i,2);
				wireLocalTable.setValueAt(wirings[i].getDestinationComponentID(),i,3);
			}
			System.out.println("printed local wirings");
		} else {
			System.out.println("node not available");
		}		
	}
	
	private void inspectWireRemoteTo()throws LoociManagementException,UnknownHostException{
		String nodeID = nodeIpField.getText();
		
		for(int i = 0 ; i < 8 ; i++){
			for(int j = 1 ; j < 4 ; j ++){
				wireRemoteToTable.setValueAt("",i ,j);
			}
		}
		RemoteToBinding[] wirings = api.getOutgoingRemoteWires(
				EventTypes.ANY_EVENT,
				LoociConstants.COMPONENT_WILDCARD,
				nodeID,
				LoociConstants.ADDR_ANY);
		
		if(wirings != null){
			for(int i = 0 ; i < wirings.length ; i ++){
				wireRemoteToTable.setValueAt(wirings[i].getEventID(),i,1);
				wireRemoteToTable.setValueAt(wirings[i].getSourceComponentID(),i,2);
				wireRemoteToTable.setValueAt(wirings[i].getDestinationNode(),i,3);
			}
			System.out.println("printed remoteTo wirings");
		} else {
			System.out.println("node not available");
		}		
	}
	
	private void inspectWireRemoteFrom()throws LoociManagementException,UnknownHostException{
		String nodeID = nodeIpField.getText();
		
		for(int i = 0 ; i < 8 ; i++){
			for(int j = 1 ; j < 5 ; j ++){
				wireRemoteFromTable.setValueAt("",i ,j);
			}
		}
		RemoteFromBinding[] wirings = api.getIncomingRemoteWires(
				EventTypes.ANY_EVENT,
				LoociConstants.COMPONENT_WILDCARD,
				LoociConstants.ADDR_ANY,
				LoociConstants.COMPONENT_WILDCARD,
				nodeID
				);
		
		if(wirings != null){
			for(int i = 0 ; i < wirings.length ; i ++){
				wireRemoteFromTable.setValueAt(wirings[i].getEventID(),i,1);
				wireRemoteFromTable.setValueAt(wirings[i].getSourceComponentID(),i,2);
				wireRemoteFromTable.setValueAt(wirings[i].getSourceNode(),i,3);
				wireRemoteFromTable.setValueAt(wirings[i].getDestinationComponentID(),i,4);
			}
			System.out.println("printed remoteFrom wirings");
		} else {
			System.out.println("node not available");
		}	
	}
	
	
	private void inspectComponents()throws LoociManagementException,UnknownHostException{
		String nodeID = nodeIpField.getText();
		
		for(int i = 0 ; i < MAX_NR_CODEBASES ; i++){
			for(int j = 1 ; j < columnNames.length ; j ++){
				codebaseTable.setValueAt("",i ,j);
			}
		}
		
		byte[] codebaseIDs = api.getCodebaseIDs(nodeID);
		
		if(codebaseIDs != null){			
			for(int i = 0 ; i < codebaseIDs.length ; i ++){
				codebaseTable.setValueAt(""+codebaseIDs[i],i,1);		
				String type = api.getCodebaseName((byte)codebaseIDs[i],  nodeIpField.getText());
				codebaseTable.setValueAt(type,i,2);
			}
		} else {
			System.out.println("node not available");
		}		
	}
	
	private void inspectInstances( ) throws LoociManagementException,UnknownHostException{
		String nodeID = nodeIpField.getText();
		for(int i = 0 ; i < MAX_NR_COMPONENTS ; i++){
			for(int j = 1 ; j < componentTableNames.length ; j ++){
				componentTable.setValueAt("",i ,j);
			}
		}
		byte[] instanceIDs = api.getComponentIDs(nodeID);
		
		if(instanceIDs != null){
			byte instanceId = 0;
			for(int i = 0 ; i < instanceIDs.length ; i ++){
				instanceId = instanceIDs[i];
				componentTable.setValueAt(""+instanceId,i,1);
				byte state = api.getState(instanceId, nodeIpField.getText());
				componentTable.setValueAt(""+state, i, 5);
				short[] interfaces = api.getInterfaces(instanceId, nodeIpField.getText());
				componentTable.setValueAt(concatenateShortIds(interfaces), i, 4);
				short[] receptacles = api.getReceptacles(instanceId, nodeIpField.getText());
				componentTable.setValueAt(concatenateShortIds(receptacles), i, 3);
				byte cbId = api.getCodebaseIdOfComponent(instanceId, nodeIpField.getText());
				componentTable.setValueAt(""+cbId, i, 2);
			}
		} else {
			System.out.println("node not available");
		}	
	}
	
	
	private void inspectNode()throws LoociManagementException,UnknownHostException{
		inspectComponents();
		inspectInstances();
	}
	

	
	
	private String concatenateShortIds(short[] bytes){
		String concat = "";
		for(int i = 0 ; i < bytes.length ;i ++){
			concat += bytes[i]+",";
		}
		return concat;
	}
	
	public LoociNodeInfo getNodeInfo(){
		LoociNodeInfo info = new LoociNodeInfo();
		info.setNodeIP(nodeIpField.getText());
		info.setFederationName(nodeOwnerField.getText());	
		info.setFederationIP(nodeOwnerIPField.getText());		
		info.setNodeMac(nodeNameField.getText());
		info.setNodeType((String) nodetypeList.getSelectedItem());
		return info;
	}
	
	public void setNodeInfo(LoociNodeInfo info){
		nodeIpField.setText(info.getNodeIP());
		nodeNameField.setText(info.getNodeMac());
		nodeOwnerIPField.setText(info.getFederationIp());
		nodeOwnerField.setText(info.getFederationName());
		nodetypeList.setSelectedItem(info.getNodeType());
	}

	@Override
	public void notifySelection(String selection, Object object) {
		if(selection.equals(SelectionListener.SELECT_NODE)){
			setNodeInfo((LoociNodeInfo)object);
		}
	}

}
