package mas.maintenanceproxy.agent;

import jade.core.AID;
import mas.maintenanceproxy.gui.MaintenanceGUI;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.Capability;

public class LocalMaintenanceAgent extends AbstractLocalMaintenanceAgent {

	private static final long serialVersionUID = 1L;
	private Logger log;
	private AID blackboard;
	
	private MaintenanceGUI mgui = null;

	public void sendCorrectiveMaintenanceRepairTime(long mtime) {

		ZoneDataUpdate correctiveRepairUpdate = new ZoneDataUpdate.Builder(
				ID.Maintenance.ZoneData.correctiveMaintdata).
				value(mtime).
				Build();

		AgentUtil.sendZoneDataUpdate(blackboard ,correctiveRepairUpdate,
				LocalMaintenanceAgent.this);
	}

	@Override
	protected void init() {
		super.init();
		
		if(mgui == null) {
			mgui = new MaintenanceGUI(this);
		}
		log = LogManager.getLogger();

		// Add capability to agent 
		Capability bCap = new MaitenanceBasicCapability();
		addCapability(bCap);

		blackboard = AgentUtil.findBlackboardAgent(this);
		bCap.getBeliefBase().updateBelief(
				ID.Maintenance.BeliefBaseConst.blackboardAgent, blackboard);

	}
}
