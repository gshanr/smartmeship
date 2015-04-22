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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


import looci.osgi.gui.serv.GuiInterface;
import looci.osgi.gui.serv.LoociPanel;
import looci.osgi.gui.serv.PnlCst;
import looci.osgi.gui.serv.SelectionListener;
import looci.osgi.mgtGui.lib.NodeSelectionListener;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.constants.LoociRuntimes;
import looci.osgi.servExt.appInfo.LoociNodeInfo;




@SuppressWarnings("unused")
public abstract class NodeInfoPanel extends LoociPanel implements SelectionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6646620034840251918L;

    private JLabel nodeIpLabel;
    protected JTextField nodeIpField;
	
    private JLabel nodeNameLabel;
    protected JTextField nodeNameField;
	
    private JLabel nodeOwnerLabel;
    protected JTextField nodeOwnerField;
	
    private JLabel nodeOwnerIPLabel;
    protected JTextField nodeOwnerIPField;
	
	private JLabel nodeTypeLabel;
	protected JComboBox nodetypeList;
	
	protected int yIndex = PnlCst.BUFFERSPACE;
	
	
	public NodeInfoPanel(){
		this.setPreferredSize(new Dimension(1024,800));
    	this.setLayout(null);
    	
    	
	 	
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
		
        nodeTypeLabel = PnlCst.addLabel(this,"Type of the node to connect to:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
        
        nodetypeList = PnlCst.addComboBox(this, LoociRuntimes.RUNTIME_NAMES, PnlCst.xquart2, PnlCst.xquart0, PnlCst.widthHalf);

        
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
	}
	
	public void setNodeInfo(LoociNodeInfo info){
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
