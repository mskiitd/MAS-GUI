package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mas.jobproxy.Batch;
import mas.machineproxy.gui.MachineGUI;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.JobQueryObject;
import mas.util.ZoneDataUpdate;
import bdi4jade.belief.Belief;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class RespondToGSAQuery extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private int batchNo;
	private BeliefBase beleifBase;
	private AID machineAID=null;
	private AID blackboard_AID;
	private Logger log=LogManager.getLogger();
	private JobQueryObject requestJobQuery;
	private MachineGUI gui;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance PI) {
		try {
			requestJobQuery= ( (JobQueryObject)((MessageGoal)PI.getGoal()).
					getMessage().getContentObject());
			batchNo = ( (JobQueryObject)((MessageGoal)PI.getGoal()).getMessage().getContentObject()).
					getCurrentBatch().getBatchNumber();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}

		log.info("Batch No " + batchNo + " is queried");
		beleifBase = PI.getBeliefBase();
		blackboard_AID = (AID)beleifBase.getBelief(ID.LocalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();
		machineAID=(AID)beleifBase.getBelief(ID.LocalScheduler.BeliefBaseConst.machine).
				getValue();

		gui = (MachineGUI) beleifBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.gui_machine).
				getValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		JobQueryObject response = new JobQueryObject.Builder().currentJob(null)
				.currentMachine(null).build();

		ArrayList<Batch> jobQ = (ArrayList<Batch>)beleifBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue).
				getValue();
		
		Object currBatchObj = beleifBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.currentBatchOnMachine);
		
		Batch currentJob = null;
		if(currBatchObj != null){
			currentJob = (Batch)(((Belief<Batch>)currBatchObj).getValue());
		}

		for(int i = 0 ; i < jobQ.size(); i++) {
			if(batchNo == jobQ.get(i).getBatchNumber() ){
				response = new JobQueryObject.Builder().currentJob(jobQ.get(i))
						.requestType(requestJobQuery.getType())//notes down for what req type this reply was
						.underProcess(false)
						.currentMachine(machineAID).build();


				if(requestJobQuery.getType().equals(ID.GlobalScheduler.requestType.changeDueDate)
						|| requestJobQuery.getType().equals(ID.GlobalScheduler.requestType.cancelBatch)) {

					Batch removedBatch = jobQ.remove(i);
					beleifBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue, jobQ);

					ArrayList<Batch> BatchToTakeAction=(ArrayList<Batch>)
							beleifBase.getBelief(ID.LocalScheduler.BeliefBaseConst.actionOnCompletedBatch).getValue();
					BatchToTakeAction.add(response.getCurrentBatch());
					beleifBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.actionOnCompletedBatch
							, BatchToTakeAction);

					if(gui != null) {
						gui.removeFromQueue(removedBatch);
					}
				}

			}
		}

		if(currentJob!=null && currentJob.getBatchNumber() ==batchNo){
			response=new JobQueryObject.Builder().currentJob(currentJob).currentMachine(machineAID)
					.requestType(requestJobQuery.getType())
					.underProcess(true)
					.build();

			if(requestJobQuery.getType().equals(ID.GlobalScheduler.requestType.changeDueDate)
					|| requestJobQuery.getType().equals(ID.GlobalScheduler.requestType.cancelBatch)){

				ArrayList<Batch> BatchToTakeAction=(ArrayList<Batch>)
						beleifBase.getBelief(ID.LocalScheduler.BeliefBaseConst.actionOnCompletedBatch).getValue();
				BatchToTakeAction.add(response.getCurrentBatch());
				beleifBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.actionOnCompletedBatch
						, BatchToTakeAction);
			}

		}

		ZoneDataUpdate queryUpdate=new ZoneDataUpdate.
				Builder(ID.LocalScheduler.ZoneData.QueryResponse).
				value(response).Build();

		AgentUtil.sendZoneDataUpdate(blackboard_AID, queryUpdate, myAgent);

	}

}
