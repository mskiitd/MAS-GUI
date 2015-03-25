package mas.globalSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.job.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;
import bdi4jade.core.BDIAgent;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class RootAskForWaitingTime extends Behaviour implements PlanBody {

	private job j;
	private AID blackboard;
	private int NoOfMachines;
	private String msgReplyID;
	private MessageTemplate mt;
	private int step = 0;
	private int MachineCount;
	protected Logger log;
	private ACLMessage[] WaitingTime;
	private int repliesCnt = 0; // The counter of replies from seller agents

	private BeliefBase bfBase;

	@Override
	public void init(PlanInstance PI) {
		log=LogManager.getLogger();

		try {
			j=(job)((MessageGoal)PI.getGoal()).getMessage().getContentObject();
		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		blackboard=(AID)PI.getBeliefBase().
				getBelief(ID.GlobalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();


		msgReplyID=Integer.toString(j.getJobNo());
		mt=MessageTemplate.and(
				MessageTemplate.MatchConversationId(MessageIds.msgWaitingTime),
				MessageTemplate.MatchReplyWith(msgReplyID));

		this.bfBase = PI.getBeliefBase();
		//		mt=MessageTemplate.MatchConversationId(MessageIds.msgWaitingTime);

	}

	@Override
	public void action() {
		switch (step) {
		case 0:

			this.MachineCount = (int) bfBase.
			getBelief(ID.GlobalScheduler.BeliefBaseConst.NoOfMachines).
			getValue();
			//			log.info(MachineCount);

			if(MachineCount != 0){
				//				log.info(j.getDuedate());
				//				ZoneDataUpdate update=new ZoneDataUpdate(ID.GlobalScheduler.ZoneData.GetWaitingTime, j);
				ZoneDataUpdate update = new ZoneDataUpdate.Builder(ID.GlobalScheduler.ZoneData.GetWaitingTime).
						value(j).
						setReplyWith(msgReplyID).
						Build();
				
				AgentUtil.sendZoneDataUpdate(blackboard, update, myAgent);
				WaitingTime = new ACLMessage[MachineCount];
				step = 1;

			}

			break;
		case 1:
			//			log.info("step="+step);
			//			log.info("expecting reply ID : "+msgReplyID);
			try{

				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					WaitingTime[repliesCnt]=reply;
					repliesCnt++;
					log.info("got waiting time from "+ reply.getSender().getLocalName());

					if (repliesCnt == MachineCount) {				
						step = 2; 
					}
				}

				else {
					block();
				}
			}
			catch (Exception e3) {

			}
			break;
		case 2:
			//			log.info("step="+step);
			try {



				ACLMessage max=ChooseWaitingTimeToSend(WaitingTime);
				job JobToSend=(job)(max.getContentObject());
				//				log.info(JobToSend.getJobDuedatebyCust());
				//				ZoneDataUpdate NegotiationUpdate=new ZoneDataUpdate(ID.GlobalScheduler.ZoneData.GSAjobsUnderNegaotiation, JobToSend);
				ZoneDataUpdate NegotiationUpdate=new ZoneDataUpdate.Builder(ID.GlobalScheduler.ZoneData.GSAjobsUnderNegaotiation)
				.value(JobToSend).setReplyWith(msgReplyID).Build();
				AgentUtil.sendZoneDataUpdate(blackboard, NegotiationUpdate, myAgent);

			} catch (UnreadableException e) {

				e.printStackTrace();
			}

			step = 3;
			break;

		}   
	}

	public ACLMessage ChooseWaitingTimeToSend(ACLMessage[] WaitingTime ) {
		ACLMessage MaxwaitingTimeMsg=WaitingTime[0]; 
		for(int i = 0; i<WaitingTime.length;i++){

			try {
				if(((job)(WaitingTime[i].getContentObject())).
						getWaitingTime() > ((job)(MaxwaitingTimeMsg.
								getContentObject())).getWaitingTime()){
					MaxwaitingTimeMsg=WaitingTime[i];
				}
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return MaxwaitingTimeMsg; //return maximum of all waiting times recieved from LSAs
	}

	@Override
	public boolean done() {

		return (step==3);
	}

	@Override
	public EndState getEndState() {
		if(step==3){
			return EndState.SUCCESSFUL;
		}
		else{
			return EndState.FAILED;
		}

	}
}
