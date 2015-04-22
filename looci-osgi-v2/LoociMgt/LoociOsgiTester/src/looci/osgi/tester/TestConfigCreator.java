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

import looci.osgi.serv.constants.LoociRuntimes;

public class TestConfigCreator {

	
	public static void main(String[] args) {
		
		
		TestConfigParser parser = new TestConfigParser();
		
		
		TestConfig configSunspot = new TestConfig(
				LoociRuntimes.RUNTIME_SUNSPOT,
				"fddb:e5ed:6fd8:beef:214:4f01:0:64db",
				"fddb:e5ed:6fd8:beef:214:4f01:0:1111",
				"/Users/klaas/SunSPOT/myProjects/looci_v1.1-sunspot-yellow/examples/TempSensor/suite/TempSensor_1.0.0.jar",
				"TempSensor_1.0.0.jar",
				"/Users/klaas/SunSPOT/myProjects/looci_v1.1-sunspot-yellow/examples/TempFilter/suite/TempFilter_1.0.0.jar",
				"TempFilter_1.0.0.jar"
				);
		
		parser.serializeToFile("conf/testSunspot.xml", configSunspot);
		
		TestConfig configOsgi = new TestConfig(
				LoociRuntimes.RUNTIME_OSGI,
				"aaab::1",
				"aaab::2",
				"testSensor.jar",
				"testSensor",
				"testFilter.jar",
				"testFilter"
				);
		
		
		parser.serializeToFile("conf/testOsgi.xml", configOsgi);
		
		TestConfig configRaven = new TestConfig(
				LoociRuntimes.RUNTIME_RAVEN,
				"node54",
				"node55",
				"testSensor.comp",
				"testSensor",
				"testFilter.comp",
				"testFilter"
				);
		

		parser.serializeToFile("conf/testRaven.xml", configRaven);
	}
}
