package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

import mas.jobproxy.Batch;
import mas.jobproxy.job;
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

public class SendJobToMachinePlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private ArrayList<Batch> jobQueue;
	private AID blackboard;
	private String status;
	private Logger log;
	private int step = 0;
	private long SleepTime = 500;
	private String replyWith;

	@Override
	public EndState getEndState() {
		return (step > 0 ? EndState.SUCCESSFUL : null);
	}

	@Override
	public void init(PlanInstance pInstance) {
		ACLMessage msg = ((MessageGoal)pInstance.getGoal()).getMessage();
		try {
			status  = (String) msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		bfBase = pInstance.getBeliefBase();
		log = LogManager.getLogger();

		jobQueue = (ArrayList<Batch>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue).
				getValue();

		blackboard = (AID) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();

		replyWith= msg.getReplyWith();
	}

	@Override
	public void action() {
		if("1".equals(status)) {

			if(jobQueue.size() > 0) {
				Batch batchToSend = jobQueue.get(0);
				batchToSend.resetJobsComplete(); //
				jobQueue.remove(0);
								
				log.info("Sending job to machine " + myAgent.getLocalName() + " " + batchToSend.getBatchNumber() );

				ZoneDataUpdate bidForJobUpdate = new ZoneDataUpdate.Builder(ID.LocalScheduler.ZoneData.batchForMachine)
				.value(batchToSend).setReplyWith(replyWith).Build();

				AgentUtil.sendZoneDataUpdate(blackboard , bidForJobUpdate, myAgent);
				bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue, jobQueue);
				bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.currentBatchOnMachine, batchToSend);
				log.info(bfBase.getBelief(ID.LocalScheduler.BeliefBaseConst.currentBatchOnMachine));
				

				
				step = 1;
			} else {
				block(SleepTime);
			}
		}
	}

	@Override
	public boolean done() {
		return step > 0;
	}
}
