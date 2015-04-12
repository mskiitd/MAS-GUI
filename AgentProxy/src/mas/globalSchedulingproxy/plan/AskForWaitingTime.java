package mas.globalSchedulingproxy.plan;

import jade.lang.acl.ACLMessage;

public class AskForWaitingTime extends RootAskForWaitingTime {

	private static final long serialVersionUID = 1L;

	@Override
	public ACLMessage getWorstWaitingTime(ACLMessage[] WaitingTime) {
		
		//takes array of msg got from all LSAs
		
		return super.getWorstWaitingTime(WaitingTime);
	}
}
