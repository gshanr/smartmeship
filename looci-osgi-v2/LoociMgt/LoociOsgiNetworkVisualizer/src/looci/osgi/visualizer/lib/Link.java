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
import java.awt.Graphics;
import java.util.StringTokenizer;

public class Link {

	public Component from;
	public Component to;
	public double len; // Lenght of the Link
	final Color arcColor1 =   Color.black;
	final Color arcColor2 =   Color.pink;
	final Color arcColor3 =   Color.red;
	public String Label;

	public Link(Component from, Component to, String iid){

		this(from,to,140,iid);

	}    

	public Link(Component from, Component to, int len, String iid){
		this.from=from;
		this.to=to;
		this.len=len;
		this.Label = iid;
		StringTokenizer st = new StringTokenizer(Label,".");
		String result=Label;   
		while(st.hasMoreElements()){
			result=st.nextToken();
		}
		Label=result;

		// System.out.println("New Link constructor, this.from = " + this.from + ", this.to = " + this.to + ", Label = " + Label);
	}    

	public void paint(Graphics g) {
		try{
			point fr = from.getPoint(Label, 1);

			//if (fr == null)
			//System.out.println("WARNING: fr is null");

			double wf = from.w;
			double hf = from.h;
			double xf = fr.x-wf-wf/2+10;
			double yf = fr.y-hf/2-10;

			point cTo = to.getPoint(Label, 0);

			//if (cTo == null)
			//System.out.println("WARNING: cTo is null");

			double wt = to.w;
			double ht = to.h;
			double xt = cTo.x-wt/2;
			double yt = cTo.y-ht/2-10;

			//if (g == null)
			//System.out.println("WARNING: g is null");

			g.setColor(arcColor1);

			double mixX=10.0;
			double x1,x2,x3,x4,x5,x6,x7,x8;
			double y1,y2,y3,y4,y5,y6,y7,y8;

			// from
			x1=xf+wf;
			y1=yf+(hf*0.2);
			// to
			x8=xt;
			y8=yt+(ht*0.2);

			// distance
			double distance=Math.sqrt(((x8-x1)*(x8-x1))+((y8-y1)*(y8-y1)));
			// Intermediate
			x2=0;
			if(x8>x1) x2=x1+(distance*0.1);
			else x2=x1+mixX;
			y2=y1;
			x7=0;
			if(x8>x1)x7=x8-(distance*0.1);
			else x7=x8-mixX;
			y7=y8;

			// Initilize the path string to empty
			String path = "";

			path += x1+","+y1+":";
			path += x2+","+y2+":";

			if(x7<x2&&y7>y2){
				x3=x2;
				y3=y2+(hf*0.9);
				x6=x7;
				y6=y7-(hf*0.3);
				double w1=(wf*1.2);
				double w2=(wt*1.2);
				path += x3+","+y3+":";
				if(y6<y3)if(Math.abs(x6-x3)>(w1+w2)){
					x4=x3-w1;
					y4=y3;
					x5=x6+w2;
					y5=y6;
					if(x5>x4)x5=x4;
					path += x4+","+y4+":";
					path += x5+","+y5+":";
				}else {
					y6=y7+(hf*0.9);
					y3=y6;
					path += x3+","+y3+":";
				}
				path += x6+","+y6+":";
			}

			if(x7<x2&&y7<y2){
				x3=x2;
				y3=y2-(hf*0.3);
				x6=x7;
				y6=y7+(hf*0.9);
				double w1=(wf*1.2);
				double w2=(wt*1.2);
				path += x3+","+y3+":";
				if(y6>y3)if(Math.abs(x6-x3)>(w1+w2)){
					x4=x3-w1;
					y4=y3;
					x5=x6+w2;
					y5=y6;
					if(x5>x4)x5=x4;
					path += x4+","+y4+":";
					path += x5+","+y5+":";
				}else {
					y6=y7-(hf*0.3);
					y3=y6;
					path += x3+","+y3+":";
				}
				path += x6+","+y6+":";
			}

			path += x7+","+y7+":";
			path += x8+","+y8;

			int[] xPoints=new int[8];
			int[] yPoints=new int[8];
			int count = 0;
			String[] pairs = path.split(":");
			for(int i=0;i<pairs.length;i++){
				String[] xy = pairs[i].split(",");
				int x=(int)Double.parseDouble(xy[0]);
				int y=(int)Double.parseDouble(xy[1]);
				xPoints[count]=x;
				yPoints[count]=y;
				count++;
			}

			g.setColor(Color.WHITE);
			g.drawPolyline(xPoints,yPoints,count);
		}
		catch(Exception e){
			//System.out.println("To= "+pIOpenCOM.getComponentName(to.pComp)+" From "+pIOpenCOM.getComponentName(from.pComp)+" Label= "+Label);
			//e.printStackTrace();
		}
	}
}// End class Link
