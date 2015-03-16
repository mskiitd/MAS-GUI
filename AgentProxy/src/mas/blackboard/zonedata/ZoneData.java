package mas.blackboard.zonedata;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mas.blackboard.nameZoneData.NamedZoneData;
import mas.util.MessageIds;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ZoneData implements ZoneDataIFace, Serializable{
	protected NamedZoneData name;
	private LinkedList<Object> data;
	private HashSet<AID> subscribers;
	private Logger log;
	private String UpdateMessageID;
	private Agent bb; //needed for sending update message
	private boolean appendValues;
	
	public ZoneData(NamedZoneData name2, String UpdateMsgID, Agent blackboard, boolean appendValues){
		log=LogManager.getLogger();
		this.name = name2;
		this.data = new LinkedList<Object>();
		this.subscribers=new HashSet<AID>();
		this.UpdateMessageID=UpdateMsgID; //ID of message to be used while sending update of data
		this.bb=blackboard;
		this.appendValues=appendValues;
	}
	

	public String getName(){
		return this.name.getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ZoneData){
	        final ZoneData other = (ZoneData) obj;
	        return new EqualsBuilder()
	            .append(name, other.name)
	            .append(data, other.data)
	            .isEquals();
	    } else {
	        return false;
	    }
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(name)
				.append(data)
				.toHashCode();
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(name)
				.append(data.toString())
				.toString();
	}

	@Override
	public void addItem(Object obj) {
		if(getAppendValues()){
//			log.info(getAppendValues());
			this.data.add(obj);
		}
		else{
//			log.info(this.data.isEmpty());
			if(!this.data.isEmpty()){
				this.data.remove(); //assumes list contains at max 1 element. This will make list empty
				
			}
			this.data.add(obj);
		}
//		log.info(data);
		sendUpdate();
	
	}

	@Override
	public void removeItem(Object obj) {
		if(this.data.contains(obj)){
			this.data.remove(obj);
		}
	}

	@Override
	public Object[] getAllItem() {
		return this.data.toArray();
	}

	@Override
	public void subscribe(AID agent) {
		subscribers.add(agent);
		log.info(agent.getLocalName()+" subscribed for "+name);
		
		
	}

	@Override
	public void unsubscribe(AID agent) {
		subscribers.remove(agent);
		
	}

	public String getUpdateMessageID(){
		return UpdateMessageID;
	}
	
	public HashSet<AID> getSubscribers(){
		return subscribers;
	}
	
	public boolean getAppendValues(){
		return appendValues;
	}
	
	public Object getData(){
//		log.info(data);
		return data.pop();
		
	}
	public void sendUpdate(){
		
		
		ACLMessage update=new ACLMessage(ACLMessage.INFORM);		
		update.setConversationId(UpdateMessageID);
	
		for(AID reciever : getSubscribers()){
			log.info("adding reciever "+reciever.getLocalName());
			update.addReceiver(reciever);
//			log.info("sent update of "+name.getName()+" to "+reciever.getLocalName()+" with ID "+UpdateMessageID);
		}
		try {
			Object obj= getData();
			update.setContentObject((Serializable) obj);
//			log.info("value is "+obj);
			log.info("ZoneData "+ getName()+" updated"+ " UpdateMessageID: "+UpdateMessageID);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		log.info(update);
		bb.send(update);
		
		
	}

}
