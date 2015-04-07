package mas.globalSchedulingproxy.agent;

import jade.core.AID;
import jade.lang.acl.MessageTemplate;

import java.util.HashSet;
import java.util.Set;

import mas.globalSchedulingproxy.goal.GSASendNegotitationGoal;
import mas.globalSchedulingproxy.goal.GetNoOfMachinesGoal;
import mas.globalSchedulingproxy.goal.QueryJobGoal;
import mas.globalSchedulingproxy.goal.RegisterAgentToBlackBoardGoal;
import mas.globalSchedulingproxy.goal.RegisterServiceGoal;
import mas.globalSchedulingproxy.gui.WebLafGSA;
import mas.globalSchedulingproxy.plan.AskForWaitingTime;
import mas.globalSchedulingproxy.plan.CallBackChangeDueDatePlan;
import mas.globalSchedulingproxy.plan.GSASendNegotiationJobPlan;
import mas.globalSchedulingproxy.plan.GetNoOfMachinesPlan;
import mas.globalSchedulingproxy.plan.HandleCompletedOrderbyLSAPlan;
import mas.globalSchedulingproxy.plan.NegotiateViaGuiPlan;
import mas.globalSchedulingproxy.plan.QueryFromLSA;
import mas.globalSchedulingproxy.plan.RegisterAgentToBlackboardPlan;
import mas.globalSchedulingproxy.plan.RegisterServicePlan;
import mas.globalSchedulingproxy.plan.TakeOrderAndRaiseBidPlan;
import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.util.ID;
import mas.util.MessageIds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
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

		Belief<AID> BB_AID = new TransientBelief<AID>(ID.GlobalScheduler.BeliefBaseConst.blackboardAgent);		
		BB_AID.setValue(new AID(ID.Blackboard.LocalName,false));

		Belief<String> DueDateCalcMethod = new TransientBelief<String>(ID.GlobalScheduler.
				BeliefBaseConst.DueDateCalcMethod);
		DueDateCalcMethod.setValue(ID.GlobalScheduler.OtherConst.LocalDueDate);

		//no of machines = no. of LSA		
		Belief<Integer> NoOfMachines=new TransientBelief<Integer>(ID.GlobalScheduler.
				BeliefBaseConst.NoOfMachines);

		Belief<Batch> query = new TransientBelief<Batch>(ID.GlobalScheduler.BeliefBaseConst.GSAqueryJob);

		Belief<Batch> underNegotiation = new 
				TransientBelief<Batch>(ID.GlobalScheduler.BeliefBaseConst.Current_Negotiation_Job);
		underNegotiation.setValue(null);

		Belief<WebLafGSA> GSA_gui = new TransientBelief<WebLafGSA>
		(ID.GlobalScheduler.BeliefBaseConst.GSA_GUI_instance); 

		beliefs.add(BB_AID);
		beliefs.add(NoOfMachines);
		beliefs.add(DueDateCalcMethod);
		beliefs.add(query);
		beliefs.add(underNegotiation);
		beliefs.add(GSA_gui);

		return beliefs;
	}

	public static Set<Plan> getPlans() {
		Set<Plan> plans = new HashSet<Plan>();

		plans.add(new SimplePlan(RegisterServiceGoal.class, RegisterServicePlan.class));

		plans.add(new SimplePlan(RegisterAgentToBlackBoardGoal.class,
				RegisterAgentToBlackboardPlan.class));

		plans.add(new SimplePlan(GetNoOfMachinesGoal.class, GetNoOfMachinesPlan.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgnewWorkOrderFromCustomer),
				AskForWaitingTime.class));

		plans.add(new SimplePlan
				(MessageTemplate.MatchConversationId(MessageIds.msgcustomerJobsUnderNegotiation),
						NegotiateViaGuiPlan.class));

		plans.add(new SimplePlan(GSASendNegotitationGoal.class, GSASendNegotiationJobPlan.class));

		plans.add(new SimplePlan(
				MessageTemplate.MatchConversationId(MessageIds.msgcustomerConfirmedJobs),
				TakeOrderAndRaiseBidPlan.class));

		plans.add(new SimplePlan(
				MessageTemplate.MatchConversationId(MessageIds.msgLSAfinishedJobs),
				HandleCompletedOrderbyLSAPlan.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgreqToChangeDueDate),
				CallBackChangeDueDatePlan.class));

		plans.add(new SimplePlan(QueryJobGoal.class, QueryFromLSA.class));

		return plans;
	}	

	@Override
	protected void setup() {
		log = LogManager.getLogger();		

		myAgent.addGoal(new RegisterServiceGoal());
		myAgent.addGoal(new RegisterAgentToBlackBoardGoal());
		myAgent.addGoal(new GetNoOfMachinesGoal());
	}

}
