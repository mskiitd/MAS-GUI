package mas.blackboard.plan;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import mas.blackboard.nameZoneData.NamedZoneData;
import mas.blackboard.namezonespace.NamedZoneSpace;
import mas.blackboard.workspace.WorkspaceBeliefSet;
import mas.blackboard.zonedata.ZoneData;
import mas.blackboard.zonespace.ZoneSpace;
import mas.util.AgentUtil;
import bdi4jade.belief.Belief;
import bdi4jade.belief.BeliefSet;
import bdi4jade.belief.TransientBelief;
import bdi4jade.core.BDIAgent;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class AddAgent extends OneShotBehaviour implements PlanBody{
//	adds agent in blackboard workspace
	private AID AgentToReg; //agent to be added in blackboard workspace
	private BeliefBase belief;  //belief base of blackboard
	private String AgentType; //To be taken from message content
	private NamedZoneData[] ZoneDataNameArray; //containes names of zone data s to be created fro AgentToReg
	private Logger log;

	@Override
	public EndState getEndState() {		
		return null;
	}

	@Override
	public void init(PlanInstance PI) {
		log=LogManager.getLogger();
		MessageGoal goal = (MessageGoal) PI.getGoal();
		ACLMessage Message = goal.getMessage();
		AgentToReg=Message.getSender();
		belief=PI.getBeliefBase();
		try {
			Object temp=Message.getContentObject();
			ZoneDataNameArray=(NamedZoneData[])temp;
			/*for(int i=0;i<ZoneDataNameArray.length;i++){
				log.info("for "+ZoneDataNameArray[i].getName()+" -> "+ZoneDataNameArray[i].getUpdateMsgID());
			}*/
		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
	}

	@Override
	public void action() {		  
		AgentType=AgentUtil.GetAgentService(AgentToReg,myAgent);
		log.info("Adding Agent :"+AgentToReg.getLocalName());
		Belief<HashMap<String,ZoneSpace>> wspace;
		
		
		
		if(!belief.hasBelief(AgentType)){
			HashMap<String,ZoneSpace> wspace_hashMap=new HashMap<String,ZoneSpace>();
			wspace=new TransientBelief<HashMap<String,ZoneSpace>>(AgentType, wspace_hashMap);		
			belief.addBelief(wspace);			
		}
		else{
			wspace=(Belief<HashMap<String,ZoneSpace>>)belief.getBelief(AgentType);
		}
		NamedZoneSpace nz=new NamedZoneSpace(AgentToReg);
		ZoneSpace zs=new ZoneSpace(nz,myAgent);
		
		for(int i=0;i<ZoneDataNameArray.length;i++){			
			zs.createZoneData(ZoneDataNameArray[i]);
		}
		
		
		HashMap<String,ZoneSpace> ZoneSpaceHashMap=wspace.getValue();
		ZoneSpaceHashMap.put(nz.getLocalName(), zs);
		wspace.setValue(ZoneSpaceHashMap);
//		log.info((wspace.getValue()));
		((BDIAgent)myAgent).getRootCapability().getBeliefBase().addOrUpdateBelief(wspace); //update belief base
		log.info(AgentType +" type Agent added");
//		log.info(((BDIAgent)myAgent).getRootCapability().getBeliefBase());
	}
}
