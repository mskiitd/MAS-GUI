package mas.blackboard.zonedata;

import mas.blackboard.util.MessageParams;
import jade.core.AID;

public interface ZoneDataIFace {

	public void removeItem(Object obj);
	public Object[] getAllItem();
	
	/**
	 * 
	 * @param agent who want to subscribe for parameter
	 */
	public void subscribe(AID agent);
	
	/**
	 * 
	 * @param agent desiring to unsubscribe
	 */
	public void unsubscribe(AID agent);

	/**
	 * add parameter value
	 * @param obj value
	 * @param msgStruct Information about message to used while sending update
	 */
	void addItem(Object obj, MessageParams msgStruct);
	
	/**
	 * 
	 * @param msgStruct Information about message to used while sending update
	 */
	public void sendUpdate(MessageParams msgStruct);	
	
}