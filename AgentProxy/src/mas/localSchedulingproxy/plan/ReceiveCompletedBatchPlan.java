package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

import mas.jobproxy.Batch;
import mas.localSchedulingproxy.algorithm.StatsTracker;
import mas.machineproxy.gui.MachineGUI;
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

/**
 * @author Anand Prajapati
 * <p>
 * Plan to receive the complete batch from the machine.
 * It receives this value from the blackboard. Based on whether the received batch is complete or marked canceled or 
 * marked for change in due date, appropriate action is taken. It batch isn't complete then it's sent to GSA again
 * for bidding.
 * </p>
 */

public class ReceiveCompletedBatchPlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private Batch batch;
	private Batch comingBatch;
	private ArrayList<Batch> jobQueue;
	private BeliefBase bfBase;
	private StatsTracker sTracker;
	private Logger log;
	private AID blackboard_AID;
	private Batch currentBatch;
	private MachineGUI gui;
	private boolean isJobCancelled=false;

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

		gui = (MachineGUI) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.gui_machine).
				getValue();
	}


	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		// since job is done update current job with null value
		bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.currentBatchOnMachine, null);

		ArrayList<Batch> BatchToTakeAction = (ArrayList<Batch>)
				bfBase.getBelief(ID.LocalScheduler.BeliefBaseConst.actionOnCompletedBatch).getValue();

		for(int i = 0 ; i < BatchToTakeAction.size(); i++) {
			if(BatchToTakeAction.get(i).getBatchNumber() == comingBatch.getBatchNumber()){
				BatchToTakeAction.remove(i);
				bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.actionOnCompletedBatch
						, BatchToTakeAction);
				isJobCancelled = true;
				log.info("cancelled batch No. "+comingBatch.getBatchNumber());

				if(gui != null) {
					gui.removeFromQueue(comingBatch);
				}
			}
		}

		if(!isJobCancelled) {

			comingBatch.IncrementOperationNumber();

			ZoneDataUpdate CompletedBatchUpdate = new ZoneDataUpdate.Builder(ID.LocalScheduler.ZoneData.finishedBatch)
			.value(comingBatch).setReplyWith(Integer.toString(comingBatch.getBatchNumber())).Build();

			AgentUtil.sendZoneDataUpdate(blackboard_AID, CompletedBatchUpdate, myAgent);
			if(gui != null) {
				gui.removeFromQueue(comingBatch);
			} else {
				log.info("Gui of machine is null");
			}

			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.doneBatchFromMachine, comingBatch);
		}

	}

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}
}
