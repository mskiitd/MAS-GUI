package mas.globalSchedulingproxy.plan;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.util.MessageIds;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class AskForBidPlan extends Behaviour implements PlanBody{

	private int step = 0;
	private ACLMessage[] bids;
	private int numLSA;
	private int repliesCnt;
	private BeliefBase bfBase;
	private MessageTemplate mt;
	private job JobToSend;
	private static final long serialVersionUID = 1L;

	@Override
	public EndState getEndState() {
		return (step >= 3 ? EndState.SUCCESSFUL : null );
	}

	@Override
	public void init(PlanInstance pInstance) {
		bfBase = pInstance.getBeliefBase();
		mt = MessageTemplate.MatchConversationId(MessageIds.msgbidForJob);
		repliesCnt = 0;
	}

	@Override
	public void action() {

		switch (step) {
		case 0:
			/*		numLSA = (Integer) bfBase.getBelief(AbstractGSCapability
								.MACHINES).getValue();*/

			bids = new ACLMessage[numLSA];
			step = 1;
			break;

		case 1:
			try{
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					bids[repliesCnt] = reply;
					repliesCnt++;				
					if (repliesCnt == numLSA) {				
						step = 2; 
					}
				} else {
					block();
				}
			} catch (Exception e3) {

			}
			break;

		case 2:			

			ACLMessage min = bids[0];			
			for(int i = 0; i < bids.length;i++){
				try {
					if(((Batch)(bids[i].getContentObject())).getBidByLSA() > 
					((Batch)(min.getContentObject())).getBidByLSA()) {
						min = bids[i];
					}
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}

			ACLMessage OrderToLSA = new ACLMessage(ACLMessage.REQUEST);
			try {
				OrderToLSA.setContentObject(JobToSend);
			} catch (IOException e) {
				e.printStackTrace();
			}
			OrderToLSA.addReceiver(min.getSender());
			OrderToLSA.setConversationId(MessageIds.msgbidResultJob);
			myAgent.send(OrderToLSA);
			//			System.out.println("OrderToLSA" + OrderToLSA);
			step = 3;
			break;
		}   
	}

	@Override
	public boolean done() {
		return step >= 3;
	}

}
