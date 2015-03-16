package mas.util;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Serializable;

public class ZoneDataUpdate implements Serializable {
	String name;
	Object value;
	static Logger log; //Try making the Logger static instead. 
	//Than you don't have to care about serialization because it is handled by the class loader.
	//http://stackoverflow.com/questions/82109/should-a-log4j-logger-be-declared-as-transient
	
/*************
 * Keep in mind that every type of the class fields also need to be serializable. 
 * So, if any class  stores a field of type MyType, which does not implement Serializable,
 *  when you'll the mentioned exception
 *  
 *  *****************************/
	
	
//	boolean toBeAppended;
	public ZoneDataUpdate(String ZoneDataName, Object value) {
		this.name=ZoneDataName;
		this.value=value;
//		this.toBeAppended=ToAppend;
		
	}
	
	public String getName(){
		return this.name;
	}
	
	public Object getValue(){
		return this.value;
	}
	
/*	public boolean toAppendToCurrentValue(){
		return this.toBeAppended;
	}*/

/*	public static void sendUpdate(AID blackboard_AID, ZoneDataUpdate zdu, Agent Sender) {
//		log.info(zdu.name);
		ACLMessage update=new ACLMessage(ACLMessage.INFORM);
		update.addReceiver(blackboard_AID);
		try {
			update.setContentObject(zdu);
		} catch (IOException e) {
			e.printStackTrace();
		}
		update.setConversationId(MessageIds.UpdateParameter);
		Sender.send(update);
//		log.info(update);
	}*/
}
