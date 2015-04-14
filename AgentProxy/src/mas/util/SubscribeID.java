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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubscribeID other = (SubscribeID) obj;
		if (agent == null) {
			if (other.agent != null)
				return false;
		} else if (!agent.equals(other.agent))
			return false;
		return true;
	}
	
}
