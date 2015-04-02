package mas.localSchedulingproxy.capability;

import jade.core.AID;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mas.jobproxy.Batch;
import mas.localSchedulingproxy.database.OperationDataBase;
import mas.localSchedulingproxy.goal.JobSchedulingGoal;
import mas.localSchedulingproxy.goal.RegisterLSAgentServiceGoal;
import mas.localSchedulingproxy.goal.RegisterLSAgentToBlackboardGoal;
import mas.localSchedulingproxy.goal.UpdateOperationDatabaseGoal;
import mas.localSchedulingproxy.plan.EnqueueBatchPlan;
import mas.localSchedulingproxy.plan.BatchSchedulingPlan;
import mas.localSchedulingproxy.plan.ReceiveCompletedBatchPlan;
import mas.localSchedulingproxy.plan.RegisterLSAgentServicePlan;
import mas.localSchedulingproxy.plan.RegisterLSAgentToBlackboardPlan;
import mas.localSchedulingproxy.plan.SendBidPlan;
import mas.localSchedulingproxy.plan.SendJobToMachinePlan;
import mas.localSchedulingproxy.plan.SendWaitingTimePlan;
import mas.localSchedulingproxy.plan.StatsTracker;
import mas.localSchedulingproxy.plan.UpdateOperationDatabasePlan;
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

/**
 * @author Anand Prajapati
 * 
 */

public class AbstractbasicCapability extends Capability {

	private Logger log;

	private static final long serialVersionUID = 1L;

	public AbstractbasicCapability(){
		super(new BeliefBase(getBeliefs()), new PlanLibrary(getPlans()));
		log = LogManager.getLogger();
	}

	public static Set<Belief<?>> getBeliefs() {
		Set<Belief<?>> beliefs = new HashSet<Belief<?>>();

		Belief<AID> bboard = new TransientBelief<AID>(
				ID.LocalScheduler.BeliefBaseConst.blackboardAgent);

		Belief<AID> myMachine = new TransientBelief<AID>(
				ID.LocalScheduler.BeliefBaseConst.machine);

		Belief<AID> myMcMaintAgent = new TransientBelief<AID>(
				ID.LocalScheduler.BeliefBaseConst.maintAgent);

		Belief<AID> mygsAgent = new TransientBelief<AID>(
				ID.LocalScheduler.BeliefBaseConst.globalSchAgent);

		Belief<StatsTracker> dtrack = new TransientBelief<StatsTracker>(
				ID.LocalScheduler.BeliefBaseConst.dataTracker);

		Belief<Double> processingCost= 
				new TransientBelief<Double>(ID.LocalScheduler.BeliefBaseConst.ProcessingCost);

		Belief<String[]> supportedOperations = 
				new TransientBelief<String[]>(ID.LocalScheduler.BeliefBaseConst.supportedOperations);

				StatsTracker stats = new StatsTracker();
				dtrack.setValue(stats);

				Belief<ArrayList<Batch> > jobSet = new TransientBelief<ArrayList<Batch> >(
						ID.LocalScheduler.BeliefBaseConst.batchQueue);

				Belief<Double> regretThreshold = new TransientBelief<Double>(
						ID.LocalScheduler.BeliefBaseConst.regretThreshold);

				Belief<OperationDataBase> operationDB = new 
						TransientBelief<OperationDataBase>(ID.LocalScheduler.BeliefBaseConst.operationDatabase);

				Belief<Batch> doneBatchFromMachine = new TransientBelief<Batch>(
						ID.LocalScheduler.BeliefBaseConst.doneBatchFromMachine);
				
				Belief<Batch> currentBatch = new TransientBelief<Batch>(
						ID.LocalScheduler.BeliefBaseConst.currentBatch);
				
				doneBatchFromMachine.setValue(null);
				currentBatch.setValue(null);

				double threshVal = 0;
				regretThreshold.setValue(threshVal);

				ArrayList<Batch> jobList = new ArrayList<Batch>();
				jobSet.setValue(jobList);

				beliefs.add(bboard);
				beliefs.add(jobSet);
				beliefs.add(myMachine);
				beliefs.add(myMcMaintAgent);
				beliefs.add(mygsAgent);
				beliefs.add(dtrack);
				beliefs.add(regretThreshold);
				beliefs.add(processingCost);
				beliefs.add(supportedOperations);
				beliefs.add(operationDB);
				beliefs.add(doneBatchFromMachine);
				beliefs.add(currentBatch);

				return beliefs;
	}

	public static Set<Plan> getPlans() {
		Set<Plan> plans = new HashSet<Plan>();

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgfinishedBatch),
				ReceiveCompletedBatchPlan.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgaskBidForJobFromLSA),
				SendBidPlan.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgGetWaitingTime),
				SendWaitingTimePlan.class));

		plans.add(new SimplePlan(RegisterLSAgentToBlackboardGoal.class,
				RegisterLSAgentToBlackboardPlan.class));

		plans.add(new SimplePlan(RegisterLSAgentServiceGoal.class,
				RegisterLSAgentServicePlan.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgjobForLSA),
				EnqueueBatchPlan.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgaskJobFromLSA),
				SendJobToMachinePlan.class));

		plans.add(new SimplePlan(JobSchedulingGoal.class,BatchSchedulingPlan.class));

		plans.add(new SimplePlan(UpdateOperationDatabaseGoal.class, UpdateOperationDatabasePlan.class));

		return plans;
	}	

	@Override
	protected void setup() {
		myAgent.addGoal(new RegisterLSAgentServiceGoal());
		myAgent.addGoal(new RegisterLSAgentToBlackboardGoal());
		myAgent.addGoal(new JobSchedulingGoal());
		myAgent.addGoal(new UpdateOperationDatabaseGoal());

		/*	myAgent.addGoal(new SendBidGoal());
		myAgent.addGoal(new SendWaitingTimeGoal());
		myAgent.addGoal(new EnqueueJobGoal());
		myAgent.addGoal(new ReceiveCompletedJobGoal());*/
	}
}
