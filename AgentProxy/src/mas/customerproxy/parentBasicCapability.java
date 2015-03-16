package mas.customerproxy;

import jade.core.AID;
import jade.lang.acl.MessageTemplate;

import java.util.HashSet;
import java.util.Set;

import mas.customer.goal.GenerateJobGoal;
import mas.customer.plan.jobGeneratorPlan;
import mas.customerproxy.goal.RegisterAgentToBlackboardGoal;
import mas.customerproxy.goal.dispatchJobGoal;
import mas.customerproxy.plan.ConfirmOrder;
import mas.customerproxy.plan.DispatchJobPlan;
import mas.customerproxy.plan.RegisterCustomerAgentToBlackboardPlan;
import mas.globalSchedulingproxy.plan.Negotiate;
import mas.job.job;
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

		Belief<AID> bboard = new TransientBelief<AID>(ID.Customer.BeliefBaseConst.blackboardAgent);
		Belief<job> currentJob = new TransientBelief<job>(ID.Customer.BeliefBaseConst.CURRENT_JOB);
		
		beliefs.add(bboard);
		beliefs.add(currentJob);
		return beliefs;
	}
	
	public static Set<Plan> getPlans() {
		Set<Plan> plans = new HashSet<Plan>();
		
		plans.add(new SimplePlan(RegisterAgentToBlackboardGoal.class,
					RegisterCustomerAgentToBlackboardPlan.class));

		plans.add(new SimplePlan(dispatchJobGoal.class,
				DispatchJobPlan.class));
		
		return plans;
	}	
	
	@Override
	protected void setup() {
		
	}
}
