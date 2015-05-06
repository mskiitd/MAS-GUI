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
import mas.util.BatchQueryObject;
import mas.util.ZoneDataUpdate;
import bdi4jade.belief.Belief;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * 
 * @author Anand Prajapati
 * <p>
 * Plan for responding to GSA queries.
 * </br>
 * GSA queries are of 3 types :
 * </br> i) Current status of batch
 * </br> ii) Change due date
 * </br> iii) Cancel an order
 * </br> Based on type of query, LSA responds.
 * If it's due date change or order-cancellation, then it stores it in the belief base and next time
 * when it receives completed batch from machine, then it triggers appropriate action for that batch.
 * 
 * </p>
 */
public class RespondToGSAQuery extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private int batchNo;
	private BeliefBase beleifBase;
	private AID machineAID=null;
	private AID blackboard_AID;
	private Logger log;
	private BatchQueryObject requestJobQuery;
	private MachineGUI gui;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance PI) {
		log = LogManager.getLogger();
		try {
			requestJobQuery= ( (BatchQueryObject)((MessageGoal)PI.getGoal()).
					getMessage().getContentObject());
			batchNo = ( (BatchQueryObject)((MessageGoal)PI.getGoal()).getMessage().getContentObject()).
					getCurrentBatch().getBatchNumber();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}

		log.info("Batch No " + batchNo + " is queried");
		beleifBase = PI.getBeliefBase();

		blackboard_AID = (AID)beleifBase.getBelief(ID.LocalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();

		machineAID = (AID)beleifBase.getBelief(ID.LocalScheduler.BeliefBaseConst.machine).
				getValue();

		gui = (MachineGUI) beleifBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.gui_machine).
				getValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		BatchQueryObject response = new BatchQueryObject.Builder().currentBatch(null).underProcess(null)
				.currentMachine(null).build();

		ArrayList<Batch> jobQ = (ArrayList<Batch>)beleifBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue).
				getValue();

		Object currBatchObj = beleifBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.currentBatchOnMachine);

		Batch currentBatch = null;
		if(currBatchObj != null){
			currentBatch = (Batch)(((Belief<Batch>)currBatchObj).getValue());
		}

		for(int i = 0 ; i < jobQ.size(); i++) {
			if(batchNo == jobQ.get(i).getBatchNumber() ){
				response = new BatchQueryObject.Builder().currentBatch(jobQ.get(i))
						.requestType(requestJobQuery.getType())//notes down for what req type this reply was
						.underProcess(false)
						.currentMachine(machineAID).build();


				if(requestJobQuery.getType().equals(ID.GlobalScheduler.requestType.changeDueDate)
						|| requestJobQuery.getType().equals(ID.GlobalScheduler.requestType.cancelBatch)) {

					Batch removedBatch = jobQ.remove(i);
					beleifBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue, jobQ);

					ArrayList<Batch> BatchToTakeAction = (ArrayList<Batch>)
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

		if(currentBatch!=null && currentBatch.getBatchNumber() == batchNo){
			
			if(requestJobQuery.getType().equals(ID.GlobalScheduler.requestType.changeDueDate)){
				currentBatch.IncrementOperationNumber();
			}
			//if batch is loaded on machine, current operation will be completed and then
			//only new due date is alloted. So, wheen new due date is generated, u should 'assume'
			//that current operation is completed and then only send that copy to customer for due date change
			
			response=new BatchQueryObject.Builder().currentBatch(currentBatch).currentMachine(machineAID)
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

		if(response.getCurrentBatch()==null){
			log.info("did not find batch no. "+batchNo+" on "+myAgent.getLocalName());
		}
		ZoneDataUpdate queryUpdate=new ZoneDataUpdate.
				Builder(ID.LocalScheduler.ZoneData.QueryResponse).
				value(response).Build();

		AgentUtil.sendZoneDataUpdate(blackboard_AID, queryUpdate, myAgent);

	}

}
