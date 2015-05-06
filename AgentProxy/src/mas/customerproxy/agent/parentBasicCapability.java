package mas.customerproxy.agent;

import jade.core.AID;
import jade.lang.acl.MessageTemplate;

import java.util.HashSet;
import java.util.Set;

import mas.customerproxy.goal.CancelOrderGoal;
import mas.customerproxy.goal.RegisterAgentToBlackboardGoal;
import mas.customerproxy.goal.SendConfirmedOrderGoal;
import mas.customerproxy.goal.DispatchBatchGoal;
import mas.customerproxy.plan.CancelOrderPlan;
import mas.customerproxy.plan.ChangeDueDate;
import mas.customerproxy.plan.DispatchJobPlan;
import mas.customerproxy.plan.HandleRejectedOrder;
import mas.customerproxy.plan.ReceiveCompletedBatchPlan;
import mas.customerproxy.plan.RegisterCustomerAgentToBlackboardPlan;
import mas.customerproxy.plan.SendConfirmedOrderPlan;
import mas.jobproxy.Batch;
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
 * This capability contains goals of customer -
 * For generating jobs
 * For dispatching them to Global scheduling agent
 */

public class parentBasicCapability extends Capability {

	private static final long serialVersionUID = 1L;

	public parentBasicCapability() {
		super(new BeliefBase(getBeliefs()), new PlanLibrary(getPlans()));
	}

	/**
	 * <b> Add beliefs for </b> <i>
	 * </br> blackboard agent ID
	 * </br> Current Batch to be sent to blackboard
	 * </br> Current Confirmed batch (after negotiation)
	 * </br>  Cancelled order by customer
	 * </br> Batch whose due date is changed
	 * </br> Current batch under negotiation
	 * </br> GUI of customer agent
	 * </i>
	 * 
	 * @return set of beliefs
	 */
	public static Set<Belief<?>> getBeliefs() {
		Set<Belief<?>> beliefs = new HashSet<Belief<?>>();

		// for storing blackboard agent's AID
		Belief<AID> bboard = new TransientBelief<AID>(ID.Customer.BeliefBaseConst.blackboardAgent);

		// for current job generated, which is to be sent to GSA for negotiation etc.
		Belief<Batch> currentJob = new TransientBelief<Batch>(ID.Customer.BeliefBaseConst.CURRENT_JOB2SEND);

		// for confirmed job after negotiation is complete
		Belief<Batch> confirmedOrder = new TransientBelief<Batch>(ID.Customer.BeliefBaseConst.CURRENT_CONFIRMED_JOB);

		// for order which has to be canceled
		Belief<String> cancelOrder = new TransientBelief<String>(ID.Customer.BeliefBaseConst.CANCEL_ORDER);

		// for job whose due date has to be changed
		Belief<String> changeDueDate = new TransientBelief<String>(ID.Customer.BeliefBaseConst.CHANGE_DUEDATE_JOB);

		// for current job which is under negotiation
		Belief<Batch> currentNegJob = new TransientBelief<Batch>(ID.Customer.BeliefBaseConst.CURRENT_NEGOTIATION_BATCH);

		Belief<Batch> customerGUI = new TransientBelief<Batch>(ID.Customer.BeliefBaseConst.CUSTOMER_GUI);

		beliefs.add(bboard);
		beliefs.add(currentJob);
		beliefs.add(cancelOrder);
		beliefs.add(changeDueDate);
		beliefs.add(confirmedOrder);
		beliefs.add(currentNegJob);
		beliefs.add(customerGUI);

		return beliefs;
	}

	/**
	 * <h2>Add plans for </h2>
	 * Registering customer on blackboard </br>
	 * Dispatching generated batch </br>
	 * Sending confirmed batch ( after negotiation) </br>
	 * Canceling the order </br>
	 * Receiving completed batch </br>
	 * changing due date of batch </br>
	 * handling rejected order from GSA </br>
	 * 
	 * @return Set of plans for this agent
	 */
	public static Set<Plan> getPlans() {
		Set<Plan> plans = new HashSet<Plan>();

		plans.add(new SimplePlan(RegisterAgentToBlackboardGoal.class,
				RegisterCustomerAgentToBlackboardPlan.class));

		plans.add(new SimplePlan(DispatchBatchGoal.class,DispatchJobPlan.class));
		plans.add(new SimplePlan(SendConfirmedOrderGoal.class,SendConfirmedOrderPlan.class));
		plans.add(new SimplePlan(CancelOrderGoal.class,CancelOrderPlan.class) );

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgJobCompletion),
				ReceiveCompletedBatchPlan.class ));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgChangeDueDate),
				ChangeDueDate.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.RejectedOrder)
				, HandleRejectedOrder.class));

		return plans;
	}	

	@Override
	protected void setup() {
	}
}
