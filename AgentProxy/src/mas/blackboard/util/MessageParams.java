package mas.blackboard.util;

import mas.util.ZoneDataUpdate;

public class MessageParams {
	
	private String replyWith=null; 
	
	public MessageParams(Builder builder) {
		this.replyWith=builder.builder_replyWith;
	}
	 
	public String getReplyWith(){
		return this.replyWith;
	}

	public static class Builder {
		   private String builder_name = null;
		   private String builder_replyWith = null;
		   
		   public Builder(){
			   
		   }
		   
		   public Builder replyWithParam(String replyWith){
			   this.builder_replyWith=replyWith;
			   return this;
		   }
		   
		   public MessageParams Build(){
			   return new MessageParams(this);
		   }
	   }
}
