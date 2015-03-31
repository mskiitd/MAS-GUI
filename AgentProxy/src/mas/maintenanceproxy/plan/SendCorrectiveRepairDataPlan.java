package mas.maintenanceproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class SendCorrectiveRepairDataPlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private AID blackboard;
	private BeliefBase bfBase;
	private boolean done = false;
	private String repairData = null;

	@Override
	public void action() {
		if(repairData != null) {
			ZoneDataUpdate correctiveRepairUpdate = new ZoneDataUpdate.Builder(
					ID.Maintenance.ZoneData.correctiveMaintdata).
					value(repairData).
					Build();

			AgentUtil.sendZoneDataUpdate(blackboard ,correctiveRepairUpdate, myAgent);
			done = true;
		} else {
			this.repairData = (String) bfBase.
					getBelief(ID.Maintenance.BeliefBaseConst.correctiveRepairData).
					getValue();
		}
	}

	@Override
	public boolean done() {
		return done;
	}

	public EndState getEndState() {
		return (done ? EndState.SUCCESSFUL : null );
	}

	public void init(PlanInstance planInstance) {
		bfBase = planInstance.getBeliefBase();

		this.blackboard = (AID) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgentAID).
				getValue();

		this.repairData = (String) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.correctiveRepairData).
				getValue();
	}
}
