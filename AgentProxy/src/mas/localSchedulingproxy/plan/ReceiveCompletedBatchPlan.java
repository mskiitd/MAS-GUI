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

public class ReceiveCompletedBatchPlan extends OneShotBehaviour implements PlanBody {

	/**
	 * Takes the complete job from the simulator
	 */

	private static final long serialVersionUID = 1L;
	private Batch batch;
	private Batch comingBatch;
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
			comingBatch = (Batch)((MessageGoal)pInstance.getGoal()).getMessage().getContentObject();

		} catch (UnreadableException e) {			
			e.printStackTrace();
		}
	}


	@Override
	public void action() {
		// since job is done update current job with null value
		bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.currentBatchOnMachine, null);

		comingBatch.IncrementOperationNumber();

		ZoneDataUpdate CompletedBatchUpdate = new ZoneDataUpdate.Builder(ID.LocalScheduler.ZoneData.finishedBatch)
		.value(comingBatch).setReplyWith(Integer.toString(comingBatch.getBatchNumber())).Build();

		AgentUtil.sendZoneDataUpdate(blackboard_AID, CompletedBatchUpdate, myAgent);
		if(LocalSchedulingAgent.mGUI != null) {
			log.info("removing from the gui ");
			LocalSchedulingAgent.mGUI.removeFromQueue(comingBatch);
		}

		bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.doneBatchFromMachine, comingBatch);
	}

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}
}
