package mas.globalSchedulingproxy.plan;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;

import mas.jobproxy.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.JobQueryObject;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import bdi4jade.core.BDIAgent;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class QueryFromLSA extends Behaviour implements PlanBody {

	private AID blackboard_AID;
	private int step=0;
	private MessageTemplate mt;
	private int MachineCount=0;
	private int repliesCnt;
	private ACLMessage[] LSAqueryResponse;
	private Logger log=LogManager.getLogger();

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance PI) {
		blackboard_AID = new AID(ID.Blackboard.LocalName, false);
		mt=MessageTemplate.MatchConversationId(MessageIds.msgLSQueryResponse);
	}

	@Override
	public void action() {
		
		switch(step){
			case 0:
				this.MachineCount = (int) ((BDIAgent) myAgent).getRootCapability()
				.getBeliefBase()
				.getBelief(ID.GlobalScheduler.BeliefBaseConst.NoOfMachines)
				.getValue();

				if (MachineCount != 0) {
					job j=new job.Builder("doesn'tmatter").build();
					j.setJobNo(1);
					JobQueryObject queryForm=new JobQueryObject.Builder().currentJob(j).build();
					ZoneDataUpdate QueryRequest= new ZoneDataUpdate.Builder(ID.GlobalScheduler.ZoneData.QueryRequest)
					.value(queryForm).Build();
					AgentUtil.sendZoneDataUpdate(blackboard_AID, QueryRequest, myAgent);
					
					step = 1;
				}
		
			case 1:
				try {
					ACLMessage reply = myAgent.receive(mt);
					if (reply != null) {
						LSAqueryResponse[repliesCnt] = reply;
						repliesCnt++;
						if (repliesCnt == MachineCount) {
							step = 2;
						}
					}

					else {
						block();
					}
				} catch (Exception e3) {

				}
				
				break;
				
			case 2:
				log.info(getQueryResponse(LSAqueryResponse));
				step=3;
				break;
		
	
		}
		
		
		
	}

	private Message getQueryResponse(ACLMessage[] LSAqueryResponse2) {
		for(int i=0;i<LSAqueryResponse2.length;i++){
			try {
				JobQueryObject queryResponse=(JobQueryObject)LSAqueryResponse2[i].getContentObject();
				job j=(queryResponse).getCurrentJob();
				if(j!=null){
					log.info(j.getJobNo()+" is at "+queryResponse.getCurrentMachine().getLocalName());
					if(queryResponse.isOnMachine()){
						log.info(j.getJobNo()+" is currently under process at "+queryResponse.getCurrentMachine().getLocalName());
					}
				}
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
		
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return step==3;
	}

}
