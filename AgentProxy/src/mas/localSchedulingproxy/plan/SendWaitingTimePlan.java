package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.util.ArrayList;
import mas.jobproxy.Batch;
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
 *  @author Anand Prajapati
 *	Sends average waiting time for the new job to global scheduling agent
 *  Based on this waiting time global scheduling accepts/negotiates the job 
 *  from customer
 */

public class SendWaitingTimePlan extends OneShotBehaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private static final long LargeLongNegativeValue =-3*((long)Math.pow(10,10));
			//3*((long)Math.pow(10,10)) milliseconds ~ 0.95 years
	//if we set Long.MIN_VALUE, and addition happends in calculation of waiting in time in GSA,
	//due to bit limit, Long.MIN_VALUE+Long.MIN_VALUE becomes 0 which is wrong
	private ACLMessage msg;
	private ArrayList<Batch> jobQueue;
	private Batch j;
	private BeliefBase bfBase;
	private StatsTracker sTracker;
	private double averageProcessingTime;
	private double averageQueueSize;
	private AID blackboard;
	private Logger log;
	private String replyWith;
	private OperationDataBase operationdb;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		bfBase = pInstance.getBeliefBase();
		log = LogManager.getLogger();
		try {
			msg = ((MessageGoal)pInstance.getGoal()).getMessage();
			j = (Batch)(msg.getContentObject());
		} catch (UnreadableException e) {
			e.printStackTrace();
		}

		jobQueue = (ArrayList<Batch>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue).
				getValue();

		sTracker = (StatsTracker) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.dataTracker).
				getValue();

		this.blackboard = (AID) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();

		this.operationdb = (OperationDataBase) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.operationDatabase).
				getValue();

		replyWith = msg.getReplyWith();
	}

	@Override
	public void action() {		
		sTracker.addSize( jobQueue.size() );

		// get average queue size and waiting time in the queue
		averageQueueSize = sTracker.getAverageQueueSize().doubleValue();
		averageProcessingTime = sTracker.getAvgProcessingTime();

		long avgWaitingTime = (long) (averageProcessingTime * averageQueueSize);

		long WaitingTime = 0;

		for(int i = 0; i < jobQueue.size(); i++) {
			WaitingTime = WaitingTime + jobQueue.get(i).getCurrentOperationProcessingTime();
		}

		if(operationdb.contains(j.getSampleJob().getCurrentOperation().getJobOperationType()) ) {
			j.setWaitingTime(avgWaitingTime ); //WaitingTime+ j.getCurrentOperationProcessTime());
		} else {
			log.info("Operation" + j.getSampleJob().getCurrentOperation().getJobOperationType() +
					"unsupported on this machine");
			j.setWaitingTime(LargeLongNegativeValue);
		}
		//		log.info("waiting time is : " + j.getWaitingTime()+ "due date is "+ j.getDuedate());
		ZoneDataUpdate waitingTimeUpdate = new ZoneDataUpdate.
				Builder(ID.LocalScheduler.ZoneData.WaitingTime).
				value(this.j).
				setReplyWith(replyWith).
				Build();

		AgentUtil.sendZoneDataUpdate(blackboard ,waitingTimeUpdate, myAgent);
	}
}
