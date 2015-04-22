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
package looci.osgi.app.tutorialBackend;
/*
 * DataWindow.java
 *
 * Copyright (c) 2008 Sun Microsystems, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.text.DateFormat;
import java.util.Date;

/**
 * Create a new window to graph the sensor readings in.
 *
 * @author Ron Goldman
 */
public class DataWindow extends JFrame implements ActionListener {

    private static final int MAX_SAMPLES = 10000;
    private int index = 0;
    private long[] time = new long[MAX_SAMPLES];
    private int[] val = new int[MAX_SAMPLES];
    DateFormat fmt = DateFormat.getDateTimeInstance();
    
    double tscale = 1.0 / 2000.0;           // 1 pixel = 2 seconds = 2000 milliseconds
    

    /** Creates new form DataWindow */
    public DataWindow() {
        initComponents();
    }

    public DataWindow(String ieee) {
        initComponents();
        setTitle(ieee);
    }
    
    public void addData(long t, int v) {
        time[index] = t;
        val[index++] = v;
        dataTextArea.append(fmt.format(new Date(t)) + "    value = " + v + "\n");
        dataTextArea.setCaretPosition(dataTextArea.getText().length());
        repaint();
    }
    
    // Graph the sensor values in the dataPanel JPanel
    public void paint(Graphics g) {
        super.paint(g);
        int left = dataPanel.getX() + 10;       // get size of pane
        int top = dataPanel.getY() + 30;
        int right = left + dataPanel.getWidth() - 20;
        int bottom = top + dataPanel.getHeight() - 20;
        
        int y0 = bottom - 20;                   // leave some room for margins
        int yn = top;
        int x0 = left + 33;
        int xn = right;
        double vscale = (yn - y0) / 50.0;      // temp values range from 0 to 50
        
        // draw X axis = time
        g.setColor(Color.BLACK);
        g.drawLine(x0, yn, x0, y0);
        g.drawLine(x0, y0, xn, y0);
        int tickInt = 60 / 2;
        for (int xt = x0 + tickInt; xt < xn; xt += tickInt) {   // tick every 1 minute
            g.drawLine(xt, y0 + 5, xt, y0 - 5);
            int min = (xt - x0) / (60 / 2);
            g.drawString(Integer.toString(min), xt - (min < 10 ? 3 : 7) , y0 + 20);
        }
        
        // draw Y axis = sensor reading
        g.setColor(Color.BLUE);
        for (int vt = 50; vt > 0; vt -= 10) {         // tick every 10
            int v = y0 + (int)(vt * vscale);
            g.drawLine(x0 - 5, v, x0 + 5, v);
            g.drawString(Integer.toString(vt), x0 - 38 , v + 5);
        }

        // graph sensor values
        int xp = -1;
        int vp = -1;
        for (int i = 0; i < index; i++) {
            int x = x0 + (int)((time[i] - time[0]) * tscale);
            int v = y0 + (int)(val[i] * vscale);
            if (xp > 0) {
                g.drawLine(xp, vp, x, v);
            }
            xp = x;
            vp = v;
        }
    }
    
    public void setMotion(boolean motion){
    	if(motion){
    		motionLabel.setText("button pressed at: "+fmt.format(new Date(System.currentTimeMillis())));
    		motionLabel.setBackground(Color.GREEN);
    	} else{
    		motionLabel.setText("no button press detected");
    		motionLabel.setBackground(Color.RED);
    	}
    }
    
    public void setDoor(boolean door){
    	if(door){
    		doorLabel.setText("SWITCH OPEN");
    		doorLabel.setBackground(Color.RED);
    	} else{
    		doorLabel.setText("SWICH CLOSED");
    		doorLabel.setBackground(Color.GREEN);
    	}
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dataPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTextArea = new javax.swing.JTextArea();

        dataPanel.setBackground(new java.awt.Color(255, 255, 255));
        dataPanel.setMinimumSize(new java.awt.Dimension(400, 250));
        dataPanel.setPreferredSize(new java.awt.Dimension(400, 250));
        getContentPane().add(dataPanel, java.awt.BorderLayout.CENTER);

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(400, 100));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(400, 100));

        dataTextArea.setColumns(20);
        dataTextArea.setEditable(false);
        dataTextArea.setRows(4);
        jScrollPane1.setViewportView(dataTextArea);
       
        
        motionLabel = new JTextArea();
        motionLabel.setPreferredSize(new Dimension(200,50));
        setMotion(false);
        
        doorLabel = new JTextArea();
        doorLabel.setPreferredSize(new Dimension(200,50));
        setDoor(false);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(jScrollPane1);
        panel.add(motionLabel);
        panel.add(doorLabel);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.LINE_AXIS));
        
        JButton resetBtn = new JButton("reset");
        resetBtn.addActionListener(this);
        resetBtn.setActionCommand("reset");
        
        JButton zoomInBtn = new JButton("zoom in");
        zoomInBtn.addActionListener(this);
        zoomInBtn.setActionCommand("zoomIn");
        
        JButton zoomOutBtn = new JButton("zoom out");
        zoomOutBtn.addActionListener(this);
        zoomOutBtn.setActionCommand("zoomOut");
        
        JButton resetPress = new JButton("resetPress");
        resetPress.addActionListener(this);
        resetPress.setActionCommand("resetPress");
        
        buttonPanel.add(resetBtn);
        buttonPanel.add(zoomInBtn);
        buttonPanel.add(zoomOutBtn);
        buttonPanel.add(resetPress);
        panel.add(buttonPanel);
  
        

        getContentPane().add(panel,java.awt.BorderLayout.SOUTH);
        
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel dataPanel;
    private javax.swing.JTextArea dataTextArea;
    private javax.swing.JScrollPane jScrollPane1;
    
    private JTextArea motionLabel;
    private JTextArea doorLabel;
    // End of variables declaration//GEN-END:variables

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getActionCommand().equals("reset")){
			index = 0;
			repaint();
		} else if(arg0.getActionCommand().equals("zoomIn")){
			tscale *= 2;
			repaint();
			
		} else if(arg0.getActionCommand().equals("zoomOut")){
			tscale *= 0.5;
			repaint();
		} else if(arg0.getActionCommand().equals("resetPress")){
			setMotion(false);
		} 
	}

	
	public void killAll(){
		this.setVisible(false);
		this.dispose();
	}
}
