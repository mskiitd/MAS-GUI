package mas.machineproxy.behaviors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.util.ID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Register2DF extends OneShotBehaviour{

	private static final long serialVersionUID = 100L;
	private Logger log;
	
	@Override
	public void action() {
		log = LogManager.getLogger();
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(myAgent.getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(ID.Machine.Service);
		sd.setName(myAgent.getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(myAgent, dfd);
			log.info(myAgent.getLocalName()+" registered with DF");

		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
}