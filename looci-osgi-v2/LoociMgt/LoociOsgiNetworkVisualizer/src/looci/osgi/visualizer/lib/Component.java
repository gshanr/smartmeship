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
/* Class: Component.
 * Source: GridKit - OpenCOMJ.
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.StringTokenizer;
import java.util.Vector;

public class Component{

	public double x;
	public double y;
	public double dx;
	public double dy;
	public int w;
	public int h;
	int      mouseX;
	int      mouseY;

	int interfaceCount;
	int receptacleCount;

	boolean fixed;
	boolean mouseOver;
	public String lbl; /* Label */
	int type;
	Vector<String> Interfaces;
	Vector<String> Receptacles;

	Vector<point> IntfPoints;
	Vector<point> RecpPoints;

	//animated leave stuff:
	boolean componentLeaving;
	boolean animationFinished;
	Color animColour;
	int animFadeColour;

	Component(double x,double y,String lbl, int type, String[] interfaces, String[] receptacles){
		animColour = Color.CYAN;
		Interfaces = new Vector<String>();
		Receptacles = new Vector<String>();
		IntfPoints = new Vector<point>();
		RecpPoints = new Vector<point>();

		//set-up animation fade-in color
		animFadeColour = 255;

		this.x=x;
		this.y=y;
		this.w=lbl.length()*8;
		if(w<100)
			w=100;

		this.lbl=lbl;
		mouseX=0;
		mouseY=0;
		mouseOver=false;
		this.type=type;

		int count=0;
		for(int i=0; i<interfaces.length; i++){
			Interfaces.add(interfaces[i]);
			String intf = interfaces[i];
			StringTokenizer st = new StringTokenizer(intf,".");
			String result=intf;   
			while(st.hasMoreElements()){
				result=st.nextToken();
			}
			point pIn = new point(x-15 ,y+(5+(count*15)), result);
			IntfPoints.add(pIn);
			count++;
		}
		interfaceCount = count;

		if(count<=5)
			this.h=80;
		else
			this.h=80+((count-4)*10);

		count=0;
		for(int i=0; i<receptacles.length; i++){
			Receptacles.add(receptacles[i]);
			String recp = receptacles[i];
			StringTokenizer st = new StringTokenizer(recp,".");
			String result=recp;   
			while(st.hasMoreElements()){
				result=st.nextToken();
			}
			point pRe = new point(x-15 ,y+(5+(count*15)), result);
			RecpPoints.add(pRe);
			count++;
		}

		receptacleCount = count;
	}


	public point getPoint(String iid, int type){

		if(type==0){
			for(int i=0;i<IntfPoints.size();i++){
				point tmp = IntfPoints.get(i);
				if(tmp.name.equalsIgnoreCase(iid)){
					point ret = new point(x-15,y+(5+(i*15)),iid) ;
					return ret;
				}
			}
			//System.out.println("WARNING: (0) Couldn't find interface point for iid = " + iid + " (my comp label is " + lbl + ")");
		}
		if(type==1){
			for(int i=0;i<RecpPoints.size();i++){
				point tmp = RecpPoints.get(i);
				if(tmp.name.equalsIgnoreCase(iid)){
					point ret = new point(x+w+10,y+(5+(i*15)),iid) ;
					return ret;
				}
			}
			//System.out.println("WARNING: (1) Couldn't find receptacle point for iid = " + iid + " (my comp label is " + lbl + ")");
		}
		return null;
	}

	public boolean hasAnimationFinished()
	{
		return animationFinished;
	}

	public void paint(Graphics g,FontMetrics fm) {
		//w = fm.stringWidth(lbl) + 10;
		//h = fm.getHeight() + 4;
		Graphics2D g2d=(Graphics2D) g;
		int x = (int)this.x-w/2;
		int y = (int)this.y-h/2;

		int alpha = 255;


		if(type==0)
		{
			int targetRed = animColour.getRed();
			int targetGreen =animColour.getGreen();
			int targetBlue = animColour.getBlue();

			if (animFadeColour > targetRed)
				targetRed = animFadeColour;

			if (animFadeColour > targetGreen)
				targetGreen = animFadeColour;

			if (animFadeColour > targetBlue)
				targetBlue = animFadeColour;

			g2d.setColor(new Color(targetRed, targetGreen, targetBlue));

			if (animFadeColour > 0)
				animFadeColour -= 5;
		}
		else
			g2d.setColor(Color.YELLOW);

		g2d.setStroke(new BasicStroke(2));
		g.fillRoundRect(x,y,w,h,5,5);
		g2d.setColor(new Color(0, 0, 0, alpha));
		g.drawRoundRect(x,y,w,h,5,5);
		g.drawString(lbl, x+15, y+30 + fm.getAscent());

		//for each interface
		int count=0;
		for(int i=0; i<IntfPoints.size();i++){
			point tmp = IntfPoints.get(i);
			String intf = tmp.name;

			g.setColor(new Color(0, 0, 0, alpha));
			g.drawLine(x, y+(10+(count*15)), x-12, y+(10+(count*15)));
			g.setColor(new Color(Color.GRAY.getRed(), Color.GRAY.getGreen(), Color.GRAY.getBlue(), alpha));

			g.fillOval((int)x-15,(int)y+(5+(count*15)),10,10);
			g.setColor(new Color(0, 0, 0, alpha));
			g.drawOval((int)x-15,(int)y+(5+(count*15)),10,10);

			if(intf.length()<15)
				g.drawString(intf, (int) x-45-(4*intf.length()), (int) y+(13+(count*15)) );
			else if(intf.length()<30)
				g.drawString(intf, (int) x-60-(4*intf.length()), (int) y+(13+(count*15)) );
			else
				g.drawString(intf, (int) x-60-(6*intf.length()), (int) y+(13+(count*15)) );
			count++;
		}
		count = 0;
		for(int i=0; i<RecpPoints.size();i++){
			point tmp = RecpPoints.get(i);

			String recp = tmp.name;
			g.setColor(new Color(0, 0, 0, alpha));
			g.drawLine(x+w, y+(10+(count*15)), x+w+15, y+(10+(count*15)));
			g.setColor(new Color(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), alpha));

			g.fillRect((int)x+w+10,(int)y+(5+(count*15)),10,10);
			g.setColor(new Color(0, 0, 0, alpha));
			g.drawRect((int)x+w+10,(int)y+(5+(count*15)),10,10);
			g.drawString(recp, (int) x+w+20, (int) y+(13+(count*15)) );
			count++;
		}
	}
}