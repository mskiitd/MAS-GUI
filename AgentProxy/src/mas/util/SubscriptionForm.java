package mas.util;

import jade.core.AID;
import java.io.Serializable;
import java.util.ArrayList;


public class SubscriptionForm implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<parameterSubscription> subscriptionReq
			= new ArrayList<parameterSubscription>();
	
	public SubscriptionForm() {
	}

	public void AddSubscriptionReq(AID Agent, String[] params){
		this.subscriptionReq.add(new parameterSubscription(Agent, params));
	}
		
	public class parameterSubscription implements Serializable{
		public AID Agent;
		public String[] parameters;
		parameterSubscription(AID Agent, String[] params){
			this.Agent=Agent;
			this.parameters=params;
		}
	}
	
	public ArrayList<parameterSubscription> GetSubscriptions(){
		return subscriptionReq; 
	}
}
