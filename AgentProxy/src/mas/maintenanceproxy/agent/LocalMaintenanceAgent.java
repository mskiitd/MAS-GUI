package mas.maintenanceproxy.agent;

import jade.core.AID;
import jade.domain.DFService;
import mas.maintenanceproxy.goal.SendCorrectiveRepairDataGoal;
import mas.maintenanceproxy.gui.MaintenanceGUI;
import mas.util.AgentUtil;
import mas.util.ID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.core.Capability;

public class LocalMaintenanceAgent extends AbstractLocalMaintenanceAgent {

	private static final long serialVersionUID = 1L;
	private Logger log;
	private AID blackboard;
	private Capability bCap;
	private BeliefBase bfBase;
	public static long prevMaintPeriod = 50000;

	public MaintenanceGUI mgui = null;

	public void sendCorrectiveMaintenanceRepairTime(long mtime) {

		String repairData = String.valueOf(mtime);
		bfBase.updateBelief(ID.Maintenance.BeliefBaseConst.correctiveRepairData, repairData);
		log.info("Sending repair data : " + repairData);
		addGoal(new SendCorrectiveRepairDataGoal());
	}

	@Override
	protected void takeDown() {
		super.takeDown();
		try {
			DFService.deregister(this);
		}
		catch (Exception e) {
		}
		if(mgui != null) {
			mgui.dispose();
		}
	}

	@Override
	protected void init() {
		super.init();

		log = LogManager.getLogger();

		// Add capability to agent 
		bCap = new MaitenanceBasicCapability();
		addCapability(bCap);
		bfBase = bCap.getBeliefBase();

		if(mgui == null) {
			mgui = new MaintenanceGUI(LocalMaintenanceAgent.this);
		}
		bfBase.updateBelief(ID.Maintenance.BeliefBaseConst.gui_maintenance, mgui);
		
		blackboard = AgentUtil.findBlackboardAgent(this);
		bCap.getBeliefBase().updateBelief(
				ID.Maintenance.BeliefBaseConst.blackboardAgentAID, blackboard);

	}
}
