package mas.blackboard.nameZoneData;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.io.Serializable;
/**
 * Creates Zone data with name
 * @author NikhilChilwant
 *
 */
public class NamedZoneData implements ZoneDataName, Serializable {
	
	private static final long serialVersionUID = 1L;
	private String name;
	//message ID of update message to be sent by Blackboard to subscribers of ZoneData
	private String MsgIDforUpdate; 
	private boolean toAppendValues;

	/**
	 * Builder for Named Zone Data
	 * @author NikhilChilwant
	 *
	 */
	public static class Builder {
		private String name = null;


		private String UpdateMsgID=null;
		
		//to store single value on paramter variable or multiple value
		//if multiple, appendValue=true
		private boolean appendValues=false;

		public Builder(String name){
			this.name=name;
		}

		/**
		 * 
		 * @param UpdateMsgID Message ID to be used when update of this parameter to be sent to registered agent by blackboard.
		 * @return builder of NamedZoneData.class
		 */
		public  Builder MsgID(String UpdateMsgID){
			this.UpdateMsgID=UpdateMsgID;
			return this;
		}

		/**
		 * 
		 * @param toAppend Whether to store multiple values at paramter or not. If yes, make it true.
		 * @return Builder of NamedZoneData.class
		 */
		public Builder appendValue(boolean toAppend){
			this.appendValues=toAppend;
			return this;
		}

		/**
		 * Builds builder of NamedZoneData class
		 * @return Builder of NamedZoneData.class
		 */
		public NamedZoneData build(){
			return new NamedZoneData(this);
		}
	}

	/**
	 * 
	 * @param ConstructorBuilder Builder for named zone data
	 */
	public NamedZoneData(Builder ConstructorBuilder) {
		this.name=ConstructorBuilder.name;
		this.MsgIDforUpdate=ConstructorBuilder.UpdateMsgID;
		this.toAppendValues=ConstructorBuilder.appendValues;
	}

	/**
	 * 
	 * @return Whether parameter is storing multiple values. 
	 * If 'true'. it is storing multiple values in parameter variable
	 */
	public boolean getAppend(){
		return this.toAppendValues;
	}

	/**
	 * get name of NamedZoneData.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * @return Message ID used when update of this parameter to be sent to registered agent by blackboard.
	 */
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
