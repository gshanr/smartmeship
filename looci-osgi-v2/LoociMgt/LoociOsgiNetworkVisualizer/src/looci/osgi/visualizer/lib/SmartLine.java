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
/* Class: SmartLine, used to calculate the intersection point between a line and a rectangle.
 * Source: SVG Unleashed
 */

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class SmartLine extends Line2D.Double{
	
	private static final long serialVersionUID = 4624302486637732188L;

	private Rectangle2D rectangle;
	private double x;
	private double y;

	public SmartLine() {
		super();
	}

	public void setRect(int cx, int cy, int width, int height) { 
		rectangle = new Rectangle2D.Double(cx, cy, width, height);
		calculateRectangleIntersection();
	}

	public double getX(){
		return x;
	}

	public double getY(){
		return y;
	}

	public double getXDistance() { 
		return getP1().getX() - getP2().getX();
	}

	public double getYDistance() {
		return getP1().getY() - getP2().getY();
	}
	
	@SuppressWarnings("unused")
	private double getYValue() {
		return (rectangle.getHeight() / 2) * ((rectangle.getWidth() / 2) / getXDistance());
	}

	@SuppressWarnings("unused")
	private double getXValue() {
		return (rectangle.getWidth() / 2) * ((rectangle.getHeight() / 2) / getYDistance());
	}

	private void calculateRectangleIntersection() {

		double yValue = (getYDistance())  * ((rectangle.getWidth() / 2) / getXDistance());
		double xValue = (getXDistance())  * ((rectangle.getHeight() / 2) / getYDistance());

		boolean eastside  = (Math.abs(yValue) < rectangle.getHeight() / 2)  && (getXDistance() >= 0);
		boolean westside  = (Math.abs(yValue) < rectangle.getHeight() / 2)  && (getXDistance() < 0);
		boolean northside = (Math.abs(xValue) < rectangle.getWidth()  / 2)  && (getYDistance() < 0);
		boolean southside = (Math.abs(xValue) < rectangle.getWidth()  / 2)  && (getYDistance() >= 0);

		if (westside){
			x = rectangle.getMinX();
			y = rectangle.getCenterY() - yValue;
		}

		if (eastside) { // right
			x = rectangle.getMaxX();
			y = rectangle.getCenterY() + yValue;
		}

		if (northside) { // top
			x = rectangle.getCenterX() - xValue;
			y = rectangle.getMinY();
		}

		if (southside) { // bottom
			x = rectangle.getCenterX() + xValue;
			y = rectangle.getMaxY();
		}

	}
}