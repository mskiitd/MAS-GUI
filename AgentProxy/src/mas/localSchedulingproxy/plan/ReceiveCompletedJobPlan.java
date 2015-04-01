package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class ReceiveCompletedJobPlan extends OneShotBehaviour implements PlanBody {

	/**
	 * Takes the complete job from the simulator
	 */

	private static final long serialVersionUID = 1L;
	private Batch batch;
	private job comingJob;
	private ArrayList<Batch> jobQueue;
	private BeliefBase bfBase;
	private StatsTracker sTracker;
	private Logger log;
	private AID blackboard_AID;
	private Batch currentBatch;

	@Override
	public void init(PlanInstance pInstance) {

		log = LogManager.getLogger();
		blackboard_AID=(AID)pInstance.getBeliefBase().
				getBelief(ID.LocalScheduler.BeliefBaseConst.blackboardAgent).getValue();
		bfBase = pInstance.getBeliefBase();

		try {
			comingJob = (job)((MessageGoal)pInstance.getGoal()).getMessage().getContentObject();

		} catch (UnreadableException e) {			
			e.printStackTrace();
		}
	}


	@Override
	public void action() {
		// since job is done update current job with null value
		bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.currentJobOnMachine, null);

		comingJob.IncrementOperationNumber();
		
		currentBatch = (Batch) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.currentBatch).
				getValue();

		if(currentBatch.getBatchId().equals(comingJob.getJobID()) ) {
			currentBatch.addJobToBatch(comingJob);
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.doneBatchFromMachine,
					currentBatch);
		} else {
			Batch incoming = new Batch(comingJob.getJobID());
			incoming.addJobToBatch(comingJob);
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.doneBatchFromMachine, incoming);
			
			ZoneDataUpdate CompletedJobUpdate = new ZoneDataUpdate.Builder(ID.LocalScheduler.ZoneData.finishedJob)
			.value(currentBatch).setReplyWith(Integer.toString(comingJob.get)).Build();

			AgentUtil.sendZoneDataUpdate(blackboard_AID, CompletedJobUpdate, myAgent);

			if(LocalSchedulingAgent.mGUI != null) {
				LocalSchedulingAgent.mGUI.removeFromQueue(batch);
			}
		}
	}

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}
}
