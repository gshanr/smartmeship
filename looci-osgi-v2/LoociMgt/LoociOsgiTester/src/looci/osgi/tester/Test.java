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

import java.net.UnknownHostException;
import java.util.Arrays;

import looci.osgi.serv.constants.ErrorCodes;
import looci.osgi.serv.constants.EventTypes;
import looci.osgi.serv.constants.LoociConstants;
import looci.osgi.serv.constants.LoociManagementException;
import looci.osgi.serv.constants.LoociRuntimes;
import looci.osgi.serv.util.Utils;
import looci.osgi.servExt.mgt.IDeployerAPI;
import looci.osgi.servExt.mgt.ILoociAPI;
import looci.osgi.servExt.mgt.MgtReply;
import looci.osgi.servExt.mgt.ServiceClient;


/**
 *
 * @author klaas
 */
public class Test {
    
    //private ServiceClient client;
    private ILoociAPI mgmt;
    private IDeployerAPI deployer;

    private GUI gui;
       
    private String platform;
    private String node_1;
    private String node_2;
    private String tempSensor;
    private String tempFilter;
    private String tempSensorName;
    private String tempFilterName;
    
    /*These should be declared elsewhere: LooCiConstants*****/
    private byte CODEBASE_ID_WILDCARD = 0;
    private byte CODEBASE_ID_RECONF_ENG = 1;
    private byte STATE_ACTIVE = 1;
    private byte STATE_INACTIVE = 0;
    /*****/
    
    public Test(GUI gui, ServiceClient client, ILoociAPI loociApi, IDeployerAPI deployerApi, TestConfig config) {

        //this.client = client;
        this.mgmt = loociApi;
        this.deployer = deployerApi;
        this.gui = gui;//new GUI();
        this.gui.setVisible(true);
        
        platform = config.getPlatform();
        node_1 = config.getNode_1();
        node_2 = config.getNode_2();
        tempSensor = config.getTempSensor();
        tempFilter = config.getTempFilter();
        tempSensorName = config.getSensorName();
        tempFilterName = config.getFilterName();
    	
        gui.print("\n[LooCI Standard Test] Configuration: ");
        gui.print("platform:\t\t" + platform);
        gui.print("node_1:\t\t" + node_1);
        gui.print("node_2:\t\t" + node_2);
        gui.print("tempSensor:\t\t" + tempSensor);
        gui.print("tempSensorName:\t" + tempSensorName);
        gui.print("tempFilter:\t\t" + tempFilter);
        gui.print("tempFilterName:\t" + tempFilterName);
        gui.print("====================================\n");
        
    }
    
    public void kill() {
        gui.dispose();
    }
    
