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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import looci.osgi.gui.serv.GuiInterface;
import looci.osgi.gui.serv.LoociPanel;
import looci.osgi.gui.serv.PnlCst;
import looci.osgi.gui.serv.SelectionListener;
import looci.osgi.mgtGui.lib.InstanceSelectionListener;
import looci.osgi.serv.components.Event;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.util.Utils;
import looci.osgi.servExt.appInfo.InstanceInfo;
import looci.osgi.servExt.mgt.ILoociAPI;




@SuppressWarnings("unused")
public class EventSendPanel extends LoociPanel implements ActionListener,SelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 519813056359388331L;

	private ILoociAPI api;
	
	private JLabel eventTypeLabel;
	private JTextField eventTypeField;
	
	private JLabel eventContentTypeLabel;
	private JComboBox eventContentTypeField;
	
	private JLabel eventContentLabel;
	private JTextField eventContentField;
	
	private JLabel needsReplyLabel;
	private JCheckBox needsReplyBox;
	
	private JLabel isDirectedLabel;
	private JCheckBox isDirectedBox;
	
	private JLabel targetAddrNameLabel;
	private JTextField targetAddrField;
	
	private JLabel targetInstIdLabel;
	private JTextField targetInstIdField;
	
	private JButton sendButton;
	
	public EventSendPanel(ILoociAPI api){
		this.api = api;
		
		this.setPreferredSize(new Dimension(1024,800));
		
		int yIndex = PnlCst.BUFFERSPACE;		
    	this.setLayout(null);
		
    	//event type label
    	eventTypeLabel = PnlCst.addLabel(this,	"Event type to send:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
    	eventTypeField = PnlCst.addTextField(this,"100", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        	
    	
    	eventContentTypeLabel = PnlCst.addLabel(this,"Type of the entered event content:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);;
    	eventContentTypeField = new JComboBox(LoociConstants.DATATYPE_STRINGS);
    	eventContentTypeField.setSelectedIndex(0);
    	eventContentTypeField.addActionListener(this);
    	eventContentTypeField.setBounds(PnlCst.xquart2, yIndex, PnlCst.widthHalf , PnlCst.HEIGHT);
        this.add(eventContentTypeField);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        eventContentLabel = PnlCst.addLabel(this,	"Event content to send:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
        eventContentField = PnlCst.addTextField(this,"0", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        needsReplyLabel = PnlCst.addLabel(this,	"Does event need reply:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
        needsReplyBox = PnlCst.addCheckbox(this,"0", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        isDirectedLabel = PnlCst.addLabel(this,	"Is event directed:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
        isDirectedBox = PnlCst.addCheckbox(this,"0", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
    	
        targetAddrNameLabel = PnlCst.addLabel(this,	"Address of target:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
        targetAddrField = PnlCst.addTextField(this,LoociConstants.ADDR_LOCAL, PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        targetInstIdLabel = PnlCst.addLabel(this,"Instance id of target:",PnlCst.xquart0, yIndex, PnlCst.widthHalf);
        targetInstIdField = PnlCst.addTextField(this,"1", PnlCst.xquart2, yIndex, PnlCst.widthHalf);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
        
        sendButton = PnlCst.addButton(this, this, "send event", "send",PnlCst.xquart2, yIndex, PnlCst.widthHalf , PnlCst.HEIGHT);
        yIndex += PnlCst.HEIGHT + PnlCst.BUFFERSPACE;
	}
	
	
	
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("send")){
			
			short eventId = Short.parseShort(eventTypeField.getText());
			String eventContentType = (String)eventContentTypeField.getSelectedItem();
			String eventContent = eventContentField.getText();
			
			byte[] payload = Utils.createByteArrayFromTypeString(eventContentType, eventContent);
			
			Event ev = new Event(eventId, payload);
			
			if(needsReplyBox.isSelected()){
				//ev.setRequiresReply();
			}
			
			if(isDirectedBox.isSelected()){
				//ev.setDirected();
				
				try {
					ev.setDestinationAddress(targetAddrField.getText());
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
				byte targetInstance = Byte.parseByte(targetInstIdField.getText());
				//ev.setDestinationComp(targetInstance);
							
			}
			
			//api.sendEvent(ev);
						
		}
	}
	
	public void setInstanceInfo(InstanceInfo info) {
		targetAddrField.setText(info.getComponentInfo().getNodeId());
		targetInstIdField.setText(info.getInstanceId()+"");		
	}





	@Override
	public void notifySelection(String selection, Object object) {
		if(selection.equals(SelectionListener.SELECT_INSTANCE)){
			setInstanceInfo((InstanceInfo)object);
		}
	}





	@Override
	public void initPanel(GuiInterface handler) {
		
	}





	@Override
	public void destroyPanel(GuiInterface handler) {
		
	}
}
