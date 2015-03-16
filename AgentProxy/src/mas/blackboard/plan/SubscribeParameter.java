package mas.blackboard.plan;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;
import java.util.Iterator;




import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.blackboard.behvr.SubscribeAgentBehvr;
import mas.blackboard.nameZoneData.NamedZoneData;
import mas.blackboard.namezonespace.NamedZoneSpace;
import mas.blackboard.workspace.Workspace;
import mas.blackboard.zonespace.ZoneSpace;
import mas.util.AgentUtil;

import mas.util.SubscriptionForm;

import bdi4jade.belief.BeliefSet;
import bdi4jade.core.BDIAgent;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;
/* User should not be able to modify method of subscription */
public class SubscribeParameter extends OneShotBehaviour implements PlanBody {
//plan to add agent in subscriber list of ZoneData	
	private AID WhoWantsTOSubscribe; //agent which wants to subscribe
	private BeliefBase BBBeliefbase; 
//	private String AgentType; 
	private ArrayList<SubscriptionForm.parameterSubscription> Subscriptions; 
	private boolean IsActionComplete=false;
	private SubscriptionForm ps;
	private Logger log;
	
	@Override
	public EndState getEndState() {
		if(IsActionComplete){
			return EndState.SUCCESSFUL;
		}
		else{
			return EndState.FAILED;//Conceptually this is wrong But EndState enum has only 2 values.
		}
	}

	@Override
	public void init(PlanInstance arg0) {
		log=LogManager.getLogger();
		
		MessageGoal mg=(MessageGoal) arg0.getGoal();
		
		ACLMessage RecievedMsg = mg.getMessage();
		WhoWantsTOSubscribe=RecievedMsg.getSender();
	
		
		try {					
			ps = (SubscriptionForm)(mg.getMessage().getContentObject());
			Subscriptions = ps.GetSubscriptions();
			BBBeliefbase=arg0.getBeliefBase();
			
		} catch (UnreadableException e) {
			e.printStackTrace();
		}		

	}

		
	@Override
	public void action() {
//		log.info("Subscriptions"+Subscriptions);
		
		for(int k=0;k<Subscriptions.size();k++)
		{
			AID AgentToReg=Subscriptions.get(k).Agent;
//			AgentType=AgentUtil.GetAgentService(AgentToReg,myAgent);
			myAgent.addBehaviour(new SubscribeAgentBehvr(AgentToReg, BBBeliefbase, Subscriptions.get(k),WhoWantsTOSubscribe));						
		}
		IsActionComplete=true;
	}

	}


