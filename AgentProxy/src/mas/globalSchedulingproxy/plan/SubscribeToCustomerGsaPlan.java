package mas.globalSchedulingproxy.plan;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.SubscribeID;
import mas.util.SubscriptionForm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class SubscribeToCustomerGsaPlan extends OneShotBehaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private AID blackBoard;
	private ArrayList<SubscribeID> listCustomer;
	private Logger log;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(PlanInstance planInstance) {
		bfBase = planInstance.getBeliefBase();

		listCustomer =  (ArrayList<SubscribeID>) bfBase.
				getBelief(ID.GlobalScheduler.BeliefBaseConst.customerList).
				getValue();
		
		blackBoard = (AID) bfBase.
				getBelief(ID.GlobalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();

		log = LogManager.getLogger();
	}

	@Override
	public void action() {
		SubscriptionForm subform = new SubscriptionForm();
		
		String[] params = {ID.Customer.ZoneData.customerConfirmedJobs,ID.Customer.ZoneData.newWorkOrderFromCustomer,
				ID.Customer.ZoneData.customerJobsUnderNegotiation};
		
		for(int i = 0; i < listCustomer.size(); i++) {
			if(! listCustomer.get(i).isSubscribed()) {
				subform.AddSubscriptionReq(listCustomer.get(i).getAgent(), params);
				listCustomer.get(i).setSubscribed(true);
			}
		}
		bfBase.updateBelief(ID.GlobalScheduler.BeliefBaseConst.customerList, listCustomer);
		AgentUtil.subscribeToParam(myAgent, blackBoard, subform);
	}

}
