package mas.blackboard.zonespace;

import jade.core.AID;
import jade.core.Agent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.blackboard.nameZoneData.NamedZoneData;
import mas.blackboard.namezonespace.NamedZoneSpace;
import mas.blackboard.zonedata.ZoneData;

/**
 * @author Anand Prajapati
 * @param <V>
 */

public class ZoneSpace implements ZoneSpaceIFace, Serializable{
	
	private String Zname;
	private HashMap<NamedZoneData, ZoneData> Zdata;
	private Logger log;
	private Agent bb;
	
	
	public ZoneSpace(NamedZoneSpace n, Agent blacboard){
		log=LogManager.getLogger();
		this.Zname = n.getLocalName();
		this.Zdata = new HashMap<NamedZoneData,ZoneData>();
		this.bb=blacboard;
		
	}
	
	/*public static ZoneSpace newInstance(NamedZone name){
		return new ZoneSpace(name);
	}*/
	@Override
	public void createZoneData(NamedZoneData name) {
		if(! Zdata.containsKey(name)){
			
			ZoneData zd = new ZoneData(name, name.getUpdateMsgID(), bb, name.getAppend());
			Zdata.put(name, zd);
			
		}		
	}
	
	public void subscribeZoneData(String ZoneDataName, AID subscriber){
		NamedZoneData nzd=new NamedZoneData.Builder(ZoneDataName).build();
		
		if(findZoneData(nzd)!=null){
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
	public void insertItem(NamedZoneData var, Object obj) {
		if(findZoneData(var)==null){
			createZoneData(var);
		}
		findZoneData(var).addItem(obj);
		
		
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
		NamedZoneData nzd=new NamedZoneData.Builder(ZoneDataName).build();
		
		if(findZoneData(nzd)!=null){
			Zdata.get(nzd).unsubscribe(subscriber);
		}
		
	}

	@Override
	public void subscribeZoneSpace(AID subscriber) {
		// TODO Auto-generated method stub
		
	}


/*	@Override
	public void createZoneData(NamedZoneData name, String MsgID) {
		if(! Zdata.containsKey(name)){
			ZoneData zd = new ZoneData(name, MsgID);
			Zdata.put(name, zd);
		}
		
	}*/
	
	/*public void subscribeZoneSpace(AID subscriber){
		Iterator it= Zdata.entrySet().iterator();
		while(it.hasNext()){
			
		}
	}*/

	
	
}
