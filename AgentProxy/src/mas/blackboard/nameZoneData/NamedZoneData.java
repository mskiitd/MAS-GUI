package mas.blackboard.nameZoneData;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.lang.StringBuilder;

public class NamedZoneData implements ZoneDataName, Serializable
{
   private String name;
   private String MsgIDforUpdate; //message ID of update message to be sent by Blackboard to subscribers of ZoneData
   private boolean toAppendValues;


   public static class Builder {
	   private String name = null;
	   private String UpdateMsgID=null;
	   private boolean appendValues=false;
	   
	   public Builder(String name){
		   this.name=name;
	   }
	   
	   public  Builder MsgID(String UpdateMsgID){
		   this.UpdateMsgID=UpdateMsgID;
		   return this;
	   }
	   
	   public Builder appendValue(boolean toAppend){
		   this.appendValues=toAppend;
		   return this;
	   }
	   
	   public NamedZoneData build(){
		   return new NamedZoneData(this);
	   }
   }
   
   public NamedZoneData(Builder ConstructorBuilder) {
	   this.name=ConstructorBuilder.name;
	   this.MsgIDforUpdate=ConstructorBuilder.UpdateMsgID;
	   this.toAppendValues=ConstructorBuilder.appendValues;
   }

   public boolean getAppend(){
	   return this.toAppendValues;
   }

   public String getName() {
      return this.name;
   }
   
   public String getUpdateMsgID(){
	   return this.MsgIDforUpdate;
   }

   public boolean equals(Object obj) {
      if (obj == null || !(obj instanceof NamedZoneData)) return false;

      return this.getName().equals(((NamedZoneData)obj).getName());
   }

   public int hashCode() {
      return new HashCodeBuilder().append(this.name).append(NamedZoneData.class).toHashCode();
   }

   
   
   public String toString() {
      return "a NamedParameter \"" + this.getName() + "\"";
   }
}
