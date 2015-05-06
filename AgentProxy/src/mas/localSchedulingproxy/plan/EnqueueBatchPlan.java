package mas.localSchedulingproxy.plan;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.util.ArrayList;
import mas.jobproxy.Batch;
import mas.localSchedulingproxy.database.OperationDataBase;
import mas.localSchedulingproxy.database.OperationItemId;
import mas.machineproxy.gui.MachineGUI;
import mas.util.ID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * @author Anand Prajapati
 *
 * This plan receives incoming batches to the machine and adds them to the queue of batch
 */

public class EnqueueBatchPlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private ArrayList<Batch> jobQueue;
	private BeliefBase bfBase;
	private Logger log;
	private Batch comingBatch;
	private OperationDataBase operationdb;
	private MachineGUI gui;
//	private String dueDateMethod=null;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(PlanInstance pInstance) {

		bfBase = pInstance.getBeliefBase();
		ACLMessage msg = ((MessageGoal)pInstance.getGoal()).
				getMessage();

		try {
			comingBatch = (Batch) msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}

//		dueDateMethod = (String)bfBase.
//				getBelief(ID.LocalScheduler.BeliefBaseConst.DueDateCalcMethod).
//				getValue();
		
		jobQueue = (ArrayList<Batch>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue).
				getValue();

		this.operationdb = (OperationDataBase) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.operationDatabase).
				getValue();
		
		gui = (MachineGUI) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.gui_machine).
				getValue();

		log = LogManager.getLogger();
//		log.info("winner : " +  comingBatch.getWinnerLSA());
	}

	@Override
	public void action() {
		if(comingBatch.getWinnerLSA().equals(myAgent.getAID())) {
			
//			comingBatch=SetDueDates(comingBatch);
			log.info("Adding the batch" + comingBatch.getBatchId() +  " to queue of agent, " +
					myAgent.getLocalName());

			OperationItemId id = new OperationItemId(comingBatch.getCurrentOperationType(), comingBatch.getCustomerId());
			
			log.info("operation " + id + " processing time : " + operationdb.getOperationInfo(id).getProcessingTime());
			comingBatch.setCurrentOperationProcessingTime(operationdb.getOperationInfo(id).getProcessingTime());
		
			jobQueue.add(comingBatch);
			
			//update the belief base
			
//			log.info(jobQueue);
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue, jobQueue);	

			if(gui != null) {
				gui.addBatchToQueue(comingBatch);
			}
		}
	}
	
	/*private Batch SetDueDates(Batch batchForBidWinner) {

		long totalProcessingTime = batchForBidWinner.getTotalProcessingTime();
		long totalAvailableTime = batchForBidWinner.getDueDateByCustomer().getTime() -
				batchForBidWinner.getStartTimeMillis();

		long slack = totalAvailableTime - totalProcessingTime;
		int NoOfOps = batchForBidWinner.getNumOperations();
		long currTime = batchForBidWinner.getStartTimeMillis();

		//		log.info("due date " + new Date(jobForBidWinner.getJobDuedatebyCust().getTime())+
		//				" start time " + new Date(jobForBidWinner.getStartTimeByCust().getTime()));
//		log.info("batch : " + batchForBidWinner.getJobsInBatch()   );

		if(dueDateMethod.equals(ID.LocalScheduler.OtherConst.LocalDueDate)) {

			long slack_perOperation = (long)((double)slack)/(NoOfOps);

//			for(int i = 0 ; i < NoOfOps; i++) {
				
				batchForBidWinner.setCurrentOperationStartTime(currTime);
				currTime += batchForBidWinner.getCurrentOperationProcessingTime() + slack_perOperation;
				batchForBidWinner.setCurrentOperationDueDate(currTime);
				log.info("curr op due date = "+new
						Date(batchForBidWinner.getCurrentOperationDueDate()));
//				batchForBidWinner.IncrementOperationNumber();
				batchForBidWinner.setSlack(slack_perOperation);
				
				
//			}
		}
		else if(dueDateMethod.equals(ID.LocalScheduler.OtherConst.GlobalDueDate)) {
//			for(int i = 0 ; i  < NoOfOps; i++) {
				batchForBidWinner.setCurrentOperationStartTime(currTime);
				currTime += batchForBidWinner.getCurrentOperationProcessingTime();
				// shift whole slack to the last operation
				if(batchForBidWinner.getCurrentOperationNumber() == NoOfOps-1) {
					currTime = currTime + slack;
				}
				batchForBidWinner.setCurrentOperationDueDate(currTime);
//				batchForBidWinner.IncrementOperationNumber();
//			}
		}
		return batchForBidWinner;
	}
*/
}
