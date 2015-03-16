package mas.globalSchedulingproxy.plan;

import jade.lang.acl.ACLMessage;

public class AskForWaitingTime extends RootAskForWaitingTime {

	private static final long serialVersionUID = 1L;

	@Override
	public ACLMessage ChooseWaitingTimeToSend(ACLMessage[] WaitingTime){
		//takes array of msg got froms all LSAs
//		super.log.info("chooseing waiting time");
		return super.ChooseWaitingTimeToSend(WaitingTime);
	}
}
