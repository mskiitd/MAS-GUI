package mas.customerproxy.plan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.jobproxy.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;

public class ConfirmOrder extends OneShotBehaviour implements PlanBody{

	private Logger log;
	private BeliefBase bfBase;
	private AID bba;
	private job ConfirmedJob;
	private String replyWith;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();
		this.bba = (AID) bfBase
				.getBelief(ID.Customer.BeliefBaseConst.blackboardAgent)
				.getValue();

		try {
			this.ConfirmedJob = (job)((MessageGoal)(pInstance.getGoal())).
					getMessage().getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		
		replyWith=((MessageGoal)(pInstance.getGoal())).
				getMessage().getReplyWith();
		
	}

	@Override
	public void action() {
		
		ZoneDataUpdate ConfirmedOrderZoneDataUpdate=new ZoneDataUpdate.Builder(ID.Customer.ZoneData.customerConfirmedJobs)
		.value(ConfirmedJob).setReplyWith(replyWith).Build();
		
		AgentUtil.sendZoneDataUpdate(bba, ConfirmedOrderZoneDataUpdate, myAgent);
	}
}
