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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import javax.swing.text.AbstractDocument;

import looci.osgi.gui.serv.LoociPanel;
import looci.osgi.servExt.mgt.ServiceClient;

/**
 *
 * @author klaas
 */
public class Terminal extends LoociPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 989804347244358138L;
	private ServiceClient client;
    private History history;

    public Terminal(ServiceClient client) {
        super(new GridBagLayout());
        this.client = client;
        this.history = new History();
        initUI();
    }

    public final void initUI() {
        final JTextArea area = new JTextArea(20,60);
        area.setFont(new Font("Monospaced",Font.PLAIN,12));
        area.setLineWrap(true);

        ((AbstractDocument)area.getDocument()).setDocumentFilter(new Filter(area));

        MouseListener mouseListener = new MouseAdapter() {

            public void mousePressed(MouseEvent e) {}

            public void mouseClicked(MouseEvent e) {
                area.getCaret().setDot(area.getText().length());
            }

            public void mouseReleased(MouseEvent e) {}
        };
        area.addMouseListener(mouseListener);

        EnterAction enterAction = new EnterAction(area, client, history);
        area.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "enter");
        area.getActionMap().put("enter", enterAction);

        UpAction upAction = new UpAction(area, history);
        area.getInputMap().put(KeyStroke.getKeyStroke("UP"), "up");
        area.getActionMap().put("up", upAction);

        DownAction downAction = new DownAction(area, history);
        area.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down");
        area.getActionMap().put("down", downAction);

        JScrollPane scrollPane = new JScrollPane(area);
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        
        add(scrollPane, c);
        area.append(client.getWelcomeMessage());
        area.getCaret().setDot(area.getText().length());
    }
}