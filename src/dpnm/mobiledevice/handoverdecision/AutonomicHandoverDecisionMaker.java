package dpnm.mobiledevice.handoverdecision;

import java.io.File;
import java.util.Random;

import net.sourceforge.jFuzzyLogic.FIS;

import dpnm.featuremodel.StaticFeatureModel;
import dpnm.mobiledevice.NetworkInterface;
import dpnm.mobiledevice.MobileApplication;
import dpnm.mobiledevice.NetworkProperty;
import dpnm.server.ContextServer;
import dpnm.tool.Resources;
import dpnm.mobiledevice.policy.ruleobjects.*;
import dpnm.network.device.NetworkDevice;

public class AutonomicHandoverDecisionMaker implements HandoverDecisionMaker {


	private NetworkInterface[] networkInterfaces = null;
	public AutonomicHandoverDecisionMaker() {
	}
	
	public void setApplication(MobileApplication app) {
		for (int i = 0; networkInterfaces != null && i < networkInterfaces.length; i++) {	
			networkInterfaces[i].setApplication(app);
		}
	}
	
	public void setNetworkInterfaces(NetworkInterface[] nis) {
		this.networkInterfaces = nis;
	}

	/**
	 * 1. Apply Speed Filter
	 * 2. Apply SLA Filter
	 * 3. Calculate APAVs
	 * 4. Calculate APSVs
	 */
	public synchronized NetworkInterface getBestAccessNetwork(Action action) {
		if (networkInterfaces == null || networkInterfaces.length == 0) return null;

		int maxNi = 0;
		NetworkDevice maxDevice = null;
		NetworkProperty maxProp = null;
		for (int i = 0; networkInterfaces != null && i < networkInterfaces.length; i++) {
			if (networkInterfaces[i].isSpeedSupport() && networkInterfaces[i].isSLASupport()) {
				networkInterfaces[i].networkSelection(action);
				NetworkDevice cDevice = (NetworkDevice)networkInterfaces[i].getCandidateDevice();
				maxDevice = (NetworkDevice)networkInterfaces[maxNi].getCandidateDevice();
				
				NetworkProperty cProp = networkInterfaces[i].getNetworkProperty(cDevice);
				maxProp = networkInterfaces[maxNi].getNetworkProperty(maxDevice);
	
	
				if (maxProp != null && cProp != null && maxProp.compare(cProp, action.getDecisionAlgorithm())) {
					maxDevice = cDevice;
					maxProp = cProp;
					maxNi = i;
				} else if (maxProp == null) {
					maxProp = cProp;
					maxNi = i;
				}
			}
		}
		if (action.getDecisionAlgorithm() != Action.RANDOM) {
			if (maxProp != null) {
				return networkInterfaces[maxNi];
			}
		} else {
			//	RANDOM selection
			int c = new Random(System.currentTimeMillis()).nextInt(networkInterfaces.length);
			int i = 0;
			boolean isCandidate = false;
			for (int j = 0; j < networkInterfaces.length; j++) {
				if (networkInterfaces[i].isSpeedSupport() && networkInterfaces[i].isSLASupport()) {
					if (networkInterfaces[j].getCandidateDevice() != null) {
						isCandidate = true;
						break;
					}
				}
			}
			while(isCandidate) {
				if (networkInterfaces[i].isSpeedSupport() && networkInterfaces[i].isSLASupport()) {
					if (networkInterfaces[i].getCandidateDevice() != null) {
						c--;
						if (c < 0) {
							return networkInterfaces[i];
						}
					}
				}
				i = (++i) % networkInterfaces.length;
			}
		}
		return null;
	}
}
