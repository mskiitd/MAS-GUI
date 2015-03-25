package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import mas.job.job;
import mas.localSchedulingproxy.algorithm.ScheduleSequence;
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

public class SendBidPlan extends OneShotBehaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private ACLMessage msg;
	private job jobToBidFor;
	private ArrayList<job> jobQueue;
	private BeliefBase bfBase;
	private Logger log;
	private AID blackboard;
	private double bidNo;
	private Random r;
	private String replyWith;

	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();
		
//		r=new Random();
		
		this.blackboard = (AID) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.blackboardAgentAID).
				getValue();

		msg = ((MessageGoal)pInstance.getGoal()).getMessage();
		replyWith=msg.getReplyWith();
		
		try {
			jobToBidFor = (job)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() {
		try{
			setBid(jobToBidFor);

			ZoneDataUpdate bidForJobUpdate=new ZoneDataUpdate.Builder(ID.LocalScheduler.ZoneData.bidForJob)
				.value(jobToBidFor).setReplyWith(replyWith).Build();
			/*ZoneDataUpdate bidForJobUpdate = new ZoneDataUpdate(
					ID.LocalScheduler.ZoneData.bidForJob,
					jobToBidFor);
*/

			AgentUtil.sendZoneDataUpdate(blackboard ,bidForJobUpdate, myAgent);

			//			log.info("Sending bid for job :" + jobToBidFor);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void setBid(job j){
		//		Random r = new Random();	
		//		j.setBidByLSA(r.nextDouble());

		jobQueue = (ArrayList<job>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue).
				getValue();

		ArrayList<job> tempQueue = new  ArrayList<job>();
		tempQueue.addAll(jobQueue);
		tempQueue.add(j);

		ScheduleSequence sch = new ScheduleSequence(tempQueue);
		ArrayList<job> tempqSolution = sch.getSolution();

		

		double PenaltyAfter=getPenaltyLocalDD(tempqSolution);
//		log.info("PenaltyAfter="+getPenaltyLocalDD(tempqSolution));
		double PenaltyBefore=getPenaltyLocalDD(jobQueue);
		log.info(myAgent.getLocalName()+" job Q size="+jobQueue.size());
//		log.info("PenaltyBefore="+getPenaltyLocalDD(jobQueue));
		double incremental_penalty=PenaltyAfter - PenaltyBefore;
		log.info(myAgent.getLocalName()+" incremental penalty="+incremental_penalty);
		
		bidNo=/*r.nextInt(10)+*/PenaltyAfter-PenaltyBefore;
		j.setBidByLSA(bidNo);
		j.setLSABidder(myAgent.getAID());
	}

	public double getPenaltyLocalDD(ArrayList<job> sequence) {
		long finishTime = 0;
		long cumulativeProcessingTime=0;//sum of processing times of jobs in Q standing ahead 
		//in milliseconds
		
		sequence=setStartTimes(sequence);
		
		double cost = 0.0;
		int l = sequence.size();

		for (int i = 0; i < l; i++) {
			
			finishTime = cumulativeProcessingTime+ sequence.get(i).getCurrentOperationProcessTime()*1000 +
					sequence.get(i).getStartTimeByCust().getTime();
			//getProcessingTime gives in time in seconds

//			log.info("difference="+(finishTime-sequence.get(i).getStartTime().getTime()));
			cumulativeProcessingTime=cumulativeProcessingTime+(long)sequence.get(i).getCurrentOperationProcessTime()*1000;

			double tardiness = 0.0;
			
//			log.info(myAgent.getLocalName()+ " cpt="+cumulativeProcessingTime +" L="+l+"ft="+new Date(finishTime)+" dd="+sequence.get(i).getDuedate()+" st="+sequence.get(i).getStartTime());
			
			if (finishTime > sequence.get(i).getCurrentOperationDueDate()){
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

	private ArrayList<job> setStartTimes(ArrayList<job> sequence) {
		long CumulativeWaitingTime=0;
		for(int i=0;i<sequence.size();i++){
			sequence.get(i).setJobStartTimeByCust(CumulativeWaitingTime+System.currentTimeMillis());
			CumulativeWaitingTime=CumulativeWaitingTime+(long)sequence.get(i).getCurrentOperationProcessTime()*1000;
		}
		return sequence;
		
	}
}	
