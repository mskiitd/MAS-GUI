package mas.customerproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import mas.jobproxy.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class CancelOrderPlan extends Behaviour implements PlanBody {
	private static final long serialVersionUID = 1L;

	private BeliefBase bfBase;
	private AID blackboard;
	private job canceledOrder;
	private boolean done = false;
	
	@Override
	public EndState getEndState() {
		return (done ?EndState.SUCCESSFUL : null);
	}

	@Override
	public void init(PlanInstance pInstance) {
		bfBase = pInstance.getBeliefBase();
		blackboard = (AID) bfBase.
				getBelief(ID.Customer.BeliefBaseConst.blackboardAgent).
				getValue();
		
		canceledOrder = (job) bfBase.
				getBelief(ID.Customer.BeliefBaseConst.CANCEL_ORDER).
				getValue();
	}

	@Override
	public void action() {
		if(canceledOrder != null) {
			ZoneDataUpdate canceledJobDataUpdate = new ZoneDataUpdate.Builder(
					ID.Customer.ZoneData.customerCanceledOrders).
					value(canceledOrder).Build();

			AgentUtil.sendZoneDataUpdate( this.blackboard,
					canceledJobDataUpdate,myAgent);
			done = true;
		}
	}

	@Override
	public boolean done() {
		return done;
	}

}
