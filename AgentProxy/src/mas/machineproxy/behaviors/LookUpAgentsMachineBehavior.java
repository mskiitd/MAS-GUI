package mas.machineproxy.behaviors;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import mas.machineproxy.Simulator;
import mas.util.ID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LookUpAgentsMachineBehavior  extends Behaviour {

	private static final long serialVersionUID = 1L;
	private int step = 0;
	private DFAgentDescription dfdLsa;
	private DFAgentDescription dfdMaint;
	private AID maintenance;
	private AID lsa;
	private Logger log;
	private Simulator machineSimulator;

	private SearchConstraints sc;

	private ServiceDescription sdLsa;
	private ServiceDescription sdMaintenance;

	private boolean lsaFound = false;
	private boolean maintFound = false;

	private String mySurName;

	public LookUpAgentsMachineBehavior(Simulator sim) {
		log = LogManager.getLogger();

		this.machineSimulator = sim;
		lsa = sim.getLsAgent();
		maintenance = sim.getMaintAgent();

		dfdLsa = new DFAgentDescription();
		sdLsa  = new ServiceDescription();
		sdMaintenance = new ServiceDescription();

		sdLsa.setType(ID.LocalScheduler.Service);
		dfdLsa.addServices(sdLsa);

		sdMaintenance.setType(ID.Maintenance.Service);
		dfdLsa.addServices(sdMaintenance);

		sc = new SearchConstraints();
		sc.setMaxResults(new Long(1));
	}

	@Override
	public void action() {
		switch(step) {
		case 0:
			String name = myAgent.getLocalName();
			mySurName = name.substring(name.lastIndexOf("#") + 1, name.length());

			DFAgentDescription[] resultLsa, resultMaint;
			try {
				resultLsa = DFService.search(myAgent, dfdLsa);
				if (resultLsa.length > 0) {
					for(int i = 0; i < resultLsa.length; i ++) {
						lsa = resultLsa[i].getName();

						String lsaName = maintenance.getLocalName();
						int hashIdx = lsaName.lastIndexOf("#");
						String lsaSurName = lsaName.substring(hashIdx + 1, lsaName.length());

						if(mySurName.equals(lsaSurName)) {
							log.info("Lsa found : " + lsa);
							lsaFound = true;
							machineSimulator.setLsAgent(lsa);
							myAgent.addBehaviour(new SubscribeToLsaMachineBehavior(machineSimulator));
							break;
						}
					}
				}
				resultMaint = DFService.search(myAgent, dfdMaint);
				if (resultMaint.length > 0) {
					for(int i = 0; i < resultMaint.length; i ++) {

						maintenance = resultMaint[i].getName();

						String maintName = maintenance.getLocalName();
						int hashIdx = maintName.lastIndexOf("#");
						String maintSurName = maintName.substring(hashIdx + 1, maintName.length());

						if(mySurName.equals(maintSurName)) {
							maintFound = true;
							log.info("maintenance found  : " + maintenance);
							machineSimulator.setMaintAgent(maintenance);
							myAgent.addBehaviour(new SubscribeToMaintMachineBehavior(machineSimulator));
							break;
						}
					}
				} 

			} catch (FIPAException e) {
				e.printStackTrace();
			}
			step = 1;
			break;
		case 1:
			if(!maintFound) {
				myAgent.send(DFService.createSubscriptionMessage(myAgent, myAgent.getDefaultDF(), 
						dfdMaint, sc));
			}

			if(!lsaFound) {
				myAgent.send(DFService.createSubscriptionMessage(myAgent, myAgent.getDefaultDF(), 
						dfdLsa, sc));
			}
			step = 2;
			break;

		case 2:
			ACLMessage msg = myAgent.receive(
					MessageTemplate.MatchSender(myAgent.getDefaultDF()));
			if (msg != null) {
				try {
					DFAgentDescription[] dfds = DFService.decodeNotification(msg.getContent());

					log.info("dfd length : " + dfds.length);
					if (dfds.length > 0) {
						String service = ((ServiceDescription) dfds[0].getAllServices().next()).getType();

						if(ID.LocalScheduler.Service.equals(service)) {
							lsa = dfds[0].getName();
							String lsaName = maintenance.getLocalName();
							int hashIdx = lsaName.lastIndexOf("#");
							String lsaSurName = lsaName.substring(hashIdx + 1, lsaName.length());

							if(mySurName.equals(lsaSurName)) {
								step  = 5;
							}

						} else if(ID.Maintenance.Service.equals(service)) {
							maintenance = dfds[0].getName();

							String maintName = maintenance.getLocalName();
							int hashIdx = maintName.lastIndexOf("#");
							String maintSurName = maintName.substring(hashIdx + 1, maintName.length());

							if(mySurName.equals(maintSurName)) {
								step = 6;
							}
						}
					}
				}
				catch (Exception ex) {}
			} else {
				block();
			}
			break;

		case 5:
			log.info("LSA found  : " + lsa);
			machineSimulator.setLsAgent(lsa);
			myAgent.addBehaviour(new SubscribeToLsaMachineBehavior(machineSimulator));
			step = 2;
			break;

		case 6:
			log.info("Maintenance found  : " + maintenance);
			machineSimulator.setMaintAgent(maintenance);
			myAgent.addBehaviour(new SubscribeToMaintMachineBehavior(machineSimulator));
			step = 2;
			break;
		}
	}

	@Override
	public boolean done() {
		return lsaFound && maintFound;
	}
}
