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

import java.util.Vector;

/**
 *
 * @author klaas
 */
public class History {
    
    
    private int currentPosition;
    
    private Vector<String> history;
    
    private boolean goingBackwards; // stores direction of previous movement
    
    public History() {
        this.currentPosition = 0;
        this.history = new Vector<String>();
        this.goingBackwards = false;
        
        history.add(""); // placeholder for empty string at position 0 of history
    }
    
    public void add(String line) {
        history.add(1, line);
    }
    
    public String getPrevious() {
        if (!goingBackwards) {
            currentPosition++;
            goingBackwards = true;
        }
        
        if (currentPosition == history.size()) {
            return (String)history.elementAt(currentPosition - 1);
        } else {
            return (String)history.elementAt(currentPosition++);
        }
    }
    
    public String getNext() {
        if (goingBackwards) {
            currentPosition--;
            goingBackwards = false;
        }
        
        if (currentPosition == 0) {
            return null;
        } else {
            return (String)history.elementAt(--currentPosition);
        }
    }
    
    public void reset() {
        currentPosition = 0;
        goingBackwards = false;
    }
}
