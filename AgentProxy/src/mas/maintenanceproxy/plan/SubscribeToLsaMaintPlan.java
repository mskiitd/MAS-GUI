package mas.maintenanceproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.SubscriptionForm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class SubscribeToLsaMaintPlan  extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private AID lsa;
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
		lsa = (AID) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.lsAgent).
				getValue();
		blackBoard = (AID) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgent).
				getValue();
		log = LogManager.getLogger();
	}

	@Override
	public void action() {
		SubscriptionForm subform = new SubscriptionForm();
		
		String[] lParams = { ID.LocalScheduler.ZoneData.MaintConfirmationLSA };
		
		subform.AddSubscriptionReq(lsa, lParams);
		AgentUtil.subscribeToParam(myAgent, blackBoard, subform);
		done = true;
	}

	@Override
	public boolean done() {
		return done;
	}

}
