package mas.globalSchedulingproxy.behaviour;

/**
 * NOT USED CURRENTLY 
 * 
 * */

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BDIAgent;

public class WaitTimeBehvr extends Behaviour {
	private static final long serialVersionUID = 1L;
	//..//
	private String[] s= new String[3] ;  
	private double[] w= new double[3] ;
	private double[] wmax= new double[3] ;  // Max wait time
	private int repliesCnt = 0; // The counter of replies from seller agents
	private MessageTemplate mt; // The template to receive replies
	private int step = 0;
	private int MachineCount=0;
	private ACLMessage[] WaitingTime;
	private String CustomerAgent;
	private String msgReplyID;
	private AID bba;
	private Logger log;


	public WaitTimeBehvr(AID bb_AID, int NoOfmachines, String replyID){		
		//		this.MachineCount=(int)((BDIAgent)myAgent).getRootCapability().getBeliefBase().getBelief(ID.Blackboard.BeliefBaseConst.NoOfMachines).getValue();

		this.bba = bb_AID;
		this.log=LogManager.getLogger();

		this.msgReplyID=replyID;
		mt=MessageTemplate.and(
				MessageTemplate.MatchConversationId(MessageIds.msgWaitingTime),
				MessageTemplate.MatchInReplyTo(replyID));	
	}

	public void action() {
		switch (step) {
		case 0:
			this.MachineCount=(int)((BDIAgent)myAgent).getRootCapability().getBeliefBase().getBelief(ID.Blackboard.BeliefBaseConst.NoOfMachines).getValue();
			log.info(MachineCount);
			WaitingTime=new ACLMessage[MachineCount];
			step = 1;
			break;
		case 1:
			try{

				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					WaitingTime[repliesCnt]=reply;
					repliesCnt++;

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
			try {
				ACLMessage max=WaitingTime[0];
				for(int i = 0; i<WaitingTime.length;i++){

					if(((Batch)(WaitingTime[i].getContentObject())).getWaitingTime() > ((Batch)(max.getContentObject())).getWaitingTime()){
						max=WaitingTime[i];
					}

				}

				Batch JobToSend=(Batch)(max.getContentObject());

				ACLMessage replyToCust = new ACLMessage(ACLMessage.PROPOSE);
				if(JobToSend.getWaitingTime()<=JobToSend.getWaitingTime()){			

				}
				else{
					log.info(JobToSend.getDueDateByCustomer());
					ZoneDataUpdate NegotiationUpdate=new ZoneDataUpdate.Builder
							(ID.GlobalScheduler.ZoneData.GSAjobsUnderNegaotiation).
							value(JobToSend).Build();
					AgentUtil.sendZoneDataUpdate(this.bba, NegotiationUpdate, myAgent);
					/*replyToCust.setContentObject(JobToSend);												
				replyToCust.addReceiver(new AID(CustomerAgent, false));
				replyToCust.setConversationId(MessageIds.ReplyFromScheduler.toString());*/
				}


			} catch (UnreadableException e) {

				e.printStackTrace();
			}

			step = 3;
			break;

		}   

	}

	public boolean done() {
		return (step == 3);
	}
}
