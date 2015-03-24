package mas.blackboard.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.blackboard.nameZoneData.NamedZoneData;
import mas.blackboard.util.MessageParams;
import mas.blackboard.zonedata.ZoneData;
import mas.blackboard.zonespace.ZoneSpace;
import mas.util.AgentUtil;
import mas.util.ZoneDataUpdate;
import bdi4jade.belief.Belief;
import bdi4jade.belief.BeliefSet;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class UpdateParam extends OneShotBehaviour implements PlanBody {
//updates value of zone data after reieving update from concerned agent
	private ACLMessage msg;
	private AID Agent;
	private ZoneDataUpdate info;
	private BeliefBase BBBeliefBase;
	private Logger log;
	private MessageParams msgStruct;
	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance arg0) {
		log=LogManager.getLogger();
		MessageGoal goal=(MessageGoal) arg0.getGoal();
		msg=goal.getMessage();
		Agent=msg.getSender();
		
		msgStruct=new MessageParams.Builder().replyWithParam(msg.getReplyWith()).Build();
		
		try {
			info=(ZoneDataUpdate) msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}		
		BBBeliefBase= arg0.getBeliefBase();
	}

	@Override
	public void action() {
		String AgentType = AgentUtil.GetAgentService(Agent, myAgent);
		
		
				
				Belief<HashMap<String,ZoneSpace>> ws=(Belief<HashMap<String,ZoneSpace>>)BBBeliefBase.getBelief(AgentType);
				
				if(ws==null){
					log.error("Could not find workspace for "+AgentType);
				}
				else{					
					
					HashMap<String,ZoneSpace> ZoneSpaceHashMap=ws.getValue();
					ZoneSpace zs=ZoneSpaceHashMap.get(Agent.getLocalName());

						if(zs!=null){
							NamedZoneData nzd = new NamedZoneData.Builder(info.getName()).build();

							if(zs.findZoneData(nzd)!=null){
							
								zs.findZoneData(nzd).addItem(info.getValue(), msgStruct);

							}
							else{
								log.info("couldn't find zone for "+nzd.getName());
							}
							ZoneSpaceHashMap.put(Agent.getLocalName(), zs);
							
							((Belief<HashMap<String,ZoneSpace>>)BBBeliefBase.getBelief(AgentType)).setValue(ZoneSpaceHashMap);
							
						}
						
						
//					}
				}
//				System.out.println(((BeliefSet<ZoneSpace>)BBBeliefBase.getBelief(AgentType)).getValue().iterator().next().findZoneData(new NamedZoneData(info.getName())));
	}
	
}

