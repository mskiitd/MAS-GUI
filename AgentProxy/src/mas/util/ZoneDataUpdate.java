package mas.util;

import java.io.IOException;

import mas.blackboard.nameZoneData.NamedZoneData;
import mas.blackboard.nameZoneData.NamedZoneData.Builder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Serializable;

public class ZoneDataUpdate implements Serializable {
	String name=null;
	Object value=null;
	String replyWith=null;
	static Logger log; //Try making the Logger static instead. 
	//Than you don't have to care about serialization because it is handled by the class loader.
	//http://stackoverflow.com/questions/82109/should-a-log4j-logger-be-declared-as-transient
	
/*************
 * Keep in mind that every type of the class fields also need to be serializable. 
 * So, if any class  stores a field of type MyType, which does not implement Serializable,
 *  when you'll the mentioned exception
 *  
 *  *****************************/
	
	
/*//	boolean toBeAppended;
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
	}*/
	
	
	
	 public static class Builder {
		   private String builder_name = null;
		   private Object builder_value = null;
		   private String reply=null;
		   
		   public Builder(String name){
			   this.builder_name=name;
		   }
		   
		   public Builder value(Object value){
			   this.builder_value=value;
			   return this;
		   }
		   
		   public Builder setReplyWith(String reply){
			   //used to set setReply in ACL message
			   this.reply=reply;
			   return this;
		   }
		   
		   public ZoneDataUpdate Build(){
			   return new ZoneDataUpdate(this);
		   }
	   }
	 
	 private ZoneDataUpdate(Builder constructorBuilder){
		 this.name=constructorBuilder.builder_name;
		 this.value=constructorBuilder.builder_value;
		 this.replyWith=constructorBuilder.reply;
	 }
	
	 public String getName(){
			return this.name;
		}
		
	 public Object getValue(){
		return this.value;
	 }
	 
	 public String getReplyWith(){
		 return this.replyWith;
	 }
	 
}
