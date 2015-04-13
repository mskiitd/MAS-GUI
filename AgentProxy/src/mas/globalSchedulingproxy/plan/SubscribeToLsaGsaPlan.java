package mas.globalSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;

import java.util.ArrayList;

import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.SubscribeID;
import mas.util.SubscriptionForm;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class SubscribeToLsaGsaPlan extends OneShotBehaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private AID lsa;
	private AID blackBoard;
	private ArrayList<SubscribeID> listLsa;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(PlanInstance planInstance) {
		bfBase = planInstance.getBeliefBase();

		listLsa =  (ArrayList<SubscribeID>) bfBase.
				getBelief(ID.GlobalScheduler.BeliefBaseConst.lsaList).
				getValue();
		
		blackBoard = (AID) bfBase.
				getBelief(ID.GlobalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();
	}

	@Override
	public void action() {
		SubscriptionForm subform = new SubscriptionForm();

		String[] LSAparams = {ID.LocalScheduler.ZoneData.WaitingTime, ID.LocalScheduler.ZoneData.bidForJob,
				ID.LocalScheduler.ZoneData.finishedBatch, ID.LocalScheduler.ZoneData.QueryResponse};
		
		for(int i = 0; i < listLsa.size(); i++) {
			if(! listLsa.get(i).isSubscribed()) {
				subform.AddSubscriptionReq(listLsa.get(i).getAgent(), LSAparams);
				listLsa.get(i).setSubscribed(true);
			}
		}
		bfBase.updateBelief(ID.GlobalScheduler.BeliefBaseConst.customerList, listLsa);		
		AgentUtil.subscribeToParam(myAgent, blackBoard, subform);
	}
}
