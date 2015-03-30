package mas.blackboard.plan;

import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import mas.blackboard.nameZoneData.NamedZoneData;
import mas.blackboard.namezonespace.NamedZoneSpace;
import mas.blackboard.zonespace.ZoneSpace;
import mas.util.AgentUtil;
import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
import bdi4jade.core.BDIAgent;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 *  adds agent in blackboard workspace
 */

public class AddAgent extends OneShotBehaviour implements PlanBody {
	private static final long serialVersionUID = 1L;

	//agent to be added in blackboard workspace
	private AID AgentToReg; 
	//belief base of blackboard
	private BeliefBase bfBase;
	//To be taken from message content
	private String AgentType; 
	 //contains names of zone data s to be created for AgentToReg
	private NamedZoneData[] ZoneDataNameArray;
	private Logger log;

	@Override
	public EndState getEndState() {		
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance PI) {
		log = LogManager.getLogger();
		MessageGoal goal = (MessageGoal) PI.getGoal();
		ACLMessage Message = goal.getMessage();
		AgentToReg = Message.getSender();
		bfBase = PI.getBeliefBase();
		try {
			Object temp = Message.getContentObject();
			ZoneDataNameArray = (NamedZoneData[])temp;
			/*for(int i=0;i<ZoneDataNameArray.length;i++){
				log.info("for "+ZoneDataNameArray[i].getName()+" -> "+ZoneDataNameArray[i].getUpdateMsgID());
			}*/
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() {		  
		AgentType = AgentUtil.GetAgentService(AgentToReg,myAgent);
		log.info("Adding Agent :" + AgentToReg.getLocalName());
		Belief<HashMap<String,ZoneSpace>> wspace;

		if(!bfBase.hasBelief(AgentType)) {
			HashMap<String,ZoneSpace> wspace_hashMap = new HashMap<String,ZoneSpace>();
			wspace = new TransientBelief<HashMap<String,ZoneSpace>>(AgentType, wspace_hashMap);		
			bfBase.addBelief(wspace);			
		}
		else {
			wspace = (Belief<HashMap<String,ZoneSpace>>)bfBase.getBelief(AgentType);
		}
		NamedZoneSpace nz = new NamedZoneSpace(AgentToReg);
		ZoneSpace zs = new ZoneSpace(nz,myAgent);

		for(int i =0 ; i < ZoneDataNameArray.length; i++) {			
			zs.createZoneData(ZoneDataNameArray[i]);
		}

		HashMap<String,ZoneSpace> ZoneSpaceHashMap = wspace.getValue();
		ZoneSpaceHashMap.put(nz.getLocalName(), zs);
		wspace.setValue(ZoneSpaceHashMap);
		//		log.info((wspace.getValue()));
		//update belief base
		
		bfBase.addOrUpdateBelief(wspace);
//		((BDIAgent)myAgent).getRootCapability().getBeliefBase().addOrUpdateBelief(wspace); 
		log.info(AgentType +" type Agent added");
		//		log.info(((BDIAgent)myAgent).getRootCapability().getBeliefBase());
	}
}
