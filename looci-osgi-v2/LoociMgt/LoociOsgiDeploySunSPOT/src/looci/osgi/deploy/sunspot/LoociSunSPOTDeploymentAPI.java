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

import looci.osgi.serv.constants.LoociRuntimes;
import looci.osgi.servExt.appInfo.LoociNodeInfo;
import looci.osgi.servExt.mgt.IDeploymentAPI;
import looci.osgi.servExt.mgt.ILoociAPI;
import looci.osgi.servExt.mgt.TextObserver;
import looci.sunspot.util.Constants;

import java.util.Map;

public class LoociSunSPOTDeploymentAPI implements IDeploymentAPI {

    public static final int TIME_OUT = 10;
    private ILoociAPI loociApi;
    
    public LoociSunSPOTDeploymentAPI(ILoociAPI loociApi) {
        this.loociApi = loociApi;
    }
    
    @Override
    public byte deploy(String componentFile, String address) {
        //System.out.println("[SUNSPOT DEPLOYER - API] deploy(" + componentFile + ", " + address + ")");
        
        LoociSunSPOTDeployer deployer = new LoociSunSPOTDeployer(address, componentFile, loociApi);
        deployer.start();
        try {
            int timeOut = 0;
            while (deployer.isRunning() && timeOut < TIME_OUT) {
                //System.out.println("[SUNSPOT DEPLOYER - API] time out count: " + timeOut + "/" + TIME_OUT);
                synchronized (this) {
                    timeOut++;
                    this.wait(4000);
                }
            }
            if (timeOut == TIME_OUT) {
                deployer.timeOut();
            }
            return deployer.getResultingCodebaseId();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (byte) Constants.CODEBASE_ID_WILDCARD;
    }

    @Override
    public byte deploy(String string, LoociNodeInfo lni, TextObserver to , Map<String, String> parameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String getTargettedPlatform() {
        return LoociRuntimes.RUNTIME_SUNSPOT;
    }
}
