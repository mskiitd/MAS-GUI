package mas.blackboard.behvr;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.blackboard.nameZoneData.NamedZoneData;
import mas.blackboard.zonespace.ZoneSpace;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.SubscriptionForm;
import bdi4jade.belief.Belief;
import bdi4jade.core.BeliefBase;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class SubscribeAgentBehavior extends Behaviour {

	private static final long serialVersionUID = 1L;
	//blackboard belief base
	BeliefBase BBbeliefBase;
	SubscriptionForm.parameterSubscription tempSubscription;
	String AgentLocalName;
	private int step; 
	private AID subscriber; 
	//agent service
	private String AgentType; 
	private Logger log;
	private static NamedZoneData nzd;
	//AID of agent whose ZoneData subscriber wants to subscribe
	private AID AgentToReg; 
	private HashMap<AID, String> serviceBase;

	public SubscribeAgentBehavior(AID agent_to_reg, BeliefBase tempBeliefbase,
			SubscriptionForm.parameterSubscription subscription, AID whoWantsTOSubscribe) {

		this.BBbeliefBase = tempBeliefbase;
		this.tempSubscription = subscription;
		this.subscriber = whoWantsTOSubscribe;
		DFAgentDescription dfa = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		dfa.addServices(sd);
		this.AgentToReg = agent_to_reg;
		log = LogManager.getLogger();
		serviceBase = (HashMap<AID, String>) BBbeliefBase.
				getBelief(ID.Blackboard.BeliefBaseConst.serviceDiary).
				getValue();
	}
	
	private String getService(AID agentToRegister) {
		if(serviceBase != null && serviceBase.containsKey(agentToRegister)) {
			return serviceBase.get(agentToRegister);
		}
		String agentType = AgentUtil.GetAgentService(agentToRegister,myAgent);
		serviceBase.put(agentToRegister, agentType);
		BBbeliefBase.updateBelief(ID.Blackboard.BeliefBaseConst.serviceDiary, serviceBase);
		log.info("Adding service type : " + agentToRegister.getLocalName() + " : " + agentType + "myagent: " + serviceBase);
		return agentType;
	}

	@Override
	public void action() {
		step = 1;
		switch(step) {
		case 1:

			this.AgentType = AgentUtil.GetAgentService(AgentToReg,myAgent);
			if(AgentType != null) { //check if agent exists in DF
				step++;
			}else {
				block(1000);
			}

		case 2:

			Belief<HashMap<String,ZoneSpace>> ws = (Belief<HashMap<String,ZoneSpace>>)BBbeliefBase.getBelief(AgentType);

			//Check if workspace is created for AgentType
			if(ws == null) { 
			}
			else {
				HashMap<String, ZoneSpace> ZoneSpaceHashMap = (HashMap<String, ZoneSpace>) ws.getValue();

				ZoneSpace zs = ZoneSpaceHashMap.get(tempSubscription.Agent.getLocalName());

				if(zs != null) {
					for (String parameter : tempSubscription.parameters) {

						nzd = new NamedZoneData.Builder(parameter).build();
						//								log.info("finding zone data: "+nzd.getName());
						if(zs.findZoneData(nzd) != null) {
							//Throws null pointer exception if ZoneData doesnn't exists
							zs.findZoneData(nzd).subscribe(subscriber); 

							//without this sendupdate() from zoneData was not working. Don't know why.
							try {
								Thread.sleep(1); 
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							step++;									
						}
						else {
							log.error("Couldn't find zone " + nzd.getName());
						}

					}
					ZoneSpaceHashMap.put(tempSubscription.Agent.getLocalName(), zs);

					((Belief<HashMap<String, ZoneSpace>>)BBbeliefBase.getBelief(AgentType)).setValue(ZoneSpaceHashMap);
//					step++;	
				}
			}
		}
	}

	@Override
	public boolean done() {
		//		log.info("ending susbscribe behvr"+step+" searching "+AgentToReg.getLocalName());
		return step > 2;
	}

}
