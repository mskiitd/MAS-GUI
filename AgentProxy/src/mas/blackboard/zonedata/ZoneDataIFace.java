package mas.blackboard.zonedata;

import mas.blackboard.util.MessageParams;
import jade.core.AID;
import jade.core.Agent;

public interface ZoneDataIFace {

	public void removeItem(Object obj);
	public Object[] getAllItem();
	public void subscribe(AID agent);
	public void unsubscribe(AID agent);
//	public void RemoveAllnAdd(Object obj);
	void addItem(Object obj, MessageParams msgStruct);
	public void sendUpdate(MessageParams msgStruct);	
	
//	public boolean updateItem(Object oldObj, E newObj);
}