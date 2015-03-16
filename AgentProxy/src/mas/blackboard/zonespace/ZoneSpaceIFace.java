package mas.blackboard.zonespace;

import jade.core.AID;
import mas.blackboard.nameZoneData.NamedZoneData;
import mas.blackboard.namezonespace.NamedZoneSpace;
import mas.blackboard.zonedata.ZoneData;

public interface ZoneSpaceIFace {
	
	public void dropZone();
	public void removeItem(Object obj);
	public ZoneData findZoneData(NamedZoneData var);
	public void insertItem(NamedZoneData var, Object obj);
	public void createZoneData(NamedZoneData name);
	public void subscribeZoneData(String ZoneDataName, AID subscriber);
	public void unsubscribeZoneData(String ZoneDataName, AID subscriber);
	public void subscribeZoneSpace(AID subscriber);
	
	
	
}
