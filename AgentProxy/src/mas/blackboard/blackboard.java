package mas.blackboard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.blackboard.capability.CommunicationCenter;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import bdi4jade.core.BDIAgent;
import mas.util.*;



public class blackboard extends BDIAgent {
	public static BDIAgent BBagent;
	private Logger log;
	public void init() {
		log=LogManager.getLogger(this.getClass());
        
		/* Registering with DF*/
		DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName( getAID() ); 
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(ID.Blackboard.Service);
        
        sd.setName( getLocalName() );
        dfd.addServices(sd);
        
        try {  

        	DFService.register(this, dfd );
        	log.info("BB Registered with DF");
        }
        catch (FIPAException fe) { fe.printStackTrace(); }
        
        /* Registering with DF finished*/
        
        
        BBagent=this;
        addCapability(new CommunicationCenter(BBagent));
	}
	

}

