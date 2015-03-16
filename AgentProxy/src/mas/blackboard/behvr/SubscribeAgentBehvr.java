package mas.blackboard.behvr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.blackboard.nameZoneData.NamedZoneData;
import mas.blackboard.namezonespace.NamedZoneSpace;
import mas.blackboard.zonedata.ZoneData;
import mas.blackboard.zonespace.ZoneSpace;
import mas.util.AgentUtil;
import mas.util.SubscriptionForm;
import bdi4jade.belief.Belief;
import bdi4jade.belief.BeliefSet;
import bdi4jade.core.BeliefBase;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class SubscribeAgentBehvr extends Behaviour {

	BeliefBase BBbeliefBase; //blackboard belief base
	SubscriptionForm.parameterSubscription tempSubscription;
	String AgentLocalName;
	private int step; 
	private AID subscriber; //subscriber of ZoneData
	private String AgentType; //agent service
	private Logger log;
	private static NamedZoneData nzd;
	private AID AgentToReg; //AID of agent whose ZoneData subscriber wants to subscribe
	
	public SubscribeAgentBehvr(AID agent_to_reg, BeliefBase tempBeliefbase,
			SubscriptionForm.parameterSubscription subscription, AID whoWantsTOSubscribe) {
		this.BBbeliefBase=tempBeliefbase;
		this.tempSubscription=subscription;
		this.subscriber=whoWantsTOSubscribe;
		DFAgentDescription dfa=new DFAgentDescription();
		ServiceDescription sd=new ServiceDescription();
		dfa.addServices(sd);
		this.AgentToReg=agent_to_reg;
		

	}

	@Override
	public void action() {
		log=LogManager.getLogger();
		step=1;
		switch(step){
			case 1:

				this.AgentType=AgentUtil.GetAgentService(AgentToReg,myAgent);
				if(AgentType!=null){ //check if agent exists in DF
					step++;
				}
						
			case 2:
				
				Belief<HashMap<String,ZoneSpace>> ws=(Belief<HashMap<String,ZoneSpace>>)BBbeliefBase.getBelief(AgentType);
				
				if(ws==null){ //Check if workspace is created for AgentType
				}
				else{
					
//					Iterator<ZoneSpace> it=ws.iterator();
//					while(it.hasNext()){ //starts searching for ZoneSpace
						
						
						
						HashMap<String, ZoneSpace> ZoneSpaceHashMap=(HashMap<String, ZoneSpace>)ws.getValue();
							
						ZoneSpace zs=ZoneSpaceHashMap.get(tempSubscription.Agent.getLocalName());
						
						if(zs!=null){
							for (String parameter : tempSubscription.parameters) {

								
								nzd=new NamedZoneData.Builder(parameter).build();
//								((Belief<HashMap<String, ZoneSpace>>)BBbeliefBase.getBelief(AgentType)).getValue().remove(key);
//								log.info("finding zone data: "+nzd.getName());
								if(zs.findZoneData(nzd)!=null){ 
									zs.findZoneData(nzd).subscribe(subscriber); //Throws null pointer exception if ZoneData doesnn't exists
//									((BeliefSet<ZoneSpace>)BBbeliefBase.getBelief(AgentType)).addValue(zs);								
//									Iterator<ZoneSpace> izd=((Set<ZoneSpace>)((BeliefSet<ZoneSpace>)BBbeliefBase.getBelief(AgentType)).getValue()).iterator();

									try {
										Thread.sleep(1); //without this sendupdate() from zoneData was not working. Don't know why.
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									step++;									
								}
								else{
									log.error("Couldn't find zone "+nzd.getName());
								}

								
								
							}
							
							ZoneSpaceHashMap.put(tempSubscription.Agent.getLocalName(), zs);
							
							((Belief<HashMap<String, ZoneSpace>>)BBbeliefBase.getBelief(AgentType)).setValue(ZoneSpaceHashMap);
							
						}
						
				
//					}
				}
		}
				

	}

	@Override
	public boolean done() {
//		log.info("ending susbscribe behvr"+step+" searching "+AgentToReg.getLocalName());
		return step>2;
	}

}
