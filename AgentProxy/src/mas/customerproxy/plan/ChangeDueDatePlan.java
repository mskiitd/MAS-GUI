package mas.customerproxy.plan;

import mas.jobproxy.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;
import jade.core.AID;
import jade.core.behaviours.Behaviour;

public class ChangeDueDatePlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;

	private BeliefBase bfBase;
	private AID blackboard;
	private job changeDDorder;
	private boolean done = false;
	
	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		bfBase = pInstance.getBeliefBase();
		blackboard = (AID) bfBase.
				getBelief(ID.Customer.BeliefBaseConst.blackboardAgent).
				getValue();
		
		changeDDorder = (job) bfBase.
				getBelief(ID.Customer.BeliefBaseConst.CHANGE_DUEDATE_JOB).
				getValue();
	}

	@Override
	public void action() {
		if(changeDDorder != null) {
			ZoneDataUpdate changedDueDateJobDataUpdate = new ZoneDataUpdate.Builder(
					ID.Customer.ZoneData.customerChangeDDorders).
					value(changeDDorder).Build();

			AgentUtil.sendZoneDataUpdate( this.blackboard,
					changedDueDateJobDataUpdate,myAgent);
			done = true;
		}
	}

	@Override
	public boolean done() {
		return done;
	}

}
