package mas.customerproxy.plan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.SubscriptionForm;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class SubscribeToZonesPlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private AID gsa;
	private AID blackBoard;
	private boolean done = false;
	private Logger log;

	@Override
	public EndState getEndState() {
		return (done ? EndState.SUCCESSFUL : null );
	}

	@Override
	public void init(PlanInstance planInstance) {
		bfBase = planInstance.getBeliefBase();
		gsa = (AID) bfBase.
				getBelief(ID.Customer.BeliefBaseConst.gsAgent).
				getValue();
		blackBoard = (AID) bfBase.
				getBelief(ID.Customer.BeliefBaseConst.blackboardAgent).
				getValue();
		log = LogManager.getLogger();
	}

	@Override
	public void action() {
		SubscriptionForm subform = new SubscriptionForm();

		String[] params = { ID.GlobalScheduler.ZoneData.GSAjobsUnderNegaotiation,
				ID.GlobalScheduler.ZoneData.GSAConfirmedOrder,
				ID.GlobalScheduler.ZoneData.completedJobByGSA ,
				ID.GlobalScheduler.ZoneData.dueDateChangeBatches,
				ID.GlobalScheduler.ZoneData.rejectedOrders};

		subform.AddSubscriptionReq(gsa, params);
		AgentUtil.subscribeToParam(myAgent, blackBoard, subform);
		done = true;
	}

	@Override
	public boolean done() {
		return done;
	}

}