    public TestResult doFreshNodeTest() {
        gui.print("[LooCI Standard Test] Starting fresh node test.");
        
        MgtReply reply;
        int testNr = 0;
        int succeeded = 0;
        
        gui.print("\n" + ++testNr + " - get codebase ids");
        try {
            mgmt.getCodebaseIDs(node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{1, CODEBASE_ID_RECONF_ENG}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - remove non-existing codebase");
        try{
            mgmt.remove((byte)2, node_1);           // assume 2 is not a valid codebase id   
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_CODEBASE_NOT_FOUND, reply.getCode(), new byte[]{2}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - instantiate non-existing codebase");
        try{
            mgmt.instantiate((byte)2, node_1);      // assume 2 is not a valid codebase id
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_CODEBASE_NOT_FOUND, reply.getCode(), new byte[]{2}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get component ids");
        try {
            mgmt.getComponentIDs(node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{1,LoociConstants.COMPONENT_RECONFIG}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - destroy non-existing component");
        try {
            mgmt.destroy((byte)2, node_1);          // assume 2 is not a valid codebase id
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND, reply.getCode(), new byte[]{2}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - activate non-existing component");
        try {
            mgmt.activate((byte)2, node_1);         // assume 2 is not a valid codebase id
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND, reply.getCode(), new byte[]{2}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - deactivate non-existing component");
        try {
            mgmt.deactivate((byte)2, node_1);       // assume 2 is not a valid codebase id
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND, reply.getCode(), new byte[]{2}, reply.getPb().getPayload());
        
        gui.print("\n==================================");
        gui.print("[LooCI Standard Test] Fresh node test: success = " + succeeded + "/" + testNr);
        gui.print("==================================\n");
        
        return new TestResult("Fresh node test", testNr, succeeded);
    }
    
    public TestResult doCodebaseTest() {
        gui.print("[LooCI Standard Test] Starting codebase test.");
        
        MgtReply reply;
        int testNr = 0;
        int succeeded = 0;
        byte codebaseId = 0;
        
        gui.print("\n" + ++testNr + " - deploy codebase (Please wait, this may take a while...)");
        try {
            codebaseId = deployer.deploy(platform, tempSensor, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int temp = evaluateNotEqual(CODEBASE_ID_WILDCARD, codebaseId);
        temp += evaluateNotEqual(CODEBASE_ID_RECONF_ENG, codebaseId);
        if (temp == 2) {
            succeeded++;
        }
        
        gui.print("\n" + ++testNr + " - get codebase ids");
        try {
            mgmt.getCodebaseIDs(node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateIDlist(new byte[]{2, CODEBASE_ID_RECONF_ENG, codebaseId}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get codebase name by id");
        try{
            mgmt.getCodebaseName(codebaseId, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        byte[] b = new byte[1 + tempSensorName.length() + 1];
        b[0] = codebaseId;
        System.arraycopy(tempSensorName.getBytes(), 0, b, 1, tempSensorName.length());
        b[b.length - 1] = 0; // string is null-terminated
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get codebase name by non-existing id");
        try{
            mgmt.getCodebaseName((byte)3, node_1);  // assume codebase id 3 is non-existing
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_CODEBASE_NOT_FOUND, reply.getCode(), new byte[]{3}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get codebase ids by name");
        try{
            mgmt.getCodebaseIDsByName(tempSensorName, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        // we only expect one codebase id here
        succeeded += evaluateEqual(codebaseId, reply.getPb().getPayload()[reply.getPb().getPayload().length - 1]);
        
        gui.print("\n" + ++testNr + " - get codebase ids by non-existing name");
        String nonExistingCodebase = "xxx";
        try{
            mgmt.getCodebaseIDsByName(nonExistingCodebase, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        b = new byte[nonExistingCodebase.length() + 1];
        System.arraycopy(nonExistingCodebase.getBytes(), 0, b, 0, nonExistingCodebase.length());
        b[nonExistingCodebase.length()] = 0;        // null-terminated string
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_CODEBASE_NOT_FOUND, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - remove codebase");
        try{
            mgmt.remove((byte)codebaseId, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{codebaseId}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - deploy non-existing codebase (Please wait, this may take a while...)");
        byte nonExistingCodebaseID = CODEBASE_ID_WILDCARD;
        try {
            nonExistingCodebaseID = deployer.deploy(platform, "xxx", node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        succeeded += evaluateEqual(CODEBASE_ID_WILDCARD, nonExistingCodebaseID);
     
        
        //gui.print("TO DO: deploy file that isn't a codebase");
        //gui.print("TO DO: deploy to wrong platform (idem as previous?)");
        
        gui.print("\n===================================");
        gui.print("[LooCI Standard Test] Code base test: success = " + succeeded + "/" + testNr);
        gui.print("===================================\n");
        
        // Clean-up
        try {
            byte[] codebaseIDs = mgmt.getCodebaseIDs(node_1);
            for (int i = 1; i < codebaseIDs.length; i++) {
                if (codebaseIDs[i] != 1 && codebaseIDs[i] != 9) { // reserved for Reconfiguration Engine and Management Engine (OSGi)
                    mgmt.remove(codebaseIDs[i], node_1);
                }
            }
        } catch (Exception lme) {}
        
        return new TestResult("Code base test", testNr, succeeded);
    }
    
    public TestResult doComponentTest() {
        gui.print("[LooCI Standard Test] Starting component test.");
        
        MgtReply reply;
        int testNr = 0;
        int succeeded = 0;
        
        byte codebaseID = 0;
        byte componentID = 0;
        byte componentID_2 = 0;
        
        gui.print("\n" + ++testNr + " - deploy codebase (Please wait, this may take a while...)");
        try {
            codebaseID = deployer.deploy(platform, tempSensor, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int temp = evaluateNotEqual(CODEBASE_ID_WILDCARD, codebaseID);
        temp += evaluateNotEqual(CODEBASE_ID_RECONF_ENG, codebaseID);
        if (temp == 2) {
            succeeded++;
        }
        
        gui.print("\n" + ++testNr + " - instantiate codebase");
        try {
            componentID = mgmt.instantiate(codebaseID, node_1);
        } catch (Exception ex) {}
        reply = mgmt.getLastMgtReply();
        int temp2 = evaluateNotEqual(LoociConstants.COMPONENT_WILDCARD, componentID);
        temp2 += evaluateNotEqual(LoociConstants.COMPONENT_RECONFIG, componentID);
        if (temp2 == 2) {
            succeeded++;
        }
        
        gui.print("\n" + ++testNr + " - get component ids");
        try {
            mgmt.getComponentIDs(node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateIDlist(new byte[]{2, LoociConstants.COMPONENT_RECONFIG, componentID}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - destroy component");
        try {
            mgmt.destroy(componentID, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{componentID}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - instantiate codebase again");
        try {
            componentID = mgmt.instantiate(codebaseID, node_1);
        } catch (Exception ex) {}
        reply = mgmt.getLastMgtReply();
        int temp3 = evaluateNotEqual(LoociConstants.COMPONENT_WILDCARD, componentID);
        temp3 += evaluateNotEqual(LoociConstants.COMPONENT_RECONFIG, componentID);
        if (temp3 == 2) {
            succeeded++;
        }
        
        gui.print("\n" + ++testNr + " - activate component");
        try {
            mgmt.activate(componentID, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{componentID}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - activate component again");
        try {
            mgmt.activate(componentID, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_ILLEGAL_STATE, reply.getCode(), new byte[]{componentID}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get state of component");
        try {
            mgmt.getState(componentID, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{componentID, STATE_ACTIVE}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get state of non-existing component");
        try {
            mgmt.getState((byte)0, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND, reply.getCode(), new byte[]{0}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - deactivate component");
        try {
            mgmt.deactivate(componentID, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{componentID}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - deactivate component again");
        try {
            mgmt.deactivate(componentID, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_ILLEGAL_STATE, reply.getCode(), new byte[]{componentID}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get state of component");
        try {
            mgmt.getState(componentID, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{componentID, STATE_INACTIVE}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get codebase id by component id");
        try {
            mgmt.getCodebaseIdOfComponent(componentID, node_1);
        } catch (Exception ex) {}
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{componentID, codebaseID}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get codebase id by non-existing component id");
        try {
            mgmt.getCodebaseIdOfComponent((byte)0, node_1);
        } catch (Exception ex) {}
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND, reply.getCode(), new byte[]{0}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - instantiate codebase second time");
        try {
            componentID_2 = mgmt.instantiate(codebaseID, node_1);
        } catch (Exception ex) {}
        reply = mgmt.getLastMgtReply();
        int temp4 = evaluateNotEqual(LoociConstants.COMPONENT_WILDCARD, componentID_2);
        temp4 += evaluateNotEqual(LoociConstants.COMPONENT_RECONFIG, componentID_2);
        if (temp4 == 2) {
            succeeded++;
        }
        
        gui.print("\n" + ++testNr + " - get component ids by codebase id");
        try {
            mgmt.getComponentIDsbyCodebaseID(codebaseID, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{codebaseID, 2, componentID, componentID_2}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get component ids by non-existing codebase id");
        try {
            mgmt.getComponentIDsbyCodebaseID((byte)0, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_CODEBASE_NOT_FOUND, reply.getCode(), new byte[]{0}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get required interfaces");
        try {
            mgmt.getReceptacles(componentID, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{componentID, 0}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get required interfaces of non-existing component");
        try {
            mgmt.getReceptacles((byte)0, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND, reply.getCode(), new byte[]{0}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get provided interfaces");
        try {
            mgmt.getInterfaces(componentID, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{componentID, 1, 1, 1}, reply.getPb().getPayload());
        // component has 1 interface with type 257 (257 is a short, which equals to [1,1] in bytes
        
        gui.print("\n" + ++testNr + " - get provided interfaces of non-existing component");
        try {
            mgmt.getInterfaces((byte)0, node_1);
        } catch (Exception ex) {}    // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND, reply.getCode(), new byte[]{0}, reply.getPb().getPayload());
        

        gui.print("\n===================================");
        gui.print("[LooCI Standard Test] Component test: success = " + succeeded + "/" + testNr);
        gui.print("===================================\n");
        
        // Clean-up
        try {
            byte[] componentIDs = mgmt.getComponentIDs(node_1);
            for (int i = 0; i < componentIDs.length; i++) {
                if(componentIDs[i] != 1 && componentIDs[i] != 9) {
                    mgmt.destroy(componentIDs[i], node_1);
                }
            }
            byte[] codebaseIDs = mgmt.getCodebaseIDs(node_1);
            for (int i = 0; i < codebaseIDs.length; i++) {
                if (codebaseIDs[i] != 1 && codebaseIDs[i] != 9) {
                    mgmt.remove(codebaseIDs[i], node_1);
                }
            }
        } catch (LoociManagementException lme) {
        	gui.print("exception during cleanup " + lme.getLoociErrorMessage());
        	lme.printStackTrace();
        } catch (Exception e){
        	
        }
        
        return new TestResult("Component test", testNr, succeeded);
    }
    
    public TestResult doPropertiesTest() {
        gui.print("[LooCI Standard Test] Starting properties test.");
        
        MgtReply reply;
        int testNr = 0;
        int succeeded = 0;
        
        byte codebaseID = 0;
        byte componentID = 0;
        
        gui.print("\n" + ++testNr + " - deploy codebase of component without properties (Please wait, this may take a while...)");
        try {
            codebaseID = deployer.deploy(platform, tempSensor, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int temp = evaluateNotEqual(CODEBASE_ID_WILDCARD, codebaseID);
        temp += evaluateNotEqual(CODEBASE_ID_RECONF_ENG, codebaseID);
        if (temp == 2) {
            succeeded++;
        }
        
        gui.print("\n" + ++testNr + " - instantiate codebase");
        try {
            componentID = mgmt.instantiate(codebaseID, node_1);
        } catch (Exception ex) {}
        reply = mgmt.getLastMgtReply();
        int temp2 = evaluateNotEqual(LoociConstants.COMPONENT_WILDCARD, componentID);
        temp2 += evaluateNotEqual(LoociConstants.COMPONENT_RECONFIG, componentID);
        if (temp2 == 2) {
            succeeded++;
        }
        
        gui.print("\n" + ++testNr + " - set property on component without properties");
        try {
            mgmt.setProperty(new byte[]{20}, (short)1, componentID, node_1);
        } catch (Exception ex) {}
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_PARAMETER_NOT_FOUND, reply.getCode(), new byte[]{componentID, 0 , 1, 1, 20}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get properties on component without properties");
        try {
            mgmt.getProperties(componentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{componentID, 0}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get property value on component without properties");
        try {
            mgmt.getProperty((short)1, componentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_PARAMETER_NOT_FOUND, reply.getCode(), new byte[]{componentID, 0, 1}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get property info on component without properties");
        try {
            mgmt.getPropertyInfo((short)1, componentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_PARAMETER_NOT_FOUND, reply.getCode(), new byte[]{componentID, 0, 1}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - deploy codebase of component with properties (Please wait, this may take a while...)");
        try {
        	codebaseID = deployer.deploy(platform, tempFilter, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int temp3 = evaluateNotEqual(CODEBASE_ID_WILDCARD, codebaseID);
        temp3 += evaluateNotEqual(CODEBASE_ID_RECONF_ENG, codebaseID);
        if (temp3 == 2) {
            succeeded++;
        }
        
        gui.print("\n" + ++testNr + " - instantiate codebase");
        try {
            componentID = mgmt.instantiate(codebaseID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        int temp4 = evaluateNotEqual(LoociConstants.COMPONENT_WILDCARD, componentID);
        temp4 += evaluateNotEqual(LoociConstants.COMPONENT_RECONFIG, componentID);
        if (temp4 == 2) {
            succeeded++;
        }
        
        gui.print("\n" + ++testNr + " - set property");
        try {
            mgmt.setProperty(new byte[]{20}, (short)1, componentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{componentID, 0, 1, 1, 20}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get properties");
        try {
            mgmt.getProperties(componentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{componentID, 1, 0, 1}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get property value");
        try {
            mgmt.getProperty((short)1, componentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{componentID, 0, 1, 1, 20}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - get property info");
        try {
            mgmt.getPropertyInfo((short)1, componentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        String propertyName = "threshold";
        byte[] b = new byte[1 + 2 + 1 + propertyName.length() + 1];
        b[0] = componentID;
        b[1] = 0; // first byte of property id
        b[2] = 1; // second byte of property id
        b[3] = LoociConstants.DATATYPE_BYTE;
        System.arraycopy(propertyName.getBytes(), 0, b, 4, propertyName.length());
        b[b.length - 1] = 0; // null-terminated string
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
         
        gui.print("\n===================================");
        gui.print("[LooCI Standard Test] Properties test: success = " + succeeded + "/" + testNr);
        gui.print("===================================\n");
        
        // Clean-up
        try {
            byte[] componentIDs = mgmt.getComponentIDs(node_1);
            for (int i = 0; i < componentIDs.length; i++) {
                if(componentIDs[i] != 1 && componentIDs[i] != 9) {
                    mgmt.destroy(componentIDs[i], node_1);
                }
            }
            byte[] codebaseIDs = mgmt.getCodebaseIDs(node_1);
            for (int i = 0; i < codebaseIDs.length; i++) {
                if (codebaseIDs[i] != 1 && codebaseIDs[i] != 9) {
                    mgmt.remove(codebaseIDs[i], node_1);
                }
            }
        } catch (Exception lme) {}
        
        return new TestResult("Properties test", testNr, succeeded);
    }
    
    public TestResult doWiringTest() throws UnknownHostException{
        gui.print("[LooCI Standard Test] Starting wiring test.");
        
        MgtReply reply;
        int testNr = 0;
        int succeeded = 0;
        
        byte sensorCodebaseID = 0;
        byte filterCodebaseID = 0;
        byte sensorComponentID = 0;
        byte filterComponentID = 0;
        
        short tempEventType = 257;//EventTypes.TEMP_READING;
        
        gui.print("\n" + ++testNr + " - getLocalWires with no wires configured");
        try {
            mgmt.getLocalWires(EventTypes.ANY_EVENT, LoociConstants.COMPONENT_WILDCARD, LoociConstants.COMPONENT_WILDCARD, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        byte[] b = new byte[5];
        Utils.putShortAt(EventTypes.ANY_EVENT, b, 0);
        b[2] = LoociConstants.COMPONENT_WILDCARD;
        b[3] = LoociConstants.COMPONENT_WILDCARD;
        b[4] = 0;
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - getWiresTo with no wires configured");
        try {
            mgmt.getOutgoingRemoteWires(EventTypes.ANY_EVENT, LoociConstants.COMPONENT_WILDCARD, node_1, node_2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[20];
        Utils.putShortAt(EventTypes.ANY_EVENT, b, 0);
        b[2] = LoociConstants.COMPONENT_WILDCARD;
        Utils.putIpAt(node_2, b, 3);
        b[19] = 0;
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - getWiresFrom with no wires configured");
        try {
            mgmt.getIncomingRemoteWires(EventTypes.ANY_EVENT, LoociConstants.COMPONENT_WILDCARD, LoociConstants.ADDR_ANY, LoociConstants.COMPONENT_WILDCARD, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[21];
        Utils.putShortAt(EventTypes.ANY_EVENT, b, 0);
        b[2] = LoociConstants.COMPONENT_WILDCARD;
        Utils.putIpAt(LoociConstants.ADDR_ANY, b, 3);
        b[19] = LoociConstants.COMPONENT_WILDCARD;
        b[20] = 0;
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - wireLocal with no existing components");
        try {
            mgmt.wireLocal(EventTypes.ANY_EVENT, (byte)2, (byte)3, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[4];
        Utils.putShortAt(EventTypes.ANY_EVENT, b, 0);
        b[2] = 2;
        b[3] = 3;
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - wireTo with non-existing source component");
        try {
            mgmt.wireTo(EventTypes.ANY_EVENT, (byte)2, node_1, node_2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[19];
        Utils.putShortAt(EventTypes.ANY_EVENT, b, 0);
        b[2] = 2;
        Utils.putIpAt(node_2, b, 3);
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - wireFrom with non-existing destination component");
        try {
            mgmt.wireFrom(EventTypes.ANY_EVENT, (byte)2, node_2, (byte)2, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[20];
        Utils.putShortAt(EventTypes.ANY_EVENT, b, 0);
        Utils.putIpAt(node_2, b, 2);
        b[18] = 2;
        b[19] = 2;
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - deploy codebase of tempsensor (Please wait, this may take a while...)");
        try {
            sensorCodebaseID = deployer.deploy(platform, tempSensor, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int temp = evaluateNotEqual(CODEBASE_ID_WILDCARD, sensorCodebaseID);
        temp += evaluateNotEqual(CODEBASE_ID_RECONF_ENG, sensorCodebaseID);
        if (temp == 2) {
            succeeded++;
        }
        
        gui.print("\n" + ++testNr + " - instantiate tempsensor");
        try {
            sensorComponentID = mgmt.instantiate(sensorCodebaseID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        int temp2 = evaluateNotEqual(LoociConstants.COMPONENT_WILDCARD, sensorComponentID);
        temp2 += evaluateNotEqual(LoociConstants.COMPONENT_RECONFIG, sensorComponentID);
        if (temp2 == 2) {
            succeeded++;
        }
        
        gui.print("\n" + ++testNr + " - wireLocal with non-existing destination component");
        try {
            mgmt.wireLocal(EventTypes.ANY_EVENT, sensorComponentID, (byte)3, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[4];
        Utils.putShortAt(EventTypes.ANY_EVENT, b, 0);
        b[2] = sensorComponentID;
        b[3] = 3;
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - deploy codebase of tempfilter (Please wait, this may take a while...)");
        filterCodebaseID = 0;
        try {
            filterCodebaseID = deployer.deploy(platform, tempFilter, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int temp3 = evaluateNotEqual(CODEBASE_ID_WILDCARD, filterCodebaseID);
        temp3 += evaluateNotEqual(CODEBASE_ID_RECONF_ENG, filterCodebaseID);
        if (temp3 == 2) {
            succeeded++;
        }
        
        gui.print("\n" + ++testNr + " - instantiate tempfilter");
        try {
            filterComponentID = mgmt.instantiate(filterCodebaseID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        int temp4 = evaluateNotEqual(LoociConstants.COMPONENT_WILDCARD, filterComponentID);
        temp4 += evaluateNotEqual(LoociConstants.COMPONENT_RECONFIG, filterComponentID);
        if (temp4 == 2) {
            succeeded++;
        }
        
        gui.print("\n" + ++testNr + " - wireLocal tempsensor and tempfilter");
        try {
            mgmt.wireLocal(tempEventType, sensorComponentID, filterComponentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[4];
        Utils.putShortAt(tempEventType, b, 0);
        b[2] = sensorComponentID;
        b[3] = filterComponentID;
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - wireLocal non-existing event type at source");
        try {
            mgmt.wireLocal((short)1, sensorComponentID, filterComponentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[4];
        Utils.putShortAt((short)1, b, 0);
        b[2] = sensorComponentID;
        b[3] = filterComponentID;
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_PROVIDED_INTERFACE_NOT_FOUND, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - wireLocal non-existing event type at destination");
        try {
            mgmt.wireLocal(tempEventType, filterComponentID, sensorComponentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[4];
        Utils.putShortAt(tempEventType, b, 0);
        b[2] = filterComponentID;
        b[3] = sensorComponentID;
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_REQURIED_INTERFACE_NOT_FOUND, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - getLocalWires (all) with one wire configured");
        try {
            mgmt.getLocalWires(EventTypes.ANY_EVENT, LoociConstants.COMPONENT_WILDCARD, LoociConstants.COMPONENT_WILDCARD, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[9];
        Utils.putShortAt(EventTypes.ANY_EVENT, b, 0);
        b[2] = LoociConstants.COMPONENT_WILDCARD;
        b[3] = LoociConstants.COMPONENT_WILDCARD;
        b[4] = 1;
        Utils.putShortAt(tempEventType, b, 5);
        b[7] = sensorComponentID;
        b[8] = filterComponentID;
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - getLocalWires (specific) with one wire configured");
        try {
            mgmt.getLocalWires(tempEventType, sensorComponentID, filterComponentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[9];
        Utils.putShortAt(tempEventType, b, 0);
        b[2] = sensorComponentID;
        b[3] = filterComponentID;
        b[4] = 1;
        Utils.putShortAt(tempEventType, b, 5);
        b[7] = sensorComponentID;
        b[8] = filterComponentID;
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - unwireLocal tempsensor and tempfilter");
        try {
            mgmt.unwireLocal(tempEventType, sensorComponentID, filterComponentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[4];
        Utils.putShortAt(tempEventType, b, 0);
        b[2] = sensorComponentID;
        b[3] = filterComponentID;
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - wireTo tempSensor with non-existing provided interface");
        try {
            mgmt.wireTo((short)1, sensorComponentID, node_1, node_2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[19];
        Utils.putShortAt((short)1, b, 0);
        b[2] = sensorComponentID;
        Utils.putIpAt(node_2, b, 3);
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_PROVIDED_INTERFACE_NOT_FOUND, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - wireTo tempSensor");
        try {
            mgmt.wireTo(tempEventType, sensorComponentID, node_1, node_2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[19];
        Utils.putShortAt(tempEventType, b, 0);
        b[2] = sensorComponentID;
        Utils.putIpAt(node_2, b, 3);
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - getWiresTo (all)");
        try {
            mgmt.getOutgoingRemoteWires(EventTypes.ANY_EVENT, LoociConstants.COMPONENT_WILDCARD, node_1, LoociConstants.ADDR_ANY);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[39];
        Utils.putShortAt(EventTypes.ANY_EVENT, b, 0);
        b[2] = LoociConstants.COMPONENT_WILDCARD;
        Utils.putIpAt(LoociConstants.ADDR_ANY, b, 3);
        b[19] = 1;
        Utils.putShortAt(tempEventType, b, 20);
        b[22] = sensorComponentID;
        Utils.putIpAt(node_2, b, 23);
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - getWiresTo (specific)");
        try {
            mgmt.getOutgoingRemoteWires(tempEventType, sensorComponentID, node_1, node_2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[39];
        Utils.putShortAt(tempEventType, b, 0);
        b[2] = sensorComponentID;
        Utils.putIpAt(node_2, b, 3);
        b[19] = 1;
        Utils.putShortAt(tempEventType, b, 20);
        b[22] = sensorComponentID;
        Utils.putIpAt(node_2, b, 23);
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - unwireTo tempSensor");
        try {
            mgmt.unwireTo(tempEventType, sensorComponentID, node_1, node_2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[19];
        Utils.putShortAt(tempEventType, b, 0);
        b[2] = sensorComponentID;
        Utils.putIpAt(node_2, b, 3);
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - wireFrom tempFilter with non-existing required interface");
        try {
            mgmt.wireFrom((short)1, (byte)2, node_2, filterComponentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[20];
        Utils.putShortAt((short)1, b, 0);
        Utils.putIpAt(node_2, b, 2);
        b[18] = 2;
        b[19] = filterComponentID;
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_REQURIED_INTERFACE_NOT_FOUND, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - wireFrom tempFilter");
        try {
            mgmt.wireFrom(tempEventType, (byte)2, node_2, filterComponentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[20];
        Utils.putShortAt(tempEventType, b, 0);
        Utils.putIpAt(node_2, b, 2);
        b[18] = 2;
        b[19] = filterComponentID;
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - getWiresFrom (all)");
        try {
            mgmt.getIncomingRemoteWires(EventTypes.ANY_EVENT, LoociConstants.COMPONENT_WILDCARD, LoociConstants.ADDR_ANY, LoociConstants.COMPONENT_WILDCARD, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[41];
        Utils.putShortAt(EventTypes.ANY_EVENT, b, 0);
        b[2] = LoociConstants.COMPONENT_WILDCARD;
        Utils.putIpAt(LoociConstants.ADDR_ANY, b, 3);
        b[19] = LoociConstants.COMPONENT_WILDCARD;
        b[20] = 1;
        Utils.putShortAt(tempEventType, b, 21);
        b[23] = 2;
        Utils.putIpAt(node_2, b, 24);
        b[40] = filterComponentID;
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - getWiresFrom (specific)");
        try {
            mgmt.getIncomingRemoteWires(tempEventType, (byte)2, node_2, filterComponentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[41];
        Utils.putShortAt(tempEventType, b, 0);
        b[2] = 2;
        Utils.putIpAt(node_2, b, 3);
        b[19] = filterComponentID;
        b[20] = 1;
        Utils.putShortAt(tempEventType, b, 21);
        b[23] = 2;
        Utils.putIpAt(node_2, b, 24);
        b[40] = filterComponentID;
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - unwireFrom tempFilter");
        try {
            mgmt.unwireFrom(tempEventType, (byte)2, node_2, filterComponentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        b = new byte[20];
        Utils.putShortAt(tempEventType, b, 0);
        Utils.putIpAt(node_2, b, 2);
        b[18] = 2;
        b[19] = filterComponentID;
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), b, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - resetWirings");
        try {
            mgmt.resetWirings(filterComponentID, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{filterComponentID}, reply.getPb().getPayload());
        
        gui.print("\n" + ++testNr + " - resetWirings non-existing component");
        try {
            mgmt.resetWirings(LoociConstants.COMPONENT_WILDCARD, node_1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reply = mgmt.getLastMgtReply();
        succeeded += evaluateCodeAndPayload(ErrorCodes.ERROR_CODE_COMPONENT_NOT_FOUND, reply.getCode(), new byte[]{LoociConstants.COMPONENT_WILDCARD}, reply.getPb().getPayload());
        
        gui.print("\n===================================");
        gui.print("[LooCI Standard Test] Wiring test: success = " + succeeded + "/" + testNr);
        gui.print("===================================\n");
        
        // Clean-up
        try {
            byte[] componentIDs = mgmt.getComponentIDs(node_1);
            for (int i = 0; i < componentIDs.length; i++) {
                if(componentIDs[i] != 1 && componentIDs[i] != 9) {
                    mgmt.destroy(componentIDs[i], node_1);
                }
            }
            byte[] codebaseIDs = mgmt.getCodebaseIDs(node_1);
            for (int i = 0; i < codebaseIDs.length; i++) {
                if (codebaseIDs[i] != 1 && codebaseIDs[i] != 9) {
                    mgmt.remove(codebaseIDs[i], node_1);
                }
            }
        } catch (Exception lme) {}
        
        return new TestResult("Wiring test", testNr, succeeded);
    }
    
    public TestResult doMiscellaneousTest() {
        gui.print("[LooCI Standard Test] Starting miscellaneous test.");
        
        MgtReply reply;
        int testNr = 0;
        int succeeded = 0;
        
        gui.print("\n" + ++testNr + " - get platform");
        try {
            mgmt.getPlatformType(node_1);
        } catch (Exception ex) {} // we don't care about this since we evaluate the MgtReply.
        reply = mgmt.getLastMgtReply();
        
        byte platformByte = (byte)LoociRuntimes.getRuntimeVal(platform);
        succeeded += evaluateCodeAndPayload(ErrorCodes.SUCCESS, reply.getCode(), new byte[]{platformByte}, reply.getPb().getPayload());
        
        gui.print("\n===================================");
        gui.print("[LooCI Standard Test] Miscellaneous test: success = " + succeeded + "/" + testNr);
        gui.print("===================================\n");
        
        return new TestResult("Miscellaneous test", testNr, succeeded);
    }
    
    
    
    /**
     * 
     * @param expectedCode
     * @param resultCode
     * @param expectedPayload
     * @param resultPayload
     * @return 0 if false, 1 if true
     */
    private int evaluateCodeAndPayload(byte expectedCode, byte resultCode, byte[] expectedPayload, byte[] resultPayload) {
        int result;
        String s = "\t>> ";
        if (resultCode == expectedCode) {
            if (Arrays.equals(resultPayload, expectedPayload)) {
                result = 1;
                s = s.concat("OK: ");
            } else {
                result = 0;
                s = s.concat("Error in payload: ");
            }
        } else {
            result = 0;
            s = s.concat("Error in result code: ");
        }
        
        if (expectedPayload == null) {
            s = s.concat("expected [" + expectedCode + "], received [" + resultCode + "]");
        } else {
            s = s.concat("expected [" + expectedCode + "]" + stringify(expectedPayload) + ", received [" + resultCode + "]" + stringify(resultPayload));
        }
        gui.print(s);
        return result;
    }
    
    /**
     * 
     * @param expectedList  in the form [size][ids...]
     * @param receivedList    in the form [size][ids...]
     * @return 
     */
    private int evaluateIDlist(byte[] expectedList, byte[] receivedList) {
        int result;
        String s = "\t>>";
        // First check sizes of lists
        if (expectedList[0] != receivedList[0]) {
            result = 0;
            s = s.concat("Error in list sizes: expected size [" + expectedList[0] + "], received size [" + receivedList[0] + "]");
        } else {
            // copy and sort ids
            byte[] expected = new byte[expectedList.length - 1];
            System.arraycopy(expectedList, 1, expected, 0, expectedList.length - 1);
            Arrays.sort(expected);
            byte[] received = new byte[receivedList.length - 1];
            System.arraycopy(receivedList, 1, received, 0, receivedList.length - 1);
            Arrays.sort(received);
            // check contents
            if (Arrays.equals(expected, received)) {
                result = 1;
                s = s.concat("OK: expected list " + stringify(expectedList) + " matches received list " + stringify(receivedList));
            } else {
                result = 0;
                s = s.concat("Error in list contents: expected list " + stringify(expectedList) + " received list " + stringify(receivedList));
            }
        }
        gui.print(s);
        return result;
    }
    
    
    private int evaluateNotEqual(int erroneousValue, int resultValue){
    	 int result;
         String s = "\t>> ";
         if(erroneousValue == resultValue ){
        	 s = s.concat("Error in result: erroneous value [" + erroneousValue + "] must differ from received [" + resultValue + "]");
        	 result = 0;
         } else{
        	 s = s.concat("OK: erroneous value [" + erroneousValue + "] differs from received [" + resultValue + "]");
        	 result = 1;
         }
         gui.print(s);
         return result;
    }
    
    private int evaluateEqual(int expectedValue, int resultValue){
   	int result;
        String s = "\t>> ";
        if(expectedValue != resultValue ){
       	 s = s.concat("Error in result: expected [" +  expectedValue + "] differs from received [" + resultValue + "]");
       	 result = 0;
        } else{
       	 s = s.concat("OK: expected [" + expectedValue + "] equals received [" + resultValue + "]");
       	 result = 1;
        }
        gui.print(s);
        return result;
   }
    
    private String stringify(byte[] array) {
        String res = "[";
        res += (array.length >= 1) ? "" + array[0] : "";
        for (int i = 1; i < array.length; i++) {
            res = res + "," + array[i];
        }
        res = res + "]";
        return res;
    }
}
