package mas.maintenanceproxy.behavior;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

import java.util.Date;

import mas.machineproxy.MachineStatus;
import mas.machineproxy.SimulatorInternals;
import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import mas.maintenanceproxy.classes.PMaintenance;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;

public class PeriodicMaintenanceTickerBehavior extends TickerBehaviour{

	private static final long serialVersionUID = 1L;
	private AID bbAgent;
	private BeliefBase bfBase;
	private Logger log;
	private SimulatorInternals myMachine;

	public PeriodicMaintenanceTickerBehavior(Agent a, long period) {
		super(a, period);
	}

	public PeriodicMaintenanceTickerBehavior(Agent a, long period,
			BeliefBase bfBase) {

		super(a, period);
		reset(LocalMaintenanceAgent.prevMaintPeriod);
		this.bfBase = bfBase;

		this.bbAgent = (AID) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgentAID).
				getValue();

		log = LogManager.getLogger();
	}

	@Override
	protected void onTick() {

		this.myMachine = (SimulatorInternals) bfBase.getBelief(ID.Maintenance.BeliefBaseConst.machineHealth).getValue();

//		log.info("machine  : " + myMachine);
		if(bbAgent != null && myMachine != null && 
				myMachine.getStatus() != MachineStatus.FAILED) {

			PMaintenance maintJob = new PMaintenance(ID.Maintenance.maintJobPrefix + System.currentTimeMillis());
			maintJob.setExpectedStartTime(new Date());

			ZoneDataUpdate maintenanceJob = new ZoneDataUpdate.Builder(ID.Maintenance.ZoneData.preventiveMaintJob)
			.value(maintJob).Build();

			log.info("Sending maintenance.. " + maintJob);
			AgentUtil.sendZoneDataUpdate(this.bbAgent ,maintenanceJob, myAgent);

			bfBase.updateBelief(ID.Maintenance.BeliefBaseConst.preventiveMaintJob, maintJob);

		} else {

			this.bbAgent = (AID) bfBase.
					getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgentAID).
					getValue();
		}
	}
}
