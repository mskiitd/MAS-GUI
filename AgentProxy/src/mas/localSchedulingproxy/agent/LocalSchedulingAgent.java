package mas.localSchedulingproxy.agent;

import java.util.ArrayList;

import jade.core.AID;
import mas.jobproxy.job;
import mas.localSchedulingproxy.capability.LocalSchedulingBasicCapability;
import mas.machineproxy.gui.MachineGUI;
import mas.util.AgentUtil;
import mas.util.ID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.Capability;

public class LocalSchedulingAgent extends AbstractlocalSchedulingAgent{

	private static final long serialVersionUID = 1L;
	private Logger log;
	public static MachineGUI mGUI;
	public static long schedulingPeriod = 5000;

	public void UpdateJobQueue(ArrayList<job> newQueue) {
		if(mGUI != null) {
			mGUI.updateQueue(newQueue);
			log.info("Updating queue in machine's GUI ");
		}
	}
	
	@Override
	protected void init() {
		super.init();

		log = LogManager.getLogger();

		// Add capability to agent 
		Capability bCap = new LocalSchedulingBasicCapability();
		addCapability(bCap);

		AID bba = AgentUtil.findBlackboardAgent(this);
		bCap.getBeliefBase().updateBelief(
				ID.LocalScheduler.BeliefBaseConst.blackboardAgent, bba);

		if(mGUI == null) {
			mGUI = new MachineGUI(LocalSchedulingAgent.this);
		}
	}

}
