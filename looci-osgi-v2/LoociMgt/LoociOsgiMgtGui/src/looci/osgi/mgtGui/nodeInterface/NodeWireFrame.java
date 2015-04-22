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

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import looci.osgi.gui.serv.PnlCst;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.util.XString;
import looci.osgi.servExt.appInfo.InstanceInfo;
import looci.osgi.servExt.mgt.ILoociAPI;




public class NodeWireFrame extends JFrame implements ActionListener{

	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -227555347239950169L;

	
    protected JTextField srcNodeIpField;
    
    protected JTextField srcInstanceField;
    
    protected JTextField srcTypesField;
    
    protected JTextField dstNodeIpField;
    
    protected JTextField dstInstanceField;
    
    protected JTextField dstTypesField;
    
    protected JTextField wireEventField;
    
	
	private InstanceInfo sourceInfo;
	private InstanceInfo destInfo;
	
	private ILoociAPI api;
	
	public NodeWireFrame(ILoociAPI api){
		this.api = api;
		
		this.setPreferredSize(new Dimension(1024,800));
		JPanel p = new JPanel();
		p.setLayout(null);
		this.add(p);
		
		int yIndex = PnlCst.BUFFERSPACE;
		
		PnlCst.addLabel(p,"Ip of source instance:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
		srcNodeIpField = PnlCst.addTextField(p,LoociConstants.ADDR_LOCAL, PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        PnlCst.addLabel(p,"Source instance Id:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
        srcInstanceField = PnlCst.addTextField(p,"10", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
		
        PnlCst.addLabel(p,"Events provided by source:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
        srcTypesField = PnlCst.addTextField(p,"0", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
		PnlCst.addLabel(p,"Ip of dest instance:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
		dstNodeIpField = PnlCst.addTextField(p,LoociConstants.ADDR_LOCAL, PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        PnlCst.addLabel(p,"Dest instance Id:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
        dstInstanceField = PnlCst.addTextField(p,"10", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
		
        PnlCst.addLabel(p,"Events consumed by dest:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
        dstTypesField = PnlCst.addTextField(p,"0", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        PnlCst.addLabel(p,"Event type to be wired:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
        wireEventField= PnlCst.addTextField(p,"0", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
    	PnlCst.addButton(p, this, "doWire", "doWire", PnlCst.xquart0, yIndex, PnlCst.widthHalf, PnlCst.HEIGHT);
    	PnlCst.addButton(p, this, "cancel", "cancel", PnlCst.xquart2, yIndex, PnlCst.widthHalf, PnlCst.HEIGHT);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
    	
    	PnlCst.addButton(p, this, "doWireToAll", "doWireToAll", PnlCst.xquart0, yIndex, PnlCst.widthHalf, PnlCst.HEIGHT);;
    	PnlCst.addButton(p, this, "doWireFromAll", "doWireFromAll", PnlCst.xquart2, yIndex, PnlCst.widthHalf, PnlCst.HEIGHT);;
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
	}
	
	private void tryMatch(){
		if(sourceInfo == null || destInfo == null){
			return;
		}
		short[] interfaces = sourceInfo.getInterfaces();
		short[] receptacles = destInfo.getReceptacles();
		short value = 0;
		for(int i = 0; i < interfaces.length && value == 0; i++){
			for(int j = 0 ; j < receptacles.length && value == 0;j++){
				if(interfaces[i] == receptacles[j]){
					value = interfaces[i];
				}
			}
		}
		wireEventField.setText(""+value);
		
	}
	
	public void setSourceInstance(InstanceInfo info){
		srcNodeIpField.setText(info.getComponentInfo().getNodeId());
		srcInstanceField.setText(info.getInstanceId()+"");
		srcTypesField.setText(XString.printShortArray(info.getInterfaces()));
		sourceInfo = info;
		tryMatch();
	}
	
	public void setDestinationInstance(InstanceInfo info){
		dstNodeIpField.setText(info.getComponentInfo().getNodeId());
		dstInstanceField.setText(info.getInstanceId()+"");
		dstTypesField.setText(XString.printShortArray(info.getReceptacles()));
		destInfo = info;
		tryMatch();
	}
	
	public void actionPerformed(ActionEvent action) {
		if(action.getActionCommand().equals("doWire")){			
			String srcNodeID = srcNodeIpField.getText();	
			String dstNodeID = dstNodeIpField.getText();
			
			byte srcComp = Byte.parseByte(srcInstanceField.getText());
			byte dstComp = Byte.parseByte(dstInstanceField.getText());
			
			short eventType = Short.parseShort(wireEventField.getText());
			
			if(srcNodeID.equals(dstNodeID)){
				
				try{
					api.wireLocal(eventType, srcComp, dstComp, srcNodeID);
					JOptionPane.showMessageDialog(this, "Done local wiring","Done local wiring",JOptionPane.INFORMATION_MESSAGE);	
				} catch(LoociManagementException e){
					JOptionPane.showMessageDialog(this, "Local wiring failed","Local wiring failed",JOptionPane.ERROR_MESSAGE);	
				}catch (UnknownHostException e) {
				}

			} else{
				boolean to = false;;
				boolean from = false;;
				try {
					api.wireTo(eventType, srcComp, srcNodeID, dstNodeID);
					to = true;
				} catch (LoociManagementException e) {
				}catch (UnknownHostException e) {
				}
				try {
					api.wireFrom(eventType, srcComp, srcNodeID, dstComp, dstNodeID);
					from = true;
				} catch (LoociManagementException e) {
				}catch (UnknownHostException e) {
				}
				if(to && from){
					JOptionPane.showMessageDialog(this, "Done wiring","Source and dest wiring created",JOptionPane.INFORMATION_MESSAGE);	
				} else{
					String message = "";
					if(to){
						message += "to wiring succeeded, ";
					} else{
						message += "to wiring failed, ";
					}
					if(from){
						message += "from wiring succeeded";
					} else{
						message += "from wiring failed";
					}

					JOptionPane.showMessageDialog(this, "Error while wiring",message,JOptionPane.ERROR_MESSAGE);
					
				}
			}
				
		} 
		
		setVisible(false);
	}

	
	
	public void showFrame( ){
		setVisible(true);		
	}
}
