package mas.localSchedulingproxy.capability;

import jade.core.AID;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.localSchedulingproxy.algorithm.StatsTracker;
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
 * Capability of local scheduling agent. Capability consists of beliefs and plans associated with goals of the agent.
 * Each capability has its own belief base.
 */

public class AbstractbasicCapability extends Capability {

	private Logger log;

	private static final long serialVersionUID = 1L;

	public AbstractbasicCapability() {
		super(new BeliefBase(getBeliefs()), new PlanLibrary(getPlans()));
		log = LogManager.getLogger();
	}

	/**
	 * @return Set of beliefs of capability
	 * </br><b> Add beliefs for </b>
	 * </br> AID of blackboard agent
	 * </br> AID of machine
	 * </br> AID of maintenance agent
	 * </br> AID of global scheduling agent
	 * </br> statistics about machine
	 * </br> queue of batches for the machine
	 * </br> regret threshold value
	 * </br> supported operations database of the machine
	 * </br> completed batch from machine
	 * </br> current batch on machine
	 * </br> current job on machine
	 * </br> actions to be taken by LSA when it receives a completed batch form machine
	 * </br> Queue of preventive maintenance schedules for machine
	 * </br> UI of machine
	 * </br> current maintenance job being done on machine
	 * </br> current due date calculation method i.e. Global/ Local
	 * </br> scheduling inter-arrival time
	 * 
	 */
	public static Set<Belief<?>> getBeliefs() {
		Set<Belief<?>> beliefs = new HashSet<Belief<?>>();

		// belief for AID of blackboard agent
		Belief<AID> bboard = new TransientBelief<AID>(
				ID.LocalScheduler.BeliefBaseConst.blackboardAgent);

		// belief for AID of machine
		Belief<AID> myMachine = new TransientBelief<AID>(
				ID.LocalScheduler.BeliefBaseConst.machine);

		// belief for AID of maintenance agent
		Belief<AID> myMcMaintAgent = new TransientBelief<AID>(
				ID.LocalScheduler.BeliefBaseConst.maintAgent);

		// belief for AID of global scheduling agent
		Belief<AID> mygsAgent = new TransientBelief<AID>(
				ID.LocalScheduler.BeliefBaseConst.globalSchAgent);

		// belief for statistics about machine
		Belief<StatsTracker> dtrack = new TransientBelief<StatsTracker>(
				ID.LocalScheduler.BeliefBaseConst.dataTracker);

		// belief for queue of batches for the machine
		Belief<ArrayList<Batch> > jobSet = new TransientBelief<ArrayList<Batch> >(
				ID.LocalScheduler.BeliefBaseConst.batchQueue);

		// belief for regret threshold value
		Belief<Double> regretThreshold = new TransientBelief<Double>(
				ID.LocalScheduler.BeliefBaseConst.regretThreshold);

		// belief for supported operations database of the machine
		Belief<OperationDataBase> operationDB = new TransientBelief<OperationDataBase>(
				ID.LocalScheduler.BeliefBaseConst.operationDatabase);

		// belief for completed batch from machine
		Belief<Batch> doneBatchFromMachine = new TransientBelief<Batch>(
				ID.LocalScheduler.BeliefBaseConst.doneBatchFromMachine);

		// belief for current batch on machine
		Belief<Batch> currentBatch = new TransientBelief<Batch>(
				ID.LocalScheduler.BeliefBaseConst.currentBatchOnMachine);

		// belief for current job on machine
		Belief<job> currentJob = new TransientBelief<job>(
				ID.LocalScheduler.BeliefBaseConst.currentJobOnMachine);

		// belief for actions to be taken by LSA when it receives a completed batch form machine
		Belief<ArrayList<Batch>> actionOnCompletedBatch = new TransientBelief<ArrayList<Batch>>(
				ID.LocalScheduler.BeliefBaseConst.actionOnCompletedBatch);

		// belief for UI of machine
		Belief<MachineGUI> gui = new TransientBelief<MachineGUI>(ID.LocalScheduler.BeliefBaseConst.gui_machine);

		// belief for Queue of preventive maintenance schedules for machine
		Belief<ArrayList<PMaintenance>> maintJobs = new TransientBelief<ArrayList<PMaintenance>>(
				ID.LocalScheduler.BeliefBaseConst.preventiveJobsQueue);

		// belief for current maintenance job being done on machine
		Belief<PMaintenance> currentMaintJob = new TransientBelief<PMaintenance>(
				ID.LocalScheduler.BeliefBaseConst.currentMaintJob);

		// belief for current due date calculation method i.e. Global/ Local
		Belief<String> DueDateCalcMethod = new TransientBelief<String>(ID.LocalScheduler.
				BeliefBaseConst.DueDateCalcMethod);
		
		// belief for scheduling inter-arrival time
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

	/**
	 * <b> Add plans for </b> <i>
	 * </br> receiving completed batch from machine
	 * </br> sending bid for a batch
	 * </br> calculate and send expected due date of an incoming batch
	 * </br> registering agent to blackboard
	 * </br> register agent to DF (JADE)
	 * </br> enqueue a new batch into the queue of batch
	 * </br> send a batch from queue to machine
	 * </br> schedule the sequence of batches
	 * </br> load configuration file for the agent
	 * </br> respond to GSA queries
	 * </br> start maintenance on machine
	 * </br> receive maintenance schedule for machine
	 * </br> finish maintenance on machine
	 * </br> negotiate with maintenance agent when maintenance schedule of machine is delayed
	 * </br> read current job being processed on machine
	 * </i>
	 * 
	 * @return Set of plans associated with goals 
	 */
	public static Set<Plan> getPlans() {
		Set<Plan> plans = new HashSet<Plan>();

		// plan for receiving completed batch from machine
		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgfinishedBatch),
				ReceiveCompletedBatchPlan.class));

		// plan for sending bid for a batch
		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgaskBidForJobFromLSA),
				SendBidPlan.class));

		// plan to calculate and send expected due date of an incoming batch
		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgGetWaitingTime),
				SendExpectedDueDatePlan.class));

		// plan for registering agent to blackboard
		plans.add(new SimplePlan(RegisterLSAgentToBlackboardGoal.class,
				RegisterLSAgentToBlackboardPlan.class));

		// plan to register agent to DF (JADE)
		plans.add(new SimplePlan(RegisterLSAgentServiceGoal.class,
				RegisterLSAgentServicePlan.class));

		// plan to enqueue a new batch into the queue of batch
		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgjobForLSA),
				EnqueueBatchPlan.class));

		// plan to send a batch from queue to machine
		plans.add(new SimplePlan(SendBatchToMachineGoal.class, SendBatchToMachinePlan.class));

		// plan to schedule the sequence of batches
		plans.add(new SimplePlan(BatchSchedulingGoal.class,BatchSchedulingPlan.class));

		// plan to load configuration file for the agent
		plans.add(new SimplePlan(LoadConfigLSAGoal.class, LoadConfigLSAPlan.class));

		// plan to respond to GSA queries
		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgGSAQuery)
				,RespondToGSAQuery.class));

		// plan to start maintenance on machine
		plans.add(new SimplePlan(StartMaintenanceGoal.class, StartMaintenancePlan.class));

		// plan to receive maintenance schedule for machine
		plans.add(new SimplePlan(ReceiveMaintenanceJobGoal.class,ReceiveMaintenanceJobPlan.class));

		// plan to finish maintenance on machine
		plans.add(new SimplePlan(FinishMaintenanceGoal.class,FinishMaintenancePlan.class));

		// plan to negotiate with maintenance agent when maintenance schedule of machine is delayed
		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgmachineStatus),
				ReceiveDelayedMaintenanceResponsePlan.class));

		// plan to read current job being processed on machine
		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgCurrentJobOnMachine),
				GetCurrentJobOnMachinePlan.class));

		return plans;
	}	

	@Override
	protected void setup() {
		// add goals to the agent
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
