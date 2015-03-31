package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;
import java.util.Random;

import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.localSchedulingproxy.algorithm.ScheduleSequence;
import mas.localSchedulingproxy.database.OperationDataBase;
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
 * 
 * receives job from global scheduling agent for bid and then sends a bid 
 * for the received job 
 */

public class SendBidPlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private ACLMessage msg;
	private Batch jobToBidFor;
	private ArrayList<Batch> jobQueue;
	private BeliefBase bfBase;
	private Logger log;
	private AID blackboard;
	private double bidNo;
	private Random rand;
	private String replyWith;
	private OperationDataBase operationdb;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();

		//		r=new Random();

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
	}

	@Override
	public void action() {
		try{
			setBid(jobToBidFor);

			ZoneDataUpdate bidForJobUpdate = new ZoneDataUpdate.Builder(ID.LocalScheduler.ZoneData.bidForJob)
			.value(jobToBidFor).setReplyWith(replyWith).Build();

			AgentUtil.sendZoneDataUpdate(blackboard ,bidForJobUpdate, myAgent);

			//			log.info("Sending bid for job :" + jobToBidFor);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void setBid(Batch j){
		//		Random r = new Random();	
		//		j.setBidByLSA(r.nextDouble());

		jobQueue = (ArrayList<Batch>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue).
				getValue();

		if( ! operationdb.contains(j.getCurrentOperation().getJobOperationType()) ) {
			log.info("Operation" + j.getCurrentOperation().getJobOperationType() +
					"unsupported on machine : " + myAgent.getLocalName());
			j.setBidByLSA(-1);
			return;
		} 

		j.setCurrentOperationProcessingTime(operationdb.
				getOperationInfo(j.getCurrentOperation().getJobOperationType()).
				getProcessingTime() );

		ArrayList<Batch> tempQueue = new  ArrayList<Batch>();
		tempQueue.addAll(jobQueue);
		tempQueue.add(j);

		ScheduleSequence sch = new ScheduleSequence(tempQueue);
		ArrayList<Batch> tempqSolution = sch.getSolution();

		double PenaltyAfter = getPenaltyLocalDD(tempqSolution);
		//		log.info("PenaltyAfter="+getPenaltyLocalDD(tempqSolution));

		double PenaltyBefore = getPenaltyLocalDD(jobQueue);
		log.info(myAgent.getLocalName() + " job Q size= " + jobQueue.size());

		//		log.info("PenaltyBefore="+getPenaltyLocalDD(jobQueue));
		double incremental_penalty=PenaltyAfter - PenaltyBefore;
		log.info(myAgent.getLocalName() + " incremental penalty = " + incremental_penalty);

		bidNo = /*r.nextInt(10)+*/PenaltyAfter-PenaltyBefore;
		j.setBidByLSA(bidNo);
		j.setLSABidder(myAgent.getAID());
	}

	public double getPenaltyLocalDD(ArrayList<Batch> sequence) {
		long finishTime = 0;
		long cumulativeProcessingTime = 0;//sum of processing times of jobs in Q standing ahead 
		//in milliseconds

		sequence = setStartTimes(sequence);

		double cost = 0.0;
		int l = sequence.size();

		for (int i = 0; i < l; i++) {

			finishTime = cumulativeProcessingTime + sequence.get(i).getCurrentOperationProcessTime()*1000 +
					sequence.get(i).getCurrentOperationStartTime();
			//getProcessingTime gives in time in seconds

			//			log.info("difference="+(finishTime-sequence.get(i).getStartTime().getTime()));
			cumulativeProcessingTime = cumulativeProcessingTime + (long)sequence.get(i).getCurrentOperationProcessTime()*1000;

			double tardiness = 0.0;

			//			log.info(myAgent.getLocalName()+ " cpt="+cumulativeProcessingTime +" L="+l+"ft="+new Date(finishTime)+" dd="+sequence.get(i).getDuedate()+" st="+sequence.get(i).getStartTime());

			if (finishTime > sequence.get(i).getCurrentOperationDueDate()) {
				tardiness = (finishTime - sequence.get(i).getCurrentOperationDueDate())/1000.0;
				//				log.info(myAgent.getLocalName()+ " tardiness="+tardiness+" L="+l+"ft="+new Date(finishTime)+" dd="+sequence.get(i).getDuedate()+" st="+sequence.get(i).getStartTime());
			}
			else{
				/*	log.info("slack: "+new Date(finishTime-sequence.get(i).getDuedate().getTime())+
						" DueDate: "+sequence.get(i).getDuedate()+
						" cumulativeProcessingTime="+cumulativeProcessingTime+
						" start date: "+sequence.get(i).getStartTime().getTime());*/
				tardiness = 0.0;
			}

			//			log.info("tardiness="+tardiness+" penalty rate="+sequence.get(i).getPenaltyRate());
			cost += tardiness * sequence.get(i).getPenaltyRate() ;/*+ sequence.get(i).getCost();*/

		}

		log.info(myAgent.getLocalName()+" cost="+cost+" with L="+l);

		return cost;
	}

	private ArrayList<Batch> setStartTimes(ArrayList<Batch> sequence) {
		long CumulativeWaitingTime = 0;
		for(int i=0 ; i < sequence.size(); i++) {
			sequence.get(i).setCurrentOperationStartTime(CumulativeWaitingTime + System.currentTimeMillis());
			CumulativeWaitingTime=CumulativeWaitingTime + (long)sequence.get(i).getCurrentOperationProcessTime()*1000;
		}
		return sequence;

	}
}	
