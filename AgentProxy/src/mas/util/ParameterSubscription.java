package mas.util;

import java.io.Serializable;
import java.util.ArrayList;

import jade.core.AID;


public class ParameterSubscription implements Serializable {

	
	public ArrayList<subscription> subscriptionReq=new ArrayList<subscription>();
	public String AgentService;
	
	public ParameterSubscription(String AgentService){ 
		//AgentService is service provided by agent to whose
		//parameter your agent wants to subscribe
		this.AgentService=AgentService;
	}
	
	public void AddSubscriptionReq(AID Agent, String[] params){
		this.subscriptionReq.add(new subscription(Agent, params));
	}
	
	
		
	public class subscription implements Serializable{
		public AID Agent;
		public String[] parameters;
		subscription(AID Agent, String[] params){
			this.Agent=Agent;
			this.parameters=params;
		}
	}
}
