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
package looci.osgi.visualizer.lib;
/* Class: Link.
 * Source: GridKit - OpenCOMJ.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

public class GraphPanel extends Panel implements Runnable, MouseListener, MouseMotionListener {

	static final long serialVersionUID = 200010003001L;

	// List of components in the current panel - MAX 100 (Frameworks should ensure this isn't exceeded
	Component components[] = new Component[100];
	Component pick;
	int nComponents; // number of components

	// List of connection in the current panel - MAX 200
	Link Links[] = new Link[200];
	int nLinks; // number of links

	Thread relaxer;
	boolean random;
	boolean pickfixed;
	Image offscreen,image,image1;
	Dimension offscreensize;
	Graphics offgraphics;

	//vector to hold components that have been removed from the configuration, to animate their departure
	Vector<Component> removedComponents;

	//number specifying how long to keep trying to position components for (goes up to MAX_POSITION_TIME)
	// - reset this to zero to re-do positioning for a while
	int positioningCounter = 0;
	int MAX_POSITION_TIME = 100;

	GraphPanel() {
		addMouseListener(this);
		nComponents = 0;
		nLinks = 0;
		removedComponents = new Vector<Component>();
	}

	/**
	 * Add a component to the panel
	 */
	public synchronized Component addComponent(String lbl, String[] interfaces, String[] receptacles) {
		Dimension d = getSize();
		double x= (d.width/2);   
		double y= (d.height/2);  
		Component c = new Component(x,y,lbl,0, interfaces, receptacles);
		components[nComponents] = c;// <========== CHECK FOR OUT OF NODE NUMBER = 100
		nComponents++;

		//re-set positioning timout
		positioningCounter = 0;

		return c;
	}
	
    public Component findComponent(String lbl){
    	if(lbl == null){
    		return null;
    	}
    	
        for (int i = 0 ; i < nComponents ; i++) {
             Component n = components[i];
             if(lbl.equals(n.lbl)){
            	 return n;
             }
        } 
        
        System.out.println("Returning null for findComponent: " + lbl);
        return null;
   }
	
    public synchronized void addLink(String from,String to, String iid) {
    	addLink(findComponent(from),findComponent(to), iid);
    }
    
    public synchronized void addLink(Component from,Component to, String iid) {
   	 	if(from != null && to != null && iid != null){
   	    	Link e = new Link(from,to,iid);
   		    Links[nLinks] = e;
   		    nLinks++;
   	 	}   	

     }


	public void run() {
		//Thread me = Thread.currentThread();
		while(true){//me==Thread.currentThread()){

			try {
				if(positioningCounter < MAX_POSITION_TIME){
					Thread.sleep(100);
					positioningCounter++;
					relax();
					if (random && (Math.random() < 0.03)) {
						// Pick a random component
						Component n = components[(int)(Math.random() * nComponents)]; 
						if (!n.fixed) {
							n.x += 100*Math.random() - 50;
							n.y += 100*Math.random() - 50;
						}
					}
				}
				else{
					Thread.sleep(100);
					repaint();
				}
			} catch (InterruptedException e) {}
		}
	}

	public synchronized void update(Graphics g) {
		Dimension d = getSize();

		if ((offscreen == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height)) {
			offscreen = createImage(d.width, d.height);
			offscreensize = d;
			if (offgraphics != null)offgraphics.dispose();
			offgraphics = offscreen.getGraphics();
			offgraphics.setFont(getFont());
		}

		// Draw the off screen background with Gradient Paint
		Graphics2D g2d = (Graphics2D)offgraphics;
		Color startColor =  new Color(94, 100,158);
		Color endColor = new Color(255, 235, 250);
		GradientPaint gradient = new GradientPaint(0, 0, startColor, d.width, d.height, endColor);
		g2d.setPaint(gradient);
		offgraphics.fillRect(0, 0, d.width, d.height);

		// Draw Links
		for (int i = 0 ; i < nLinks ; i++)
			Links[i].paint(offgraphics);

		// Draw components
		for (int i = 0 ; i < nComponents ; i++)
			components[i].paint(offgraphics,offgraphics.getFontMetrics());


		for (int i = removedComponents.size() - 1; i >= 0 ; i--)
		{
			if (! removedComponents.elementAt(i).hasAnimationFinished())
			{
				removedComponents.elementAt(i).paint(offgraphics,offgraphics.getFontMetrics());
			}
			else
				removedComponents.removeElementAt(i);
		}

		// Draw off screen
		g.drawImage(offscreen, 0, 0, null);
	}


	synchronized void relax() {
		for (int i = 0 ; i < nLinks ; i++) {
			Link e = Links[i];

			//null check...there's a problem sometimes when a to/from component is put in a different window, so the link can't be drawn
			if ((e.to != null) && (e.from != null))
			{
				double vx = e.to.x - e.from.x;
				double vy = e.to.y - e.from.y;
				double len = Math.sqrt(vx * vx + vy * vy);
				len = (len == 0) ? .0001 : len;
				double f = (Links[i].len - len) / (len * 3);
				double dx = f * vx;
				double dy = f * vy;

				if (e.to != null)
				{
					e.to.dx += dx;
					e.to.dy += dy;
				}

				if (e.from != null)
				{
					e.from.dx += -dx;
					e.from.dy += -dy;
				}
			}
		}


		for (int i = 0 ; i < nComponents ; i++) {
			Component n1 = components[i];

			double dx = 0;
			double dy = 0;

			for (int j = 0 ; j < nComponents ; j++) {
				if (i == j) {
					continue;
				}
				Component n2 = components[j];
				double vx = n1.x - n2.x;
				double vy = n1.y - n2.y;
				double len = vx * vx + vy * vy;
				if (len == 0) {
					dx += Math.random();
					dy += Math.random();
				} else if (len < 500*250) {
					dx += vx / len;
					dy += vy / len;
				}
			}
			double dlen = dx * dx + dy * dy;
			if (dlen > 0) {
				dlen = Math.sqrt(dlen) / 2;
				n1.dx += dx / dlen*1.5;
				n1.dy += dy / dlen*1.5;
			}
		}

		Dimension d = getSize();

		for (int i = 0 ; i < nComponents ; i++) {
			Component n = components[i];
			if (!n.fixed) {
				n.x += Math.max(-5, Math.min(5, n.dx));
				n.y += Math.max(-5, Math.min(5, n.dy));
			}
			if (n.x < n.w/2) {
				n.x = n.w/2;
			} else if (n.x > d.width-n.w/2) {
				n.x = d.width-n.w/2;
			}
			if (n.y < n.h/2) {
				n.y =  n.h/2;
			} else if (n.y > d.height- n.h/2) {
				n.y = d.height- n.h/2;
			}
			n.dx /= 2;
			n.dy /= 2;
		}
		repaint();
	}
	//
	// start
	//
	public void start() {
		relaxer = new Thread(this);
		relaxer.start();
	}
	//
	// stop
	//
	public void stop() {
		relaxer = null;
	}

	//
	// mouseClicked
	//
	public void mouseClicked(MouseEvent e) {    

//	    if (e.getClickCount() == 2){}
//	    else
//	        return;
//	    
//	    	int x = e.getX();
//		int y = e.getY();
//	        for (int i = 0 ; i < nComponents ; i++) {
//	            Component n = components[i];
//	            if(n.type==1){
//	                boolean xRange = ((x>=n.x)&&(x<(n.x+n.w)));
//	                boolean yRange = ((y<=n.y)&&(y>(n.y-n.h)));
//	                if (xRange&&yRange) {
//	                    // Framework --> New Graph
//	                    if (localSnapshotMode)
//	                       visualise(n.myMeta, n.lbl);
//	                       else
//	                       {
//	                       //find the framework window and show it
//	                       System.out.println("User clicked on a framework; searching for matching window");
//	                        for (int k = 0; k < subWindowList.size(); k++)
//	                           {
//	                           if (subWindowList.elementAt(k).frameworkName.indexOf(n.lbl) != -1)
//	                              {
//	                              System.out.println("...found matching window");
//	                              subWindowList.elementAt(k).setVisible(true);
//	                              break;
//	                              }
//	                           }
//	                       }
//	                }
//	            }
//	        }
	   
	}

