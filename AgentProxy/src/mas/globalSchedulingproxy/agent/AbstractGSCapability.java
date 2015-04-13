package mas.globalSchedulingproxy.agent;

import jade.core.AID;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mas.globalSchedulingproxy.database.BatchDataBase;
import mas.globalSchedulingproxy.goal.GSASendNegotitationGoal;
import mas.globalSchedulingproxy.goal.GetNoOfMachinesGoal;
import mas.globalSchedulingproxy.goal.LoadBatchOperationDetailsGoal;
import mas.globalSchedulingproxy.goal.LookUpAgentsGsaGoal;
import mas.globalSchedulingproxy.goal.QueryJobGoal;
import mas.globalSchedulingproxy.goal.RegisterAgentToBlackBoardGoal;
import mas.globalSchedulingproxy.goal.RegisterServiceGoal;
import mas.globalSchedulingproxy.goal.SubscribeToCustomerGsaGoal;
import mas.globalSchedulingproxy.goal.SubscribeToLsaGoal;
import mas.globalSchedulingproxy.gui.WebLafGSA;
import mas.globalSchedulingproxy.plan.AskForWaitingTime;
import mas.globalSchedulingproxy.plan.CallBackChangeDueDatePlan;
import mas.globalSchedulingproxy.plan.GSASendNegotiationJobPlan;
import mas.globalSchedulingproxy.plan.GetNoOfMachinesPlan;
import mas.globalSchedulingproxy.plan.HandleCompletedOrderbyLSAPlan;
import mas.globalSchedulingproxy.plan.LoadBatchOperationDetailsPlan;
import mas.globalSchedulingproxy.plan.LookUpAgentsGsaPlan;
import mas.globalSchedulingproxy.plan.NegotiateViaGuiPlan;
import mas.globalSchedulingproxy.plan.QueryFromLSA;
import mas.globalSchedulingproxy.plan.RegisterAgentToBlackboardPlan;
import mas.globalSchedulingproxy.plan.RegisterServicePlan;
import mas.globalSchedulingproxy.plan.SubscribeToCustomerGsaPlan;
import mas.globalSchedulingproxy.plan.SubscribeToLsaGsaPlan;
import mas.globalSchedulingproxy.plan.TakeOrderAndRaiseBidPlan;
import mas.jobproxy.Batch;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.SubscribeID;

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

	public AbstractGSCapability() {
		super(new BeliefBase(getBeliefs()), new PlanLibrary(getPlans()));
	}

	public static Set<Belief<?>> getBeliefs() {
		Set<Belief<?>> beliefs = new HashSet<Belief<?>>();

		Belief<AID> BB_AID = new TransientBelief<AID>(
				ID.GlobalScheduler.BeliefBaseConst.blackboardAgent);	

		Belief<ArrayList<SubscribeID>> customerSet = new TransientBelief<ArrayList<SubscribeID>>(
				ID.GlobalScheduler.BeliefBaseConst.customerList);

		Belief<ArrayList<SubscribeID>> lsaList = new TransientBelief<ArrayList<SubscribeID>>(
				ID.GlobalScheduler.BeliefBaseConst.lsaList); 

		Belief<String> DueDateCalcMethod = new TransientBelief<String>(ID.GlobalScheduler.
				BeliefBaseConst.DueDateCalcMethod);

		//no of machines = no. of LSA		
		Belief<Integer> NoOfMachines = new TransientBelief<Integer>(ID.GlobalScheduler.
				BeliefBaseConst.NoOfMachines);

		Belief<Batch> query = new TransientBelief<Batch>(
				ID.GlobalScheduler.BeliefBaseConst.GSAqueryJob);

		Belief<Batch> underNegotiation = new TransientBelief<Batch>(
				ID.GlobalScheduler.BeliefBaseConst.Current_Negotiation_Job);

		Belief<WebLafGSA> GSA_gui = new TransientBelief<WebLafGSA>(
				ID.GlobalScheduler.BeliefBaseConst.GSA_GUI_instance); 

		Belief<BatchDataBase> dBase = new TransientBelief<BatchDataBase>(
				ID.GlobalScheduler.BeliefBaseConst.batchDatabase);

		lsaList.setValue(new ArrayList<SubscribeID>());
		customerSet.setValue(new ArrayList<SubscribeID>());
		dBase.setValue(null);
		DueDateCalcMethod.setValue(ID.GlobalScheduler.OtherConst.LocalDueDate);
		underNegotiation.setValue(null);

		beliefs.add(BB_AID);
		beliefs.add(NoOfMachines);
		beliefs.add(DueDateCalcMethod);
		beliefs.add(query);
		beliefs.add(underNegotiation);
		beliefs.add(GSA_gui);
		beliefs.add(dBase);
		beliefs.add(customerSet);
		beliefs.add(lsaList);

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

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(
				MessageIds.msgcustomerJobsUnderNegotiation), NegotiateViaGuiPlan.class));

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

		plans.add(new SimplePlan(LoadBatchOperationDetailsGoal.class, LoadBatchOperationDetailsPlan.class));

		plans.add(new SimplePlan(SubscribeToLsaGoal.class, SubscribeToLsaGsaPlan.class));

		plans.add(new SimplePlan(SubscribeToCustomerGsaGoal.class, SubscribeToCustomerGsaPlan.class));

		plans.add(new SimplePlan(LookUpAgentsGsaGoal.class, LookUpAgentsGsaPlan.class));

		return plans;
	}	

	@Override
	protected void setup() {
		log = LogManager.getLogger();		

		myAgent.addGoal(new RegisterServiceGoal());
		myAgent.addGoal(new RegisterAgentToBlackBoardGoal());
		myAgent.addGoal(new LookUpAgentsGsaGoal());
		myAgent.addGoal(new LoadBatchOperationDetailsGoal());
		myAgent.addGoal(new GetNoOfMachinesGoal());
	}

}
