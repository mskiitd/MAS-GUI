package mas.customerproxy;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import mas.util.ID;
import bdi4jade.core.BDIAgent;

public abstract class AbstractCustomerAgent extends BDIAgent {
	
	private static final long serialVersionUID = 1L;
	
	private void registerMe() {
		DFAgentDescription mc = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(ID.Customer.Service);
		sd.setName(getLocalName());
		mc.addServices(sd);
		try {
			DFService.register(this, mc);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	@Override
	protected void init() {
		super.init();
		registerMe();
	}
}
