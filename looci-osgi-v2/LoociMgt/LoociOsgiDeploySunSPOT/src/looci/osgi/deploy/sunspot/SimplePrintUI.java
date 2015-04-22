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

package looci.osgi.deploy.sunspot;

import com.sun.spot.client.IUI;

/*
 * Utility class that simply forwards the communication from the SPOTClient
 * towards System.out
 * 
 */
public class SimplePrintUI implements IUI {

    private int progressSteps = 0;

    private void print(String msg) {
        System.out.println("[SunSPOT][Client] " + msg);
    }

    public void diagnostic(String msg) {
        print("Diagnostics: " + msg);
    }

    public void echoFromTarget(String msg) {
        print("Echo: " + msg);
    }

    public void info(String msg) {
        print("Info: " + msg);
    }

    public void newProgress(int initialSteps, int totalSteps, String title) {
        print("Starting lengthy operation: " + title);
        print("(completed " + initialSteps + " of " + totalSteps + ")");
        progressSteps = totalSteps;
    }

    public void progressEnd(String msg) {
        print("End of lengthy operation: " + msg);
    }

    public void progressUpdate(int stepsComplete, String msg) {
        print(msg + " (completed " + stepsComplete + " of " + progressSteps + ")");
    }

    public void quit() {
    }
}