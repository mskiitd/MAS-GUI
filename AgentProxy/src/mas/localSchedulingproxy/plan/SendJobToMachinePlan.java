package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.jobproxy.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class SendJobToMachinePlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private ArrayList<job> jobQueue;
	private AID blackboard;
	private String status;
	private Logger log;
	private int step = 0;
	private long SleepTime = 500;
	private String replyWith;

	@Override
	public EndState getEndState() {
		return null;
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

		jobQueue = (ArrayList<job>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue).
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
				job jobToSend=jobQueue.get(0);
				log.info("Sending job to machine "+ myAgent.getLocalName() +" "+ jobToSend.getJobNo());
				ZoneDataUpdate bidForJobUpdate = new ZoneDataUpdate.Builder(ID.LocalScheduler.ZoneData.jobForMachine)
					.value(jobToSend).setReplyWith(replyWith).Build();
			/*	ZoneDataUpdate bidForJobUpdate = new ZoneDataUpdate(
						ID.LocalScheduler.ZoneData.jobForMachine,
						jobQueue.get(0));*/

				jobQueue.remove(0);
				AgentUtil.sendZoneDataUpdate(blackboard ,bidForJobUpdate, myAgent);
				
				bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.currentJobOnMachine, jobToSend);
				bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue, jobQueue);
				
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
