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

import java.io.File;

import com.sun.spot.client.IUI;
import com.sun.spot.client.SpotClientCommands;
import com.sun.spot.peripheral.ota.ISpotAdminConstants;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import looci.osgi.servExt.mgt.ILoociAPI;
import looci.sunspot.util.Constants;
import looci.sunspot.util.Utils;

public class LoociSunSPOTDeployer extends Thread {

    private String nodeAddress;
    private String filePath;
    private volatile boolean isExecuting;
    private byte resultingCodebaseID = Constants.CODEBASE_ID_WILDCARD;//-1;
    
    private final boolean DEBUG = true;
    
    private ILoociAPI api;

    // deployment related variables
    String sunspotHome = null;
    private String appPath = null;
    private String libPath = null;
    private File armDirFile = null;
    private String keyStorePath = null;
    private String baseStationPort = null;

    private static final String SUITE_DIR = "suite";

    public LoociSunSPOTDeployer(String address, String componentFile, ILoociAPI loociApi) {
        //System.out.println("[SUNSPOT DEPLOYER] LoociSunSPOTDeployer(...)");
        this.nodeAddress = address;
        this.filePath = componentFile;      
        this.api = loociApi;
        
        // set deployment related variables
        String userHome = System.getProperty("user.home");
        //System.out.println("[SUNSPOT DEPLOYER] userHome = " + userHome);
        try {
            Properties p = new Properties();         
            p.load(new FileInputStream(new File(userHome, ".sunspot.properties")));
            sunspotHome = p.getProperty("sunspot.home").concat("/");
            //System.out.println("[SUNSPOT DEPLOYER] sunspotHome = " + sunspotHome);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.setProperty("spot.basestation.sharing", "true");
        //System.setProperty("SERIAL_PORT", "dummyport");
        System.setProperty("squawk.startup.arguments", "-Xboot:268763136 -Xmxnvm:0 -isolateinit:com.sun.spot.peripheral.Spot -dma:1024");

        // CHECK IF THESE VALUES MAKE SENSE
//        String libName = System.getProperty("spot.library.name");
//        System.out.println("[SUNSPOT DEPLOYER] libName = " + libName);
        
        appPath = SUITE_DIR;
        //appPath = sunspotHome + "dummy";
        //System.out.println("[SUNSPOT DEPLOYER] appPath = " + appPath);
        
        libPath = sunspotHome + "arm";// + File.separator + libName;
        //System.out.println("[SUNSPOT DEPLOYER] libPath = " + libPath);
        
        armDirFile = new File(sunspotHome + File.separator + "arm");
        //System.out.println("[SUNSPOT DEPLOYER] armDirFile = " + armDirFile.toString());
        
        keyStorePath = userHome + File.separator + "sunspotkeystore";
        //System.out.println("[SUNSPOT DEPLOYER] keyStorePath = " + keyStorePath);
        
        baseStationPort = System.getProperty("SERIAL_PORT");
        //System.out.println("[SUNSPOT DEPLOYER] baseStationPort = " + baseStationPort);
        
        isExecuting = true;
    }

    public boolean isRunning() {
        return isExecuting;
    }

    public byte getResultingCodebaseId() {
        return resultingCodebaseID;
    }

    public void run() {
        //System.out.println("[SUNSPOT DEPLOYER] run()");
        execute();
    }

    public void execute() {
        //System.out.println("[SUNSPOT DEPLOYER] execute()");
        
        int stage = 0;
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.out.println("[SUNSPOT DEPLOYER] File not found: " + filePath);
            isExecuting = false;
            return;
        }
        
        String fileName = file.getName();
        //System.out.println("[SUNSPOT DEPLOYER] fileName = " + fileName + ", address = " + nodeAddress);
        
        try {
            // Attempt to convert the provided Jar filePath into a SunSPOT suite
            if (!convertJarToSuite(file)) {
                throw new Exception();
            }
            stage++;
            System.out.println("[SUNSPOT DEPLOYER] Deployment step [" + stage + "/3] success: jar converted to suite.");

            // Deploy the suite to the specified node.
            File suiteDir = new File(SUITE_DIR);  
            //SpotClientCommands scc = new SpotClientCommands((IUI) new SimplePrintUI(), appPath, libPath, armDirFile, keyStorePath, /*baseStationPort*/null, IPv6toIEEE(nodeAddress), ISpotAdminConstants.MASTER_ISOLATE_ECHO_PORT);
            SpotClientCommands scc = new SpotClientCommands((IUI) new SimplePrintUI(), appPath, libPath, armDirFile, keyStorePath, null, Utils.IPv6toIEEE(nodeAddress), ISpotAdminConstants.MASTER_ISOLATE_ECHO_PORT);
            scc.execute("synchronize");
            //System.out.println("[SUNSPOT DEPLOYER] start flashapp");
            String suiteLocation = suiteDir.getAbsolutePath() + File.separator + fileName;
            String uri = "spotsuite://" + fileName;
            //scc.execute("flashapp", "false", "0", uri, "true", suiteLocation);
            scc.execute("flashapp", "false", "0", uri, "false", suiteLocation);
            scc.execute("quit");
            stage++;
            System.out.println("[SUNSPOT DEPLOYER] Deployment step [" + stage + "/3] success: suite deployed.");

            // Send a deploy event to notify the spot of a new suite and return its codebase id
            resultingCodebaseID = api.deploy(fileName, nodeAddress);            
            if(!(resultingCodebaseID > 0)) {
                System.err.println("[SUNSPOT DEPLOYER] Returned codebase ID is zero or negative.");
                throw new Exception();
            } else {
                stage++;
                System.out.println("[SUNSPOT DEPLOYER] Deployment step [" + stage + "/3] success: codebase id " + resultingCodebaseID + " received for " + fileName);
            }
        } catch (Exception e) {
            stage++;
            System.err.println("[SUNSPOT DEPLOYER] Deployment Failed at step " + stage + ".");
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        isExecuting = false;
    }

    public void timeOut() {
        System.out.println("[SUNSPOT DEPLOYER] Timed out!!!");
    }
    
    /**
     * This private method converts the given jar into a suite with a unique
     * name based on the provided fileName.
     *
     */
    private boolean convertJarToSuite(File jar) {
        List<String> command = new ArrayList<String>();
        String suiteloc = SUITE_DIR + File.separator + jar.getName();

        // Set the romizer classpath
        String romizerClasspath = sunspotHome + "bin" + File.separator + "romizer_classes.jar" 
                + System.getProperty("path.separator") + sunspotHome + "bin" + File.separator + "squawk.jar" 
                + System.getProperty("path.separator") + sunspotHome + "bin" + File.separator + "squawk_device_classes.jar" 
                + System.getProperty("path.separator") + sunspotHome + "bin" + File.separator + "translator_classes.jar" 
                + System.getProperty("path.separator") + sunspotHome + "lib" + File.separator + "multihop_common.jar" 
                + System.getProperty("path.separator") + sunspotHome + "lib" + File.separator + "spotlib_host.jar" 
                + System.getProperty("path.separator") + sunspotHome + "lib" + File.separator + "spotlib_common.jar" 
                + System.getProperty("path.separator") + sunspotHome + "lib" + File.separator + "squawk_common.jar" 
                + System.getProperty("path.separator") + sunspotHome + "lib" + File.separator + "RXTXcomm.jar";
        System.setProperty("romizer.classpath", romizerClasspath);

        // Add the commands to run Romizer
        command.add("java");
        command.add("-Xmx256M");
        command.add("-XX:CompileCommand=exclude,com/sun/squawk/Method.getParameterTypes");
        command.add("-XX:CompileCommand=exclude,com/sun/squawk/SymbolParser.getSignatureTypeAt");
        command.add("-XX:CompileCommand=exclude,com/sun/squawk/SymbolParser.stripMethods");
        command.add("-classpath");
        command.add(romizerClasspath);
        command.add("com.sun.squawk.Romizer");
        command.add("-nobuildproperties");
        command.add("-suitepath:" + sunspotHome.replace("\\", "/") + "arm");
        command.add("-boot:squawk");
        command.add("-parent:transducerlib");
        //command.add("-parent:"+System.getProperty("spot.library.name"));
        command.add("-metadata");
        command.add("-lnt");
        command.add("-strip:d");
        command.add("-cp:" + jar.getAbsolutePath());
        command.add("-endian:little");
        command.add("-o:" + suiteloc);
        command.add(jar.getAbsolutePath());

        // Run the commands
        try {
            (new File(SUITE_DIR)).mkdirs();
            System.out.println("[SUNSPOT DEPLOYER] Invoking romizer: ");
            for (String arg : command) {
                System.out.println("[SUNSPOT DEPLOYER] " + arg);
            }
            ProcessBuilder processBuilder = new ProcessBuilder("java");
            processBuilder.redirectErrorStream(true);
            Process romizer;
            romizer = null;
            try {
                romizer = processBuilder.command(command).start();
            } catch (IOException ex) {
                System.err.println("[SUNSPOT DEPLOYER] Error! Romizer Failed");
                if (DEBUG) {
                    ex.printStackTrace();
                }
                return false;
            }
            BufferedReader romizerout = new BufferedReader(new InputStreamReader(romizer.getInputStream()));
            String line;
            while ((line = romizerout.readLine()) != null) {
                System.out.println(line);
            }
            return (romizer.waitFor() == 0);
        } catch (InterruptedException e) {
            System.err.println("[SUNSPOT DEPLOYER] Error! Romizer Failed");
            if (DEBUG == true) {
                e.printStackTrace();
            }
            return false;
        } catch (IOException e) {
            System.err.println("[SUNSPOT DEPLOYER] Error! Romizer Failed");
            if (DEBUG == true) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
