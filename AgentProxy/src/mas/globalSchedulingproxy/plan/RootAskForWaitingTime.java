package mas.globalSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.jobproxy.Batch;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class RootAskForWaitingTime extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;

	private Batch dummyJob;
	private AID blackboard;
	private int NoOfMachines;
	private String msgReplyID;
	private MessageTemplate mt;
	private int step = 0;
	private int MachineCount;
	protected Logger log;
	private ACLMessage[] WaitingTime;

	// The counter of replies from seller agents
	private int repliesCnt = 0; 
	private Batch JobToSend;
	private long CumulativeWaitingTime = 0;
	private BeliefBase bfBase;

	@Override
	public void init(PlanInstance PI) {
		log = LogManager.getLogger();

		try {
			dummyJob = (Batch)((MessageGoal)PI.getGoal()).getMessage().getContentObject();
			msgReplyID = Integer.toString(dummyJob.getBatchNumber());

		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		bfBase = PI.getBeliefBase();
		blackboard = (AID) bfBase.getBelief(ID.GlobalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();

		mt = MessageTemplate.and(
				MessageTemplate.MatchConversationId(MessageIds.msgWaitingTime),
				MessageTemplate.MatchReplyWith(msgReplyID));
	}

	@Override
	public void action() {
		switch (step) {
		case 0:

			this.MachineCount = (int) bfBase.getBelief(ID.GlobalScheduler.BeliefBaseConst.NoOfMachines).
			getValue();

			if(MachineCount != 0) {
				step = 1;
			}
			break;

		case 1:

			ZoneDataUpdate update = new ZoneDataUpdate.Builder(ID.GlobalScheduler.ZoneData.GetWaitingTime)
			.value(dummyJob).setReplyWith(msgReplyID).Build();
			AgentUtil.sendZoneDataUpdate(blackboard, update, myAgent);
			WaitingTime = new ACLMessage[MachineCount];

			step = 2;
			break;

		case 2:
			try{
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					WaitingTime[repliesCnt]=reply;
					repliesCnt++;
					//					log.info("got waiting time from "+ reply.getSender().getLocalName());

					if (repliesCnt == MachineCount) {				
						step = 3; 
						repliesCnt = 0;
					}
				}
				else {
					block();
				}
			}
			catch (Exception e3) {

			}
			break;
		case 3:
			try {
				ACLMessage max = getWorstWaitingTime(WaitingTime);
				CumulativeWaitingTime = CumulativeWaitingTime +
						((Batch)max.getContentObject()).getWaitingTime();

				JobToSend = (Batch)(max.getContentObject());

				if(dummyJob.getSampleJob().getCurrentOperationNumber() < 
						dummyJob.getSampleJob().getOperations().size()) {

					step = 1;
				}
				else {
					step = 4;
				}

			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			break;

		case 4:
			JobToSend.getSampleJob().setCurrentOperationNumber(0);
			JobToSend.setWaitingTime(CumulativeWaitingTime);

			if(JobToSend.getWaitingTime() < 0) {
				log.info("cannot process Batch no " + JobToSend.getBatchNumber() );
			}
			else{
				log.info("sending waiting time:" + CumulativeWaitingTime + " ms");
				ZoneDataUpdate NegotiationUpdate = new ZoneDataUpdate.Builder(ID.GlobalScheduler.ZoneData.GSAjobsUnderNegaotiation)
				.value(JobToSend).setReplyWith(msgReplyID).Build();
				AgentUtil.sendZoneDataUpdate(blackboard, NegotiationUpdate, myAgent);	
			}
			step = 5;
			break;

		}   
	}

	public ACLMessage getWorstWaitingTime(ACLMessage[] WaitingTime ) {
		ACLMessage MaxwaitingTimeMsg = WaitingTime[0]; 
		for(int i = 0; i < WaitingTime.length; i++){

			try {
				if(((Batch)(WaitingTime[i].getContentObject())).
						getWaitingTime() > ((Batch)(MaxwaitingTimeMsg.
								getContentObject())).getWaitingTime()){
					MaxwaitingTimeMsg = WaitingTime[i];
				}
			} catch (UnreadableException e) {
				e.printStackTrace();
			}

		}
		return MaxwaitingTimeMsg; //return maximum of all waiting times recieved from LSAs
	}

	@Override
	public boolean done() {
		return (step >= 5);
	}

	@Override
	public EndState getEndState() {
		if(step >= 5) {
			return EndState.SUCCESSFUL;
		}
		else{
			return null;
		}
	}
}