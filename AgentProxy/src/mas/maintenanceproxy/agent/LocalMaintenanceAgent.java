package mas.maintenanceproxy.agent;

import java.util.ArrayList;

import jade.core.AID;
import mas.machineproxy.SimulatorInternals;
import mas.machineproxy.component.Component;
import mas.machineproxy.component.IComponent;
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
	private Capability bCap;
	public static long prevMaintPeriod = 1 * 60 * 1000;
	
	public static MaintenanceGUI mgui = null;

	public void sendCorrectiveMaintenanceRepairTime(long mtime) {
		
		SimulatorInternals machine =  (SimulatorInternals) bCap.getBeliefBase().
				getBelief(ID.Maintenance.BeliefBaseConst.machine).getValue();
		
		ArrayList<IComponent> machineComponents = machine.getComponents();
		String repairData = String.valueOf(mtime);
		
		for(int i = 0 ; i < machineComponents.size(); i++) {
			repairData += " " + i;
		}

		ZoneDataUpdate correctiveRepairUpdate = new ZoneDataUpdate.Builder(
				ID.Maintenance.ZoneData.correctiveMaintdata).
				value(repairData).
				Build();

		AgentUtil.sendZoneDataUpdate(blackboard ,correctiveRepairUpdate,
				LocalMaintenanceAgent.this);
	}

	@Override
	protected void init() {
		super.init();
		
		if(mgui == null) {
			mgui = new MaintenanceGUI(LocalMaintenanceAgent.this);
		}
		log = LogManager.getLogger();

		// Add capability to agent 
		bCap = new MaitenanceBasicCapability();
		addCapability(bCap);

		blackboard = AgentUtil.findBlackboardAgent(this);
		bCap.getBeliefBase().updateBelief(
				ID.Maintenance.BeliefBaseConst.blackboardAgentAID, blackboard);

	}
}
