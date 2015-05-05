package mas.blackboard.zonespace;

import jade.core.AID;
import jade.core.Agent;
import java.io.Serializable;
import java.util.HashMap;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mas.blackboard.nameZoneData.NamedZoneData;
import mas.blackboard.namezonespace.NamedZoneSpace;
import mas.blackboard.zonedata.ZoneData;

/**
 * One zone space contains one agent. 
 * @author NikhilChilwant
 *
 */
public class ZoneSpace implements ZoneSpaceIFace, Serializable{

	private static final long serialVersionUID = 1L;
	private String Zname;
	
	private HashMap<NamedZoneData, ZoneData> Zdata;
	private Logger log;
	private Agent bb;

/**
 * 
 * @param named_zone_space instance of named zone space containing name and relevent details
 * @param blacboard instance of blackboard
 */
	public ZoneSpace(NamedZoneSpace named_zone_space, Agent blacboard){
		log = LogManager.getLogger();
		this.Zname = named_zone_space.getLocalName();
		this.Zdata = new HashMap<NamedZoneData,ZoneData>();
		this.bb = blacboard;
	}

	@Override
	public void createZoneData(NamedZoneData name) {
		if(! Zdata.containsKey(name)) {
			ZoneData zd = new ZoneData(name, name.getUpdateMsgID(), bb, name.getAppend());
			Zdata.put(name, zd);
		}		
	}

	
	public void subscribeZoneData(String ZoneDataName, AID subscriber){
		NamedZoneData nzd=new NamedZoneData.Builder(ZoneDataName).build();

		if(findZoneData(nzd) != null){
			Zdata.get(nzd).subscribe(subscriber);
		}
	}

	public String getName(){
		return this.Zname;
	}

	@Override
	public void dropZone() {
		Zdata = null;
	}
	
	@Override
	public void removeItem(Object obj) {
		Zdata.remove(obj);
	}

	@Override
	public ZoneData findZoneData(NamedZoneData var) {
		return Zdata.get(var);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
		.append(Zname)
		.append(Zdata)
		.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if(obj instanceof ZoneSpace){
			final ZoneSpace other = (ZoneSpace) obj;
			return new EqualsBuilder()
			.append(Zname, other.Zname)
			.append(Zdata, other.Zdata)
			.isEquals();
		} else {
			return false;
		}
	}

	@Override
	public void unsubscribeZoneData(String ZoneDataName, AID subscriber) {
		NamedZoneData nzd = new NamedZoneData.Builder(ZoneDataName).build();

		if(findZoneData(nzd) != null){
			Zdata.get(nzd).unsubscribe(subscriber);
		}

	}

	@Override
	public void subscribeZoneSpace(AID subscriber) {

	}

}
