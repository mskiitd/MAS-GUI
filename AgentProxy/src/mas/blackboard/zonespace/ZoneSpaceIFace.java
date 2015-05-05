package mas.blackboard.zonespace;

import jade.core.AID;
import mas.blackboard.nameZoneData.NamedZoneData;
import mas.blackboard.zonedata.ZoneData;

public interface ZoneSpaceIFace {
	/**
	 * delete zone space
	 */
	public void dropZone();
	
	/**
	 * 
	 * @param obj value stored in zone space
	 */
	public void removeItem(Object obj);
	
	/**
	 * 
	 * @param var named zone data to be searched
	 * @return zone data found
	 */
	public ZoneData findZoneData(NamedZoneData var);
	
	/**
	 * construct zone data with parameters
	 * @param name Named Zone Data containing details
	 */
	public void createZoneData(NamedZoneData name);
	
	/**
	 * 
	 * @param ZoneDataName Name of zone data to subscribed for
	 * @param subscriber AID of agent which want to subscribe
	 */
	public void subscribeZoneData(String ZoneDataName, AID subscriber);
	
	/**
	 * 
	 * @param ZoneDataName Name of zone data to unsubscribed for
	 * @param subscriber AID of agent which want to unsubscribe
	 */
	public void unsubscribeZoneData(String ZoneDataName, AID subscriber);
	
	/**
	 * 
	 * @param subscriber AID of agent which want to subscribe
	 */
	public void subscribeZoneSpace(AID subscriber);
	
}
