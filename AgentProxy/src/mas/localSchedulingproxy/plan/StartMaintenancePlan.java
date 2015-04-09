package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;

import java.util.ArrayList;

import mas.machineproxy.gui.MachineGUI;
import mas.maintenanceproxy.classes.PMaintenance;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class StartMaintenancePlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;

	private BeliefBase bfBase;
	private MachineGUI gui;
	private ArrayList<PMaintenance> maintJobList;
	private Logger log;
	private AID blackboard;
	private boolean done = false;

	@Override
	public EndState getEndState() {
		return (done ? EndState.SUCCESSFUL : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(PlanInstance pInstance) {

		this.log = LogManager.getLogger();
		this.bfBase = pInstance.getBeliefBase();
		this.gui = (MachineGUI) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.gui_machine).
				getValue();
		maintJobList = (ArrayList<PMaintenance>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.preventiveJobsQueue).
				getValue();
		blackboard = (AID) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();
	}

	@Override
	public void action() {
		log.info("No of Maintenance jobs : " + maintJobList.size());
		if(maintJobList.size() > 0) {
			PMaintenance maintJob = maintJobList.remove(0);

			ZoneDataUpdate maintJobForMachine = new ZoneDataUpdate.
					Builder(ID.LocalScheduler.ZoneData.maintenanceJobForMachine).value(maintJob).Build();

			AgentUtil.sendZoneDataUpdate(blackboard, maintJobForMachine, myAgent);

			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.preventiveJobsQueue, maintJobList);
		}
		else {
			gui.showNoMaintJobPopup();
		}
		done = true;
	}

	@Override
	public boolean done() {
		return done;
	}

}
