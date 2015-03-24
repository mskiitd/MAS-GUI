package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.job.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * @author Anand Prajapati
 *	Sends average waiting time for the new job to global scheduling agent
 *  Based on this waiting time global scheduling accepts/negotiates the job 
 *  from customer
 *
 */

public class SendWaitingTimePlan extends OneShotBehaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private ACLMessage msg;
	private ArrayList<job> jobQueue;
	private job j;
	private BeliefBase bfBase;
	private StatsTracker sTracker;
	private double averageProcessingTime;
	private double averageQueueSize;
	private AID blackboard;
	private Logger log;
	private String replyWith;

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
			j = (job)(msg.getContentObject());
		} catch (UnreadableException e) {
			e.printStackTrace();
		}

		jobQueue = (ArrayList<job>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue).
				getValue();

		sTracker = (StatsTracker) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.dataTracker).
				getValue();

		this.blackboard = (AID) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();
		
		replyWith=msg.getReplyWith();
	}

	@Override
	public void action() {		
		sTracker.addSize( jobQueue.size() );

		// get average queue size and waiting time in the queue
		/*averageQueueSize = sTracker.getAverageQueueSize().doubleValue();
		averageProcessingTime = sTracker.getAvgProcessingTime();

		long avgWaitingTime = (long) (averageProcessingTime*averageQueueSize);
*///		log.info("waiting time is : " + avgWaitingTime);
		
		long WaitingTime=0;
		
		for(int i=0;i<jobQueue.size();i++){
			WaitingTime=WaitingTime+jobQueue.get(i).getCurrentOperationProcessTime()*1000;
		}
		
		j.setWaitingTime(/*avgWaitingTime +*/WaitingTime+ j.getCurrentOperationProcessTime());
//		j.setStartTime(/*avgWaitingTime +*/ WaitingTime+System.currentTimeMillis()); //why do we need this???

//		log.info("waiting time is : " + j.getWaitingTime()+ "due date is "+ j.getDuedate());
		ZoneDataUpdate waitingTimeUpdate=new ZoneDataUpdate.Builder(ID.LocalScheduler.ZoneData.WaitingTime)
			.value(this.j).setReplyWith(replyWith).Build();
		
//		log.info("replyWith = "+replyWith);
		/*ZoneDataUpdate waitingTimeUpdate = new ZoneDataUpdate(
				ID.LocalScheduler.ZoneData.WaitingTime,
				this.j );*/

		AgentUtil.sendZoneDataUpdate(blackboard ,waitingTimeUpdate, myAgent);
	}
}
