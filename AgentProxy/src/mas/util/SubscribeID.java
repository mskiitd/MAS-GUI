package mas.util;

import jade.core.AID;

public class SubscribeID {
	private AID agent;
	private boolean subscribed;
	
	public SubscribeID(AID agent, boolean b) {
		this.agent = agent;
		this.subscribed = b;
	}
	public AID getAgent() {
		return agent;
	}
	public void setAgent(AID agent) {
		this.agent = agent;
	}
	public boolean isSubscribed() {
		return subscribed;
	}
	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}
}
