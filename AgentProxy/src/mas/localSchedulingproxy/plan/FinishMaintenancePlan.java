package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;

import java.util.Date;

import mas.machineproxy.gui.MachineGUI;
import mas.maintenanceproxy.classes.MaintStatus;
import mas.maintenanceproxy.classes.PMaintenance;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class FinishMaintenancePlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private AID blackboard;
	private PMaintenance maintJob;
	private boolean done = false;
	private MachineGUI gui;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {

		bfBase = pInstance.getBeliefBase();
		maintJob = (PMaintenance) bfBase.getBelief(ID.LocalScheduler.BeliefBaseConst.currentMaintJob).getValue();
		blackboard = (AID) bfBase.getBelief(ID.LocalScheduler.BeliefBaseConst.blackboardAgent).getValue();
		gui = (MachineGUI) bfBase.getBelief(ID.LocalScheduler.BeliefBaseConst.gui_machine).getValue();
	}

	@Override
	public void action() {
		if(maintJob != null ) {

			maintJob.setActualFinishTime(new Date());
			maintJob.setMaintStatus(MaintStatus.COMPLETE);
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.currentMaintJob, null);

			ZoneDataUpdate finishedMaint = new ZoneDataUpdate.
					Builder(ID.LocalScheduler.ZoneData.MaintConfirmationLSA).
					value(maintJob).Build();

			AgentUtil.sendZoneDataUpdate(blackboard ,finishedMaint, myAgent);

			if(gui != null) {
				gui.machineIdle();
				gui.enablePmStart();
			}

			done = true;
		} else {
			maintJob = (PMaintenance) bfBase.getBelief(ID.LocalScheduler.BeliefBaseConst.currentMaintJob).getValue();
		}
	}

	@Override
	public boolean done() {
		return done;
	}

}
