package mas.localSchedulingproxy.capability;

import jade.core.AID;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.localSchedulingproxy.database.OperationDataBase;
import mas.localSchedulingproxy.goal.BatchSchedulingGoal;
import mas.localSchedulingproxy.goal.FinishMaintenanceGoal;
import mas.localSchedulingproxy.goal.ReceiveMaintenanceJobGoal;
import mas.localSchedulingproxy.goal.RegisterLSAgentServiceGoal;
import mas.localSchedulingproxy.goal.RegisterLSAgentToBlackboardGoal;
import mas.localSchedulingproxy.goal.SendBatchToMachineGoal;
import mas.localSchedulingproxy.goal.StartMaintenanceGoal;
import mas.localSchedulingproxy.goal.LoadConfigLSAGoal;
import mas.localSchedulingproxy.plan.BatchSchedulingPlan;
import mas.localSchedulingproxy.plan.EnqueueBatchPlan;
import mas.localSchedulingproxy.plan.FinishMaintenancePlan;
import mas.localSchedulingproxy.plan.GetCurrentJobOnMachinePlan;
import mas.localSchedulingproxy.plan.LoadConfigLSAPlan;
import mas.localSchedulingproxy.plan.ReceiveCompletedBatchPlan;
import mas.localSchedulingproxy.plan.ReceiveDelayedMaintenanceResponsePlan;
import mas.localSchedulingproxy.plan.ReceiveMaintenanceJobPlan;
import mas.localSchedulingproxy.plan.RegisterLSAgentServicePlan;
import mas.localSchedulingproxy.plan.RegisterLSAgentToBlackboardPlan;
import mas.localSchedulingproxy.plan.RespondToGSAQuery;
import mas.localSchedulingproxy.plan.SendBidPlan;
import mas.localSchedulingproxy.plan.SendBatchToMachinePlan;
import mas.localSchedulingproxy.plan.SendExpectedDueDatePlan;
import mas.localSchedulingproxy.plan.StartMaintenancePlan;
import mas.localSchedulingproxy.plan.StatsTracker;
import mas.machineproxy.gui.MachineGUI;
import mas.maintenanceproxy.classes.PMaintenance;
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

	public AbstractbasicCapability() {
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

		Belief<Double> processingCost = new TransientBelief<Double>(
				ID.LocalScheduler.BeliefBaseConst.ProcessingCost);

		Belief<String[]> supportedOperations = new TransientBelief<String[]>(
				ID.LocalScheduler.BeliefBaseConst.supportedOperations);

		Belief<ArrayList<Batch> > jobSet = new TransientBelief<ArrayList<Batch> >(
				ID.LocalScheduler.BeliefBaseConst.batchQueue);

		Belief<Double> regretThreshold = new TransientBelief<Double>(
				ID.LocalScheduler.BeliefBaseConst.regretThreshold);

		Belief<OperationDataBase> operationDB = new TransientBelief<OperationDataBase>(
				ID.LocalScheduler.BeliefBaseConst.operationDatabase);

		Belief<Batch> doneBatchFromMachine = new TransientBelief<Batch>(
				ID.LocalScheduler.BeliefBaseConst.doneBatchFromMachine);

		Belief<Batch> currentBatch = new TransientBelief<Batch>(
				ID.LocalScheduler.BeliefBaseConst.currentBatchOnMachine);

		Belief<job> currentJob = new TransientBelief<job>(
				ID.LocalScheduler.BeliefBaseConst.currentJobOnMachine);

		Belief<ArrayList<Batch>> actionOnCompletedBatch = new TransientBelief<ArrayList<Batch>>(
				ID.LocalScheduler.BeliefBaseConst.actionOnCompletedBatch);

		Belief<MachineGUI> gui = new TransientBelief<MachineGUI>(ID.LocalScheduler.BeliefBaseConst.gui_machine);

		Belief<ArrayList<PMaintenance>> maintJobs = new TransientBelief<ArrayList<PMaintenance>>(
				ID.LocalScheduler.BeliefBaseConst.preventiveJobsQueue);

		Belief<PMaintenance> currentMaintJob = new TransientBelief<PMaintenance>(
				ID.LocalScheduler.BeliefBaseConst.currentMaintJob);

		Belief<String> DueDateCalcMethod = new TransientBelief<String>(ID.LocalScheduler.
				BeliefBaseConst.DueDateCalcMethod);
		
		Belief<Double> schedulingPeriod = new TransientBelief<Double>(
				ID.LocalScheduler.BeliefBaseConst.schedulingInterval);
		
		schedulingPeriod.setValue(null);
		currentJob.setValue(null);
		gui.setValue(null);
		dtrack.setValue(new StatsTracker());
		doneBatchFromMachine.setValue(null);
		currentBatch.setValue(null);
		actionOnCompletedBatch.setValue(new ArrayList<Batch>());
		maintJobs.setValue(new ArrayList<PMaintenance>());
		currentMaintJob.setValue(null);
		jobSet.setValue(new ArrayList<Batch>());
		operationDB.setValue(new OperationDataBase());
		DueDateCalcMethod.setValue(ID.LocalScheduler.OtherConst.LocalDueDate);
		regretThreshold.setValue(null);

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
		beliefs.add(actionOnCompletedBatch);
		beliefs.add(gui);
		beliefs.add(maintJobs);
		beliefs.add(currentMaintJob);
		beliefs.add(currentJob);
		beliefs.add(DueDateCalcMethod);
		beliefs.add(schedulingPeriod);

		return beliefs;
	}

	public static Set<Plan> getPlans() {
		Set<Plan> plans = new HashSet<Plan>();

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgfinishedBatch),
				ReceiveCompletedBatchPlan.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgaskBidForJobFromLSA),
				SendBidPlan.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgGetWaitingTime),
				SendExpectedDueDatePlan.class));

		plans.add(new SimplePlan(RegisterLSAgentToBlackboardGoal.class,
				RegisterLSAgentToBlackboardPlan.class));

		plans.add(new SimplePlan(RegisterLSAgentServiceGoal.class,
				RegisterLSAgentServicePlan.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgjobForLSA),
				EnqueueBatchPlan.class));

		plans.add(new SimplePlan(SendBatchToMachineGoal.class, SendBatchToMachinePlan.class));

		plans.add(new SimplePlan(BatchSchedulingGoal.class,BatchSchedulingPlan.class));

		plans.add(new SimplePlan(LoadConfigLSAGoal.class, LoadConfigLSAPlan.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgGSAQuery)
				,RespondToGSAQuery.class));

		plans.add(new SimplePlan(StartMaintenanceGoal.class, StartMaintenancePlan.class));

		plans.add(new SimplePlan(ReceiveMaintenanceJobGoal.class,ReceiveMaintenanceJobPlan.class));

		plans.add(new SimplePlan(FinishMaintenanceGoal.class,FinishMaintenancePlan.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgmachineStatus),
				ReceiveDelayedMaintenanceResponsePlan.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgCurrentJobOnMachine),
				GetCurrentJobOnMachinePlan.class));

		return plans;
	}	

	@Override
	protected void setup() {
		myAgent.addGoal(new RegisterLSAgentServiceGoal());
		myAgent.addGoal(new RegisterLSAgentToBlackboardGoal());
		myAgent.addGoal(new BatchSchedulingGoal());
		myAgent.addGoal(new LoadConfigLSAGoal());
		myAgent.addGoal(new ReceiveMaintenanceJobGoal());
		myAgent.addGoal(new SendBatchToMachineGoal());
		/*	myAgent.addGoal(new SendBidGoal());
		myAgent.addGoal(new SendWaitingTimeGoal());
		myAgent.addGoal(new EnqueueJobGoal());
		myAgent.addGoal(new ReceiveCompletedJobGoal());*/
	}
}
