package mas.globalSchedulingproxy.agent;

import jade.core.AID;
import jade.lang.acl.MessageTemplate;
import java.util.HashSet;
import java.util.Set;
import mas.globalSchedulingproxy.goal.GetNoOfMachinesGoal;
import mas.globalSchedulingproxy.goal.RegisterAgentGoal;
import mas.globalSchedulingproxy.goal.RegisterServiceGoal;
import mas.globalSchedulingproxy.plan.*;
import mas.util.ID;
import mas.util.MessageIds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.belief.Belief;
import bdi4jade.belief.BeliefSet;
import bdi4jade.belief.TransientBelief;
import bdi4jade.belief.TransientBeliefSet;
import bdi4jade.core.BeliefBase;
import bdi4jade.core.Capability;
import bdi4jade.core.PlanLibrary;
import bdi4jade.plan.Plan;
import bdi4jade.util.plan.SimplePlan;

public abstract class AbstractGSCapability  extends Capability {

	private static final long serialVersionUID = 1L;
	private Logger log;

	public AbstractGSCapability(){
		super(new BeliefBase(getBeliefs()), new PlanLibrary(getPlans()));

	}

	public static Set<Belief<?>> getBeliefs() {
		Set<Belief<?>> beliefs = new HashSet<Belief<?>>();

		Belief<AID> BB_AID = 
				new TransientBelief<AID>(ID.GlobalScheduler.BeliefBaseConst.blackboardAgent);	

		BB_AID.setValue(new AID(ID.Blackboard.LocalName,AID.ISLOCALNAME));

		//no of machines = no of LSA
		BeliefSet<Integer> NoOfMachines = new 
				TransientBeliefSet<Integer>(ID.GlobalScheduler.BeliefBaseConst.NoOfMachines);		
		beliefs.add(BB_AID);
		beliefs.add(NoOfMachines);

		return beliefs;
	}

	public static Set<Plan> getPlans() {
		Set<Plan> plans = new HashSet<Plan>();
		plans.add(new SimplePlan(GetNoOfMachinesGoal.class,GetNoOfMachinesPlan.class));
		plans.add(new SimplePlan(RegisterServiceGoal.class, RegisterServicePlan.class));
		plans.add(new SimplePlan(RegisterAgentGoal.class,RegisterAgentToBlackboard.class));

		plans.add(new SimplePlan(
				MessageTemplate.MatchConversationId(MessageIds.msgcustomerConfirmedJobs),
				TakeOrderAndRaiseBid.class));

		plans.add(new SimplePlan
				(MessageTemplate.MatchConversationId(
						MessageIds.msgcustomerJobsUnderNegotiation),Negotiate.class));

		//		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgWaitingTime, )))

		return plans;
	}	

	@Override
	protected void setup() {
		log=LogManager.getLogger();		
		myAgent.addGoal(new RegisterServiceGoal());
		myAgent.addGoal(new RegisterAgentGoal());
		myAgent.addGoal(new GetNoOfMachinesGoal());
		//		log.info(myAgent.getAllGoals());
	}
}
