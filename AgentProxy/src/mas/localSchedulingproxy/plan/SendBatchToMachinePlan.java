package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

import mas.jobproxy.Batch;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class SendBatchToMachinePlan extends CyclicBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private ArrayList<Batch> jobQueue;
	private AID blackboard;
	private String status;
	private Logger log;
	private int step;
	private long SleepTime = 800;
	private ACLMessage msg;
	private MessageTemplate sendJobMsgTemplate;
	private String replyWith;

	@Override
	public EndState getEndState() {
		return (step >= 2 ? EndState.SUCCESSFUL : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(PlanInstance pInstance) {
		bfBase = pInstance.getBeliefBase();
		log = LogManager.getLogger();
		step = 0;
		jobQueue = (ArrayList<Batch>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue).
				getValue();

		blackboard = (AID) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();

		sendJobMsgTemplate = MessageTemplate.MatchConversationId(MessageIds.msgaskJobFromLSA);
		log.info("running.. " + sendJobMsgTemplate);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {

		switch(step) {
		case 0:
			msg = myAgent.receive(sendJobMsgTemplate);
			if(msg != null) {

				try {
					status = (String) msg.getContentObject();
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				replyWith = msg.getReplyWith();
				log.info("msg receievd : " + status);
				step = 1;
			} else {
				block();
			}
			break;
		case 1:
			//				if("1".equals(status)) {
			Batch currentBatchOnMachine=(Batch) bfBase.getBelief(ID.LocalScheduler.
					BeliefBaseConst.currentBatchOnMachine).getValue();
			
			if(jobQueue.size() > 0 && currentBatchOnMachine==null) {
				Batch batchToSend = jobQueue.get(0);
				batchToSend.resetJobsComplete(); 
				jobQueue.remove(0);

				ZoneDataUpdate bidForJobUpdate = new ZoneDataUpdate.
						Builder(ID.LocalScheduler.ZoneData.batchForMachine).
						value(batchToSend).
						setReplyWith(replyWith).
						Build();

				AgentUtil.sendZoneDataUpdate(blackboard , bidForJobUpdate, myAgent);

				bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue, jobQueue);
				bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.currentBatchOnMachine, batchToSend);

				log.info("sending.. " + batchToSend);

				step = 0;
			} else {
//				log.info("currentBatchOnMachine = "+currentBatchOnMachine+"jobQueue.size() ="+jobQueue.size());
				jobQueue = (ArrayList<Batch>) bfBase.
						getBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue).
						getValue();
//				log.info("getting jobQueue "+jobQueue.size());
				block(100);
			}
			break;
		}
	}
}
