package mas.blackboard.zonedata;

import jade.core.AID;
import jade.core.Agent;

public interface ZoneDataIFace {

	public void removeItem(Object obj);
	public Object[] getAllItem();
	public void subscribe(AID agent);
	public void unsubscribe(AID agent);
//	public void RemoveAllnAdd(Object obj);
	public void addItem(Object obj);
	
	
//	public boolean updateItem(Object oldObj, E newObj);
}