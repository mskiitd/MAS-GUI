package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import java.util.Date;
import mas.machineproxy.gui.MachineGUI;
import mas.machineproxy.gui.MaintenanceActivityCodeFrame;
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
	private int step = 0;

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

		MaintenanceActivityCodeFrame aCode = new MaintenanceActivityCodeFrame(maintJob);
	}

	@Override
	public void action() {
		switch(step) {
		case 0:
			if(maintJob.getActivityCode() != null) {
				step = 1;
			} else{
				block(100);
			}
			break;
		case 1:
			if(maintJob != null ) {

				maintJob.setActualFinishTime(new Date());
				maintJob.setMaintStatus(MaintStatus.COMPLETE);
				
				bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.currentMaintJob, null);

				ZoneDataUpdate finishedMaint = new ZoneDataUpdate.
						Builder(ID.LocalScheduler.ZoneData.MaintConfirmationLSA).
						setReplyWith(maintJob.getMaintId()).
						value(maintJob).Build();

				AgentUtil.sendZoneDataUpdate(blackboard ,finishedMaint, myAgent);

				if(gui != null) {
					gui.machineIdle();
					gui.enablePmStart();
					if(gui.isMachinePaused()){
						gui.resumeMachine();
					}
				}

				done = true;
			} else {
				maintJob = (PMaintenance) bfBase.
						getBelief(ID.LocalScheduler.BeliefBaseConst.currentMaintJob).
						getValue();
			}
			break;
		}
	}

	@Override
	public boolean done() {
		return done;
	}
}
