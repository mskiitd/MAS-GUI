package mas.customerproxy.agent;

import jade.core.AID;
import jade.lang.acl.MessageTemplate;

import java.util.HashSet;
import java.util.Set;

import mas.customerproxy.goal.ReceiveCompletedJobGoal;
import mas.customerproxy.goal.RegisterAgentToBlackboardGoal;
import mas.customerproxy.goal.SendConfirmedOrderGoal;
import mas.customerproxy.goal.dispatchJobGoal;
import mas.customerproxy.gui.CancelOrderGoal;
import mas.customerproxy.gui.ChangeDueDateGoal;
import mas.customerproxy.plan.CancelOrderPlan;
import mas.customerproxy.plan.ChangeDueDatePlan;
import mas.customerproxy.plan.DispatchJobPlan;
import mas.customerproxy.plan.ReceiveCompletedJobPlan;
import mas.customerproxy.plan.RegisterCustomerAgentToBlackboardPlan;
import mas.customerproxy.plan.SendConfirmedOrderPlan;
import mas.jobproxy.job;
import mas.util.ID;
import mas.util.MessageIds;
import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
import bdi4jade.core.BeliefBase;
import bdi4jade.core.Capability;
import bdi4jade.core.PlanLibrary;
import bdi4jade.plan.Plan;
import bdi4jade.util.plan.SimplePlan;

/**
 * @author Anand Prajapati
 * 
 * This capability contains two goals of customer - one for generating jobs
 * and one for dispatching them to Global scheduling agent
 * 
 */

public class parentBasicCapability extends Capability {

	private static final long serialVersionUID = 1L;

	public parentBasicCapability() {
		super(new BeliefBase(getBeliefs()), new PlanLibrary(getPlans()));
	}

	public static Set<Belief<?>> getBeliefs() {
		Set<Belief<?>> beliefs = new HashSet<Belief<?>>();

		// for storing blackboard agent's AID
		Belief<AID> bboard = new TransientBelief<AID>(ID.Customer.BeliefBaseConst.blackboardAgent);

		// for current job generated, which is to be sent to GSA for negotiation etc.
		Belief<job> currentJob = new TransientBelief<job>(ID.Customer.BeliefBaseConst.CURRENT_JOB2SEND);

		// for confirmed job after negotiation is complete
		Belief<job> confirmedOrder = new TransientBelief<job>(ID.Customer.BeliefBaseConst.CURRENT_CONFIRMED_JOB);

		// for order which has to be canceled
		Belief<String> cancelOrder = new TransientBelief<String>(ID.Customer.BeliefBaseConst.CANCEL_ORDER);

		// for job whose due date has to be changed
		Belief<String> changeDueDate = new TransientBelief<String>(ID.Customer.BeliefBaseConst.CHANGE_DUEDATE_JOB);

		// for current job which is under negotiation
		Belief<job> currentNegJob = new TransientBelief<job>(ID.Customer.BeliefBaseConst.CURRENT_NEGOTIATION_JOB);

		beliefs.add(bboard);
		beliefs.add(currentJob);
		beliefs.add(cancelOrder);
		beliefs.add(changeDueDate);
		beliefs.add(confirmedOrder);
		beliefs.add(currentNegJob);

		return beliefs;
	}

	public static Set<Plan> getPlans() {
		Set<Plan> plans = new HashSet<Plan>();

		plans.add(new SimplePlan(RegisterAgentToBlackboardGoal.class,
				RegisterCustomerAgentToBlackboardPlan.class));

		plans.add(new SimplePlan(dispatchJobGoal.class,DispatchJobPlan.class));
		plans.add(new SimplePlan(SendConfirmedOrderGoal.class,SendConfirmedOrderPlan.class));
		plans.add(new SimplePlan(CancelOrderGoal.class,CancelOrderPlan.class) );
		plans.add(new SimplePlan(ChangeDueDateGoal.class,ChangeDueDatePlan.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgJobCompletion),
				ReceiveCompletedJobPlan.class ));

		return plans;
	}	

	@Override
	protected void setup() {

	}
}
