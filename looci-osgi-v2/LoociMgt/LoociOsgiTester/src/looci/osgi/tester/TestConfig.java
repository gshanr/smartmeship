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


public class TestConfig {

	
    private String platform = "";
    private String node_1 = "";
    private String node_2 ="";
    private String tempSensor = "";
    private String tempFilter = "";
    private String sensorName = "";
    private String filterName = "";
    

    private boolean doFreshNodeTest = true;
    private boolean doCodebaseTest = true;
    private boolean doComponentTest = true;
    private boolean doWireTest = true;
    private boolean doPropertiesTest = true;
    private boolean doMiscTest = true;
    
    
    public TestConfig(){
    	
    }
    
    public TestConfig(
    		String platform,
    		String node1, 
    		String node2, 
    		String sensor, 
    		String sensorName,
    		String filter,
    		String filterName){
    	this.platform = platform;
    	this.node_1 = node1;
    	this.node_2 = node2;
    	this.tempSensor = sensor;
    	this.tempFilter = filter; 
    	this.sensorName = sensorName;
    	this.filterName = filterName;
    }
    
    public String getNode_1() {
		return node_1;
	}
    
    public String getNode_2() {
		return node_2;
	}
    
    public String getPlatform() {
		return platform;
	}
    
    public String getTempFilter() {
		return tempFilter;
	}
    
    public String getTempSensor() {
		return tempSensor;
	}
    
    public String getFilterName() {
		return filterName;
	}
    
    public String getSensorName() {
		return sensorName;
	}
    
    public boolean getDoCodebaseTest() {
		return doCodebaseTest;
	}
    
    public boolean getDoComponentTest() {
		return doComponentTest;
	}
    
    public boolean getDoFreshNodeTest() {
		return doFreshNodeTest;
	}
    
    public boolean getDoMiscTest() {
		return doMiscTest;
	}
    
    public boolean getDoPropertiesTest() {
		return doPropertiesTest;
	}
    
    public boolean getDoWireTest() {
		return doWireTest;
	}
    
    
    public void setNode_1(String node_1) {
		this.node_1 = node_1;
	}
    
    public void setNode_2(String node_2) {
		this.node_2 = node_2;
	}
    
    public void setPlatform(String platform) {
		this.platform = platform;
	}
    
    public void setTempFilter(String tempFilter) {
		this.tempFilter = tempFilter;
	}
    
    public void setTempSensor(String tempSensor) {
		this.tempSensor = tempSensor;
	}
    
    public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
    
    public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}
    
    
    public void setDoCodebaseTest(boolean doCodebaseTest) {
		this.doCodebaseTest = doCodebaseTest;
	}
    
    public void setDoComponentTest(boolean doComponentTest) {
		this.doComponentTest = doComponentTest;
	}
    
    public void setDoFreshNodeTest(boolean doFreshNodeTest) {
		this.doFreshNodeTest = doFreshNodeTest;
	}
    
    public void setDoPropertiesTest(boolean doPropertiesTest) {
		this.doPropertiesTest = doPropertiesTest;
	}
    
    public void setDoMiscTest(boolean doMiscTest) {
		this.doMiscTest = doMiscTest;
	}
    
    public void setDoWireTest(boolean doWireTest) {
		this.doWireTest = doWireTest;
	}
}
