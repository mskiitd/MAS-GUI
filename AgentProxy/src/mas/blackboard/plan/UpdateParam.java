package mas.blackboard.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mas.blackboard.nameZoneData.NamedZoneData;
import mas.blackboard.util.MessageParams;
import mas.blackboard.zonespace.ZoneSpace;
import mas.util.AgentUtil;
import mas.util.ZoneDataUpdate;
import bdi4jade.belief.Belief;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * updates value of zone data after receiving update from concerned agent
 */

public class UpdateParam extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;

	private ACLMessage msg;
	private AID Agent;
	private ZoneDataUpdate info;
	private BeliefBase BBBeliefBase;
	private Logger log;
	private MessageParams msgStruct;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		MessageGoal goal = (MessageGoal) pInstance.getGoal();
		msg = goal.getMessage();
		Agent = msg.getSender();

		msgStruct = new MessageParams.Builder().
				replyWithParam(msg.getReplyWith()).
				Build();

		try {
			info = (ZoneDataUpdate) msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}		
		BBBeliefBase= pInstance.getBeliefBase();
	}

	@Override
	public void action() {
		String AgentType = AgentUtil.GetAgentService(Agent, myAgent);

		Belief<HashMap<String,ZoneSpace>> ws=(Belief<HashMap<String,ZoneSpace>>)BBBeliefBase.getBelief(AgentType);

		if(ws == null){
			log.error("Could not find workspace for " + AgentType);
		}
		else {					

			HashMap<String,ZoneSpace> ZoneSpaceHashMap=ws.getValue();
			ZoneSpace zs=ZoneSpaceHashMap.get(Agent.getLocalName());

			if(zs != null) {
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
		}
	}
}