//	public void visualise(ICFMetaInterface pMeta, String label){
//	        VisualGraph g = new VisualGraph(pIOpenCOM, label);
//	        g.setVisible(true);
//
//	        Vector<IUnknown> pComps  = new Vector<IUnknown>();
//	        int noComps = pMeta.get_internal_components(pComps);
//	  
//	        // Add compoenent to the graph
//	        for(int i=0;i<noComps;i++){
//	            IUnknown pComp = pComps.get(i);
//	            String name = pMeta.getComponentName(pComp);
//	            //String name = iOpenCOM.getComponentName(pComp);
//	            // If its not primitive -- ignore
//	            
//	            // Detect if its a framework
//	            ICFMetaInterface iFrameworkMeta = (ICFMetaInterface) pComp.QueryInterface("OpenCOM.ICFMetaInterface");
//	            if(iFrameworkMeta==null){
//	                g.panel.addComponent(name, pComp);
//	            }
//	            //else add framework
//	            else{
//	                g.panel.addFramework(name, pComp);
//	            }
//	        }
//	  
//	        for(int i=0;i<noComps;i++){
//	            IUnknown pComp = pComps.get(i);
//
//	            Vector<OCM_RecpMetaInfo_t> ppRecps = new Vector<OCM_RecpMetaInfo_t>();
//	            IMetaInterface pMetaIntf =  (IMetaInterface) pComp.QueryInterface("OpenCOM.IMetaInterface");
//	            int noRecps = pMetaIntf.enumRecps(ppRecps);
//	            for (int j=0; j<noRecps; j++){
//	                OCM_RecpMetaInfo_t temp = ppRecps.elementAt(j);
//	                Vector<Long> Recplist = new Vector<Long>();
//	                IMetaArchitecture pMetaArch = (IMetaArchitecture) pIOpenCOM.QueryInterface("OpenCOM.IMetaArchitecture");
//	                int noConns = pMetaArch.enumConnsFromRecp(pComp, temp.iid, Recplist);
//	                for(int k=0;k<noConns;k++){
//	                    OCM_ConnInfo_t TempConnInfo = pIOpenCOM.getConnectionInfo(Recplist.get(k).longValue());
//	                    // If they are both in this domain --> Connect them
//	                    boolean source = false;
//	                    boolean sink = false;
//	                    for(int c = 0; c<noComps;c++){
//	                        String name = pIOpenCOM.getComponentName(pComps.get(c));
//	                        if(name.equalsIgnoreCase(TempConnInfo.sourceComponentName))
//	                            source=true;
//	                        if(name.equalsIgnoreCase(TempConnInfo.sinkComponentName))
//	                            sink=true;
//	                    }
//	                    if(source&&sink)
//	                        g.panel.addLink(TempConnInfo.sourceComponentName, TempConnInfo.sinkComponentName, TempConnInfo.interfaceType);
//	                }
//	            }     
//	        }
//	    }
	//
	// mousePressed
	//
	public void mousePressed(MouseEvent e) {
		
		addMouseMotionListener(this);
		double bestdist = Double.MAX_VALUE;
		int x = e.getX();
		int y = e.getY();
		     
	   for (int i = 0 ; i < nComponents ; i++) {
		  Component n = components[i];
	    if(n!=null){
	        double dist = (n.x - x) * (n.x - x) + (n.y - y) * (n.y - y);
	        if (dist < bestdist) {
	        pick = n;
	        bestdist = dist;
	        }
	    }
	  }
	   if (pick != null)
	      {
	   	pickfixed = pick.fixed;
	   	pick.fixed = true;
	   	pick.mouseOver=false;
	   	pick.x = x;
	   	pick.y = y;
	   	repaint();
		   }
		e.consume();
	}
	//
	// mouseReleased
	//
	public void mouseReleased(MouseEvent e) {
		  removeMouseMotionListener(this);
	        if (pick != null) {
	            pick.x = e.getX();
	            pick.y = e.getY();
	            pick.fixed = pickfixed;
	            pick = null;
	        }
		repaint();
		e.consume();
	}
	//
	// mouseEntered
	//
	public void mouseEntered(MouseEvent e) {   addMouseMotionListener(this); }
	//
	// mouseExited
	//
	public void mouseExited(MouseEvent e) {  removeMouseMotionListener(this);  }
	//
	// mouseDragged
	//
	public void mouseDragged(MouseEvent e) {
		pick.x = e.getX();
		pick.y = e.getY();
		repaint();
		e.consume();
	}
	//
	// mouseMoved
	//
	public void mouseMoved(MouseEvent e) { 
	  
	 	int x = e.getX();
		int y = e.getY();
		
	  for (int i = 0 ; i < nComponents ; i++) {
		  Component n = components[i];
	    if(n!=null){
	        int nx=(int)n.x-n.w/2;
	        int ny=(int)n.y-n.h/2;
	        if(x>=nx&&x<=(nx+n.w)&&y>=ny&&y<=(ny+n.h)){
	            n.mouseOver=true;
	            n.mouseX=x;
	            n.mouseY=y;
	        }
	        else {
	              n.mouseOver=false;
	              n.mouseX=x;
	              n.mouseY=y;
	        }
	   }
	  }

		e.consume();
	}

}// End class
