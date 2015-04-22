package mas.customerproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import mas.jobproxy.Batch;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * Plan to cancel a previously accepted order
 * It updates zone-data of customer with the order to be cancelled.
 * GSA is subscribed to this zone-data and as it receives message about change in this zone-data,
 * It triggers its own plan to handle the scenario.
 */

public class CancelOrderPlan extends Behaviour implements PlanBody {
	private static final long serialVersionUID = 1L;

	private BeliefBase bfBase;
	private AID blackboard;
	private Batch canceledOrder;
	private boolean done = false;
	
	@Override
	public EndState getEndState() {
		return (done ? EndState.SUCCESSFUL : null);
	}

	@Override
	public void init(PlanInstance pInstance) {
		bfBase = pInstance.getBeliefBase();
		blackboard = (AID) bfBase.
				getBelief(ID.Customer.BeliefBaseConst.blackboardAgent).
				getValue();
		
		canceledOrder = (Batch) bfBase.
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
		} else {
			canceledOrder = (Batch) bfBase.
					getBelief(ID.Customer.BeliefBaseConst.CANCEL_ORDER).
					getValue();
		}
	}

	@Override
	public boolean done() {
		return done;
	}

}
