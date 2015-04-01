package mas.customerproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import mas.customerproxy.agent.CustomerAgent;
import mas.customerproxy.gui.CustomerNegotiateProxyGUI;
import mas.jobproxy.Batch;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class NegotiationGuiPlan extends Behaviour implements PlanBody {
	private static final long serialVersionUID = 1L;
	private Logger log;
	private BeliefBase bfBase;
	private AID bba;
	private Batch negotiationJob;
	private String replyWith;
	private boolean done = false;

	@Override
	public EndState getEndState() {
		return (done ? EndState.SUCCESSFUL : null);
	}

	@Override
	public void init(PlanInstance pInstance) {

		ACLMessage msg = ( (MessageGoal)pInstance.getGoal()).getMessage();

		try {
			negotiationJob = (Batch) msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}

		this.bfBase = pInstance.getBeliefBase();

		this.bba = (AID) bfBase
				.getBelief(ID.Customer.BeliefBaseConst.blackboardAgent)
				.getValue();

		replyWith = ((MessageGoal)(pInstance.getGoal())).
				getMessage().getReplyWith();
	}

	@Override
	public void action() {
		CustomerNegotiateProxyGUI nego = new CustomerNegotiateProxyGUI(
				(CustomerAgent)myAgent, negotiationJob);
		done = true;
	}

	public void setNegotiation(Batch j) {
		/**
		 *  Write your own negotiation logic here. You have job j to negotiate with scheduler.
		 *  Take this job as input and change due date or profit or some other parameter
		 *  and return that job from this method.
		 *  first check if the sent negotiation is acceptable or not.
		 *  if it is acceptable, then job is simply being sent back to blackboard.
		 */

		long myDate = (long) (j.getDueDateByCustomer().getTime() ); 
		double newprofit = 0.9 * j.getProfit();

		long newDate = j.getWaitingTime();

		if(myDate >=  newDate ) {
			ZoneDataUpdate negotiationJobDataUpdate = new ZoneDataUpdate.Builder(
					ID.Customer.ZoneData.customerConfirmedJobs).
					value(negotiationJob).
					setReplyWith(replyWith).
					Build();

			AgentUtil.sendZoneDataUpdate(this.bba,negotiationJobDataUpdate, myAgent);

			// Update this job in the list of completed jobs of customer GUI
			log.info("Updating accepted jobs in the queue of customer");
			CustomerAgent.mygui.addAcceptedJob(j);

			return;
		}else {
			j.setDueDateMillisByCustomer((myDate + newDate)/2 );
			j.setProfit(newprofit);
			log.info("************" + negotiationJob.getDueDateByCustomer() );
			ZoneDataUpdate negotiationJobDataUpdate=new ZoneDataUpdate.Builder(
					ID.Customer.ZoneData.customerJobsUnderNegotiation).
					value(negotiationJob).setReplyWith(replyWith).
					Build();

			AgentUtil.sendZoneDataUpdate(this.bba,negotiationJobDataUpdate, myAgent);
		}
	}

	@Override
	public boolean done() {
		return done;
	}
}
