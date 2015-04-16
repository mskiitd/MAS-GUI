package mas.localSchedulingproxy.agent;

import jade.core.AID;
import jade.domain.DFService;

import java.util.ArrayList;

import mas.jobproxy.Batch;
import mas.localSchedulingproxy.capability.LocalSchedulingBasicCapability;
import mas.machineproxy.gui.MachineGUI;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.core.Capability;

public class LocalSchedulingAgent extends AbstractlocalSchedulingAgent{

	private static final long serialVersionUID = 1L;
	private Logger log;
	public MachineGUI mGUI;
	public static long schedulingPeriod = 20000;
	private BeliefBase bfBase;

	public void UpdateJobQueue(ArrayList<Batch> newQueue) {
		if(mGUI != null) {
			mGUI.updateQueue(newQueue);
			log.info("Updating queue in machine's GUI ");
		}
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
		try {
			DFService.deregister(this);
		}
		catch (Exception e) {
		}
		if(mGUI != null) {
			mGUI.clean();
			mGUI.dispose();
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
		bfBase = bCap.getBeliefBase();
		bfBase.updateBelief(
				ID.LocalScheduler.BeliefBaseConst.blackboardAgent, bba);

		if(mGUI == null) {
			mGUI = new MachineGUI(LocalSchedulingAgent.this);
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.gui_machine, mGUI);

			ZoneDataUpdate guiUpdate = new ZoneDataUpdate.
					Builder(ID.LocalScheduler.ZoneData.gui_machine).
					value(mGUI).Build();

			//			AgentUtil.sendZoneDataUpdate(bba, guiUpdate, LocalSchedulingAgent.this);
		}
	}
}
