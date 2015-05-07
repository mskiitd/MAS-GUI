package mas.globalSchedulingproxy.plan;

import jade.lang.acl.ACLMessage;
/**
 * Ask for expeted due date from every machine. Reply customer  worst case.
 * @author NikhilChilwant
 *
 */
public class AskForWaitingTime extends RootAskForWaitingTime {

	private static final long serialVersionUID = 1L;

	@Override
	public ACLMessage getWorstWaitingTime(ACLMessage[] WaitingTime) {
		
		//takes array of msg got from all LSAs
		
		return super.getWorstWaitingTime(WaitingTime);
	}
}
