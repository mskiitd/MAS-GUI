package mas.blackboard.zonedata;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import mas.blackboard.nameZoneData.NamedZoneData;
import mas.blackboard.util.MessageParams;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Contains parameters of agent
 * @author NikhilChilwant
 *
 */
public class ZoneData implements ZoneDataIFace, Serializable{

	private static final long serialVersionUID = 1L;
	protected NamedZoneData name;
	
	//data stored
	private LinkedList<Object> data;
	
	//subscribers of zone data
	private HashSet<AID> subscribers;
	private Logger log;
	
	//message ID to be used when updates to sent
	private String UpdateMessageID;
	private Agent bb;
	
	//Whether multiple valus to be stored or not
	private boolean appendValues;

	/**
	 * 
	 * @param zoneDataName Name of zone data
	 * @param UpdateMsgID Message ID to be used when update of this paramter to be sent
	 * @param blackboard instance of blackboard
	 * @param appendValues whether to store multiple values in paramter
	 */
	public ZoneData(NamedZoneData zoneDataName, String UpdateMsgID, Agent blackboard, boolean appendValues){
		log = LogManager.getLogger();
		this.name = zoneDataName;
		this.data = new LinkedList<Object>();
		this.subscribers = new HashSet<AID>();
		//ID of message to be used while sending update of data
		this.UpdateMessageID = UpdateMsgID; 
		this.bb = blackboard;
		this.appendValues = appendValues;
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
	public void addItem(Object obj, MessageParams msgStruct) {
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
		sendUpdate(msgStruct);

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
		return data.pop();

	}

	/**
	 * sends update of parameter
	 */
	public void sendUpdate(MessageParams msgStruct){

		ACLMessage update=new ACLMessage(ACLMessage.INFORM);		
		update.setConversationId(UpdateMessageID);
		update.setReplyWith(msgStruct.getReplyWith());

		for(AID reciever : getSubscribers()) {
			//			log.info("adding reciever "+reciever.getLocalName());
			update.addReceiver(reciever);
			//			log.info("sent update of "+name.getName()+" to "+reciever.getLocalName()+" with ID "+UpdateMessageID);
		}
		try {
			Object obj= getData();
			update.setContentObject((Serializable) obj);
			//			log.info("value is "+obj);
//			log.info("ZoneData "+ getName()+" updated"+ " UpdateMessageID: "+UpdateMessageID);
		} catch (IOException e) {
			e.printStackTrace();
		}
//			log.info(update);
		bb.send(update);
	}
}
