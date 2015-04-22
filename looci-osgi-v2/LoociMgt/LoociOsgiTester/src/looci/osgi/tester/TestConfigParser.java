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

import eu.maerien.xmlutils.XmlUnitParser; 
import eu.maerien.xmlutils.XmlUnit;  

public class TestConfigParser extends XmlUnitParser<TestConfig> { 
	private static final String platform = "platform";
	private static final String node_1 = "node_1";
	private static final String node_2 = "node_2";
	private static final String tempSensor = "tempSensor";
	private static final String tempFilter = "tempFilter";
	private static final String sensorName = "sensorName";
	private static final String filterName = "filterName";
	private static final String doFreshNodeTest = "doFreshNodeTest";
	private static final String doCodebaseTest = "doCodebaseTest";
	private static final String doComponentTest = "doComponentTest";
	private static final String doWireTest = "doWireTest";
	private static final String doPropertiesTest = "doPropertiesTest";
	private static final String doMiscTest = "doMiscTest";

	@Override
	public TestConfig fromUnit(XmlUnit unit){
		if( unit == null){ return null;}
		TestConfig val = new TestConfig();
		val.setPlatform(unit.getLeafContent(platform));
		val.setNode_1(unit.getLeafContent(node_1));
		val.setNode_2(unit.getLeafContent(node_2));
		val.setTempSensor(unit.getLeafContent(tempSensor));
		val.setTempFilter(unit.getLeafContent(tempFilter));
		val.setSensorName(unit.getLeafContent(sensorName));
		val.setFilterName(unit.getLeafContent(filterName));
		val.setDoFreshNodeTest(Boolean.parseBoolean(unit.getLeafContent(doFreshNodeTest)));
		val.setDoCodebaseTest(Boolean.parseBoolean(unit.getLeafContent(doCodebaseTest)));
		val.setDoComponentTest(Boolean.parseBoolean(unit.getLeafContent(doComponentTest)));
		val.setDoWireTest(Boolean.parseBoolean(unit.getLeafContent(doWireTest)));
		val.setDoPropertiesTest(Boolean.parseBoolean(unit.getLeafContent(doPropertiesTest)));
		val.setDoMiscTest(Boolean.parseBoolean(unit.getLeafContent(doMiscTest)));
		return val;
	}


	@Override
	public XmlUnit toUnit(TestConfig input){
		if( input == null){ return null;}
		XmlUnit unit = new XmlUnit();
		unit.setName("TestConfig");
		unit.addLeafChild(platform, input.getPlatform());
		unit.addLeafChild(node_1, input.getNode_1());
		unit.addLeafChild(node_2, input.getNode_2());
		unit.addLeafChild(tempSensor, input.getTempSensor());
		unit.addLeafChild(tempFilter, input.getTempFilter());
		unit.addLeafChild(sensorName, input.getSensorName());
		unit.addLeafChild(filterName, input.getFilterName());
		unit.addLeafChild(doFreshNodeTest, Boolean.toString(input.getDoFreshNodeTest()));
		unit.addLeafChild(doCodebaseTest, Boolean.toString(input.getDoCodebaseTest()));
		unit.addLeafChild(doComponentTest, Boolean.toString(input.getDoComponentTest()));
		unit.addLeafChild(doWireTest, Boolean.toString(input.getDoWireTest()));
		unit.addLeafChild(doPropertiesTest, Boolean.toString(input.getDoPropertiesTest()));
		unit.addLeafChild(doMiscTest, Boolean.toString(input.getDoMiscTest()));
		return unit;
	}


}
