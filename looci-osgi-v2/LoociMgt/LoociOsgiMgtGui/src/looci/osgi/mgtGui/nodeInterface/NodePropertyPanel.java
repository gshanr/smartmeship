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

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import looci.osgi.gui.serv.GuiInterface;
import looci.osgi.gui.serv.LoociPanel;
import looci.osgi.gui.serv.PnlCst;
import looci.osgi.gui.serv.SelectionListener;
import looci.osgi.mgtGui.lib.InstanceSelectionListener;
import looci.osgi.mgtGui.lib.NodeSelectionListener;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.impl.property.PropertyInfo;
import looci.osgi.serv.util.Utils;
import looci.osgi.servExt.appInfo.InstanceInfo;
import looci.osgi.servExt.appInfo.LoociNodeInfo;
import looci.osgi.servExt.mgt.ILoociAPI;




@SuppressWarnings("unused")
public class NodePropertyPanel extends LoociPanel implements ActionListener,SelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 552452993697442927L;

	private int componentTableHeight = 75;
	private int maxNrProperties = 10;
	
	
	private ILoociAPI api;
	
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
	
    private JLabel instanceIdLabel;
	private JTextField instanceIdField;
	
	private JButton btnGetProperties;
	private JButton btnGetPropertyInfo;
	private JButton btnGetProperty;
	private JButton btnSetProperty;
	
	private JTable propertyTable;
	
	public NodePropertyPanel(ILoociAPI api, String[] nodeTypes){

		this.api = api;
		this.setPreferredSize(new Dimension(1024,800));
		
		int yIndex = PnlCst.BUFFERSPACE;		
    	this.setLayout(null);
		
        /////////////////////////////////////////////////////////////////////////
        // Header part: node info
        /////////////////////////////////////////////////////////////////////////
    	
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
        
        
        nodetypeList = new JComboBox(nodeTypes);
        nodetypeList.setSelectedIndex(1);
        nodetypeList.addActionListener(this);
        nodetypeList.setBounds(PnlCst.xquart2, yIndex, PnlCst.widthHalf , PnlCst.HEIGHT);
        this.add(nodetypeList);
        
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        // instance id
        instanceIdLabel = PnlCst.addLabel(this,	"Instance to reparamaterize:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
        instanceIdField = PnlCst.addTextField(this,"10", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        // get properties button
        
        btnGetProperties = PnlCst.addButton(this, this, "get properties", "getProperties",PnlCst.xquart0, yIndex, PnlCst.widthQuart , PnlCst.HEIGHT);
        btnGetPropertyInfo = PnlCst.addButton(this, this, "get property info", "getPropertyInfo",PnlCst.xquart1, yIndex, PnlCst.widthQuart , PnlCst.HEIGHT);
        btnGetProperty = PnlCst.addButton(this, this, "get property", "getProperty",PnlCst.xquart2, yIndex, PnlCst.widthQuart , PnlCst.HEIGHT);
        btnSetProperty= PnlCst.addButton(this, this, "set property", "setProperty",PnlCst.xquart3, yIndex, PnlCst.widthQuart , PnlCst.HEIGHT);
        
        yIndex += PnlCst.HEIGHT  + PnlCst.BUFFERSPACE;
        
        /////////////////////////////////////////////////////////////////////////
        // 
        /////////////////////////////////////////////////////////////////////////
        
		
        Object[] propertyTableHeader = new Object[]{"Property Nr","PropertyId","PropertyName","PropertyType","PropertyValue"};
        
        Object[][] propertyTableValues = new Object[maxNrProperties][7];
        for(int i = 0 ; i < maxNrProperties ; i ++){
        	propertyTableValues[i][0] = i+""; //index
        	propertyTableValues[i][1] = ""; // propId
        	propertyTableValues[i][2] = ""; // propName
        	propertyTableValues[i][3] = ""; // propType
        	propertyTableValues[i][4] = ""; // propValue
        	
        }
        
        DefaultTableModel dm = new DefaultTableModel();
        dm.setDataVector(propertyTableValues, propertyTableHeader);
               
        propertyTable = new JTable(dm);
        propertyTable.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthFull, componentTableHeight * 2);        
        JScrollPane scrollPanel = new JScrollPane(propertyTable);
        propertyTable.setFillsViewportHeight(true);
        scrollPanel.setBounds(PnlCst.xquart0, yIndex, PnlCst.widthFull, componentTableHeight * 2);
        
        JComboBox valueCombobox = new JComboBox(LoociConstants.DATATYPE_STRINGS);
        TableColumn typeColumn = propertyTable.getColumnModel().getColumn(3);
        typeColumn.setCellEditor(new DefaultCellEditor(valueCombobox));
        
        this.add(scrollPanel);
        yIndex += componentTableHeight * 2 + PnlCst.BUFFERSPACE;
	      
        
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try{
			if(e.getActionCommand().equals("getProperties")){
				int instanceId = Integer.parseInt(instanceIdField.getText());
				short[] properties;
				properties = api.getProperties((byte)instanceId, nodeIpField.getText());
				int i = 0;
				for(i = 0 ; i < properties.length;i++){
					propertyTable.setValueAt(""+properties[i], i, 1);
				}
				for(;i<maxNrProperties;i++){
					propertyTable.setValueAt("", i, 1);				
				}

			} else if(e.getActionCommand().equals("getPropertyInfo")){
				int instanceId = Integer.parseInt(instanceIdField.getText());
				int row = propertyTable.getSelectedRow();
				short propertyId = Short.parseShort((String)propertyTable.getValueAt(row, 1));
				PropertyInfo info = api.getPropertyInfo(propertyId,(byte) instanceId,  nodeIpField.getText());
				propertyTable.setValueAt(info.getPropertyName(), row, 2);
				propertyTable.setValueAt(info.getPropertyTypeName(), row, 3);
			} else if(e.getActionCommand().equals("getProperty")){
				int row = propertyTable.getSelectedRow();
				int instanceId = Integer.parseInt(instanceIdField.getText());
				short propertyId = Short.parseShort((String)propertyTable.getValueAt(row, 1));
				byte[] value = api.getProperty(propertyId, (byte) instanceId, nodeIpField.getText());
				String type = (String)propertyTable.getValueAt(row, 3);
				
				String output = "";
				output = Utils.createStringFromByteContent(type, value);
				
				propertyTable.setValueAt(output, row, 4);
			} else if(e.getActionCommand().equals("setProperty")){
				int row = propertyTable.getSelectedRow();
				//set
				int instanceId = Integer.parseInt(instanceIdField.getText());
				short propertyId = Short.parseShort((String)propertyTable.getValueAt(row, 1));
				byte[] output = null;
				String valueText = (String)propertyTable.getValueAt(row, 4);
				String type = (String)propertyTable.getValueAt(row, 3);
				
				output = Utils.createByteArrayFromTypeString(type, valueText);
				api.setProperty(output, propertyId, (byte)instanceId, nodeIpField.getText());
			} 
		} catch (LoociManagementException exc){
			exc.printStackTrace();
		} catch (UnknownHostException exc){
			exc.printStackTrace();
		}

	}
	
	
	public void setNodeInfo(LoociNodeInfo info) {
		nodeIpField.setText(info.getNodeIP());
		nodeNameField.setText(info.getNodeMac());
		nodeOwnerField.setText(info.getFederationName());
		nodeOwnerIPField.setText(info.getFederationIp());
		nodetypeList.setSelectedItem(info.getNodeType());
	}


	public void setInstanceInfo(InstanceInfo info) {
		nodeIpField.setText(info.getComponentInfo().getNodeId());
		instanceIdField.setText(info.getInstanceId()+"");
	}
	
	public void notifySelection(String selection, Object object){
		if(selection.equals(SelectionListener.SELECT_NODE)){
			setNodeInfo((LoociNodeInfo)object);
		} else if(selection.equals(SelectionListener.SELECT_NODE)){
			setInstanceInfo((InstanceInfo)object);
		}
	}
}
