/**
 * LooCI Copyright (C) 2013 KU Leuven.
 * All rights reserved.
 *
 * LooCI is an open-source software development kit for developing and 
 * maintaining networked embedded applications;
 * it is distributed under a dual-use software license model:
 *
 * 1. Non-commercial use:
 * Non-Profits, Academic Institutions, and Private Individuals can redistribute 
 * and/or modify LooCI code under the terms of the GNU General Public License 
 * version 3, as published by the Free Software Foundation
 * (http://www.gnu.org/licenses/gpl.html).
 *
 * 2. Commercial use:
 * In order to apply LooCI in commercial code, a dedicated software license must 
 * be negotiated with KU Leuven Research & Development.
 *
 * Contact information:
 *  Administrative Contact: Sam Michiels, sam.michiels@cs.kuleuven.be
 *  Technical Contact:           Danny Hughes, danny.hughes@cs.kuleuven.be
 * Address:
 *  iMinds-DistriNet, KU Leuven
 *  Celestijnenlaan 200A - PB 2402,
 *  B-3001 Leuven,
 *  BELGIUM. 
 **/

package looci.osgi.mgtTerminal;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JTextArea;

import looci.osgi.servExt.mgt.ServiceClient;

/**
 *
 * @author klaas
 */
public class EnterAction extends AbstractAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1537643243725465730L;
	private JTextArea area;
    private ServiceClient client;
    private History history;

    public EnterAction(JTextArea area, ServiceClient client, History history) {
        this.area = area;
        this.client = client;
        this.history = history;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String text = area.getText();
        int index = text.lastIndexOf('\n') + 1;
        String command = text.substring(index, text.length());
        
        area.append("\n>> " + client.process(command) + "\n");
        area.getCaret().setDot(area.getText().length());
        
        history.add(command);
        history.reset();
    }
}
