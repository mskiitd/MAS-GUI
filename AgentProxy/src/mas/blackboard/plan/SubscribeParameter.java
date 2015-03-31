package mas.blackboard.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mas.blackboard.behvr.SubscribeAgentBehavior;
import mas.util.SubscriptionForm;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * plan to add agent in subscriber list of ZoneData	
 */

/* User should not be able to modify method of subscription */
public class SubscribeParameter extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;

	//agent which wants to subscribe
	private AID WhoWantsTOSubscribe; 
	private BeliefBase BBBeliefbase; 
	//	private String AgentType; 
	private ArrayList<SubscriptionForm.parameterSubscription> Subscriptions; 
	private boolean IsActionComplete=false;
	private SubscriptionForm ps;
	private Logger log;

	@Override
	public EndState getEndState() {
		if(IsActionComplete) {
			return EndState.SUCCESSFUL;
		}
		else {
			return null;
		}
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();

		MessageGoal mg = (MessageGoal) pInstance.getGoal();

		ACLMessage RecievedMsg = mg.getMessage();
		WhoWantsTOSubscribe=RecievedMsg.getSender();

		try {					
			ps = (SubscriptionForm)(mg.getMessage().getContentObject());
			Subscriptions = ps.GetSubscriptions();
			BBBeliefbase = pInstance.getBeliefBase();

		} catch (UnreadableException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void action() {
		//		log.info("Subscriptions"+Subscriptions);

		for(int k = 0; k < Subscriptions.size(); k++) {
			
			AID AgentToReg = Subscriptions.get(k).Agent;
			myAgent.addBehaviour(new SubscribeAgentBehavior(AgentToReg, BBBeliefbase, Subscriptions.get(k),WhoWantsTOSubscribe));						
		}
		IsActionComplete=true;
	}
}


