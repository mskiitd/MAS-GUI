package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;
import java.util.Date;

import mas.jobproxy.Batch;
import mas.localSchedulingproxy.algorithm.ScheduleSequence;
import mas.localSchedulingproxy.database.OperationDataBase;
import mas.localSchedulingproxy.database.OperationItemId;
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
 *  <p>
 * Receives batch from global scheduling agent for bid and then sends a bid 
 * for the received batch. For bid, please calculation refer to report. 
 * </p>
 */

public class SendBidPlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private ACLMessage msg;
	private Batch jobToBidFor;
	private ArrayList<Batch> batchQueue;
	private BeliefBase bfBase;
	private Logger log;
	private AID blackboard;
	private double bidNo;
	private String replyWith;
	private OperationDataBase operationdb;
	private String dueDateMethod=null;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();

		this.blackboard = (AID) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();

		msg = ((MessageGoal)pInstance.getGoal()).getMessage();
		replyWith = msg.getReplyWith();

		try {
			jobToBidFor = (Batch)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}

		this.operationdb = (OperationDataBase) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.operationDatabase).
				getValue();

		dueDateMethod = (String)bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.DueDateCalcMethod).
				getValue();
	}

	@Override
	public void action() {
		try{

			jobToBidFor = setBid(jobToBidFor);

			ZoneDataUpdate bidForJobUpdate = new ZoneDataUpdate.Builder(ID.LocalScheduler.ZoneData.bidForJob)
			.value(jobToBidFor).setReplyWith(replyWith).Build();

			AgentUtil.sendZoneDataUpdate(blackboard ,bidForJobUpdate, myAgent);

			//			log.info("Sending bid for job :" + jobToBidFor);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private Batch setBid(Batch batchToBidFor){

		batchQueue = (ArrayList<Batch>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue).
				getValue();

		log.info("bidding for batch : " + batchToBidFor.getBatchCount() + " operation : " +
				batchToBidFor.getFirstJob().getCurrentOperation());

		batchToBidFor=SetDueDates(batchToBidFor);

		OperationItemId id = new OperationItemId(batchToBidFor.getCurrentOperationType(), batchToBidFor.getCustomerId());

		if( ! operationdb.contains(id) ) {
			log.info("Operation " + batchToBidFor.getCurrentOperationType() +
					" unsupported on machine : " + myAgent.getLocalName());
			batchToBidFor.setBidByLSA(Double.MAX_VALUE);
			batchToBidFor.setLSABidder(myAgent.getAID());
			return batchToBidFor;
		} 

		// set processing time for current operation in the batch for each of the job
		batchToBidFor.setCurrentOperationProcessingTime(operationdb. getOperationInfo(id).getProcessingTime());

		ArrayList<Batch> tempQueue = new  ArrayList<Batch>();
		tempQueue.addAll(batchQueue);
		tempQueue.add(batchToBidFor);

		ScheduleSequence sch = new ScheduleSequence(tempQueue);
		ArrayList<Batch> tempqSolution = sch.getSolution();

		double PenaltyAfter = getPenaltyLocalDD(tempqSolution);
		//		log.info("PenaltyAfter="+getPenaltyLocalDD(tempqSolution));

		double PenaltyBefore = getPenaltyLocalDD(batchQueue);
		log.info(myAgent.getLocalName() + " job Q size= " + batchQueue.size());

		double incremental_penalty = PenaltyAfter - PenaltyBefore;
		log.info(myAgent.getLocalName() + " incremental penalty = " + incremental_penalty);

		double processingCost = (batchToBidFor.getCurrentOperationProcessingTime()/1000)*
				operationdb.getOperationInfo(id).getProcessingCost();

		log.info("" + id + " cost : " + operationdb.getOperationInfo(id).getProcessingCost());

		bidNo = PenaltyAfter - PenaltyBefore + processingCost;

		batchToBidFor.setBidByLSA(bidNo);
		batchToBidFor.setLSABidder(myAgent.getAID());

		return batchToBidFor;
	}

	public double getPenaltyLocalDD(ArrayList<Batch> sequence) {
		long finishTime = 0;
		long cumulativeProcessingTime = 0;//sum of processing times of jobs in Q standing ahead 
		//in milliseconds

		// initialize start times of all batches in the queue
		sequence = setStartTimes(sequence);

		double cost = 0.0;
		int sequenceSize = sequence.size();

		for (int i = 0; i < sequenceSize; i++) {

			finishTime = cumulativeProcessingTime + sequence.get(i).getCurrentOperationProcessingTime() +
					sequence.get(i).getCurrentOperationStartTime();

			cumulativeProcessingTime = cumulativeProcessingTime + sequence.get(i).getCurrentOperationProcessingTime();
			double tardiness = 0.0;

			//						log.info(myAgent.getLocalName()+ " cpt="+cumulativeProcessingTime +" L="+l+
			//			"ft="+new Date(finishTime)+" dd="+sequence.get(i).getDuedate()+" st="+sequence.get(i).getStartTime());

			if (finishTime > sequence.get(i).getCurrentOperationDueDate()) {
				tardiness = (finishTime - sequence.get(i).getCurrentOperationDueDate())/1000.0;
			}
			else {
				tardiness = 0.0;
			}
			log.info("tardiness="+tardiness+" penalty rate="+sequence.get(i).getPenaltyRate()+
					"finishTime="+new Date(finishTime)+"CurrentOperationDueDate="+
					new Date(sequence.get(i).getCurrentOperationDueDate())+
					"CurrentOperationStartTime="+new Date(sequence.get(i).getCurrentOperationStartTime())
			+"cumulativeProcessingTime="+cumulativeProcessingTime);

			cost += tardiness * sequence.get(i).getPenaltyRate() ;/*+ sequence.get(i).getCost();*/
		}
		log.info(myAgent.getLocalName() + " cost = " + cost + " with L = " + sequenceSize );
		return cost;
	}

	/**
	 * sets start time for all the batches in the queue
	 * @param sequence
	 * @return
	 */
	private ArrayList<Batch> setStartTimes(ArrayList<Batch> sequence) {
		long CumulativeWaitingTime = 0;

		for(int i = 0 ; i < sequence.size(); i++) {
			sequence.get(i).setCurrentOperationStartTime(CumulativeWaitingTime +
					System.currentTimeMillis());

			CumulativeWaitingTime = CumulativeWaitingTime +
					(long)sequence.get(i).getCurrentOperationProcessingTime();
		}
		return sequence;
	}

	private Batch SetDueDates(Batch batchForBidWinner) {

		long totalProcessingTime = batchForBidWinner.getTotalBatchProcessingTime();
		long totalAvailableTime = batchForBidWinner.getDueDateByCustomer().getTime() -
				batchForBidWinner.getStartTimeMillis();

		long slack = totalAvailableTime - totalProcessingTime; //in seconds
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

			if(slack_perOperation<0){
				slack_perOperation=0;
			}

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

}	
