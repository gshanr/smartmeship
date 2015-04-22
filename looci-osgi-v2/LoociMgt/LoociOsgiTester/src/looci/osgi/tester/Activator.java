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

package looci.osgi.tester;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.net.UnknownHostException;
import java.util.ArrayList;

import looci.osgi.servExt.mgt.IDeployerAPI;
import looci.osgi.servExt.mgt.ILoociAPI;
import looci.osgi.servExt.mgt.LoociCommandList;
import looci.osgi.servExt.mgt.ServiceClient;

public class Activator implements BundleActivator {

    private static BundleContext context;
    private ILoociAPI loociApi;
    private IDeployerAPI deployerApi;
    private GUI gui;
    
    private Test test;
    private TestConfig config;

    static BundleContext getContext() {
        return context;
    }

    /*
     * (non-Javadoc) @see
     * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext bundleContext) throws Exception {

        Activator.context = bundleContext;

        TestConfigParser parser = new TestConfigParser();
        config = parser.parseFromFile("conf/test.xml");
        
        ServiceReference[] apiRefs = context.getServiceReferences(ILoociAPI.class.getName(), null);
        if (apiRefs == null || apiRefs.length == 0) {
            System.out.println("no LooCI mgt API found");
            throw new IllegalStateException();
        }

        ServiceReference[] deployRefs = context.getServiceReferences(IDeployerAPI.class.getName(), null);
        if (deployRefs == null || deployRefs.length == 0) {
            System.out.println("no LooCI deployment API found");
            throw new IllegalStateException();
        }

        loociApi = (ILoociAPI) Activator.context.getService(apiRefs[0]);
        deployerApi = (IDeployerAPI) Activator.context.getService(deployRefs[0]);

        LoociCommandList commands = new LoociCommandList(deployerApi, loociApi);
        ServiceClient client = new ServiceClient(commands);

        System.out.println("[LooCI Standard Test] Activator started.");

        gui = new GUI(this);
        
        test = new Test(gui, client, loociApi, deployerApi,config);

    }

    private boolean goOn = true;
    
    public void startTest(){
    	goOn = true;
        ArrayList<TestResult> results = new ArrayList<TestResult>();
        
        if(config.getDoFreshNodeTest() && goOn){
            results.add(test.doFreshNodeTest());
        }
        if(config.getDoCodebaseTest() && goOn){
            results.add(test.doCodebaseTest());
        }
        if(config.getDoComponentTest() && goOn){
            results.add(test.doComponentTest());
        }
        if(config.getDoPropertiesTest() && goOn){
            results.add(test.doPropertiesTest());
        }
        if(config.getDoWireTest() && goOn){
            try {
				results.add(test.doWiringTest());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
        }
        if(config.getDoMiscTest() && goOn){
            results.add(test.doMiscellaneousTest());
        }

        int totalSucceeded = 0;
        int totalNrOfTests = 0;
        gui.print("========================================");
        gui.print("[LooCI Standard Test] FINAL TEST REPORT:");
        gui.print("========================================");
        String line;
        for (TestResult result : results) {
            line = String.format("%-20s" + "\t" + result.getSucceeded() + "/" + result.getNrOfTests(), result.getTestName() + ":");
            gui.print(line);
            totalSucceeded += result.getSucceeded();
            totalNrOfTests += result.getNrOfTests();
        }
        gui.print("========================================");
        String verdict = totalSucceeded == totalNrOfTests ? "TEST PASSED" : "TEST FAILED";
        line = String.format("%-20s" + "\t" + totalSucceeded + "/" + totalNrOfTests + "\t" + verdict, "Overall result:");
        gui.print(line);
        gui.print("========================================");
    }
    
    public void stopTest(){
    	goOn = false;
    }
    
    /*
     * (non-Javadoc) @see
     * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext bundleContext) throws Exception {
        test.kill();
        Activator.context = null;
    }
}
