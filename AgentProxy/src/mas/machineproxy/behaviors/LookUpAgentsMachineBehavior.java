package mas.machineproxy.behaviors;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import mas.machineproxy.Simulator;
import mas.maintenanceproxy.goal.SubscribeToLsaMaintGoal;
import mas.maintenanceproxy.goal.SubscribeToMachineMaintGoal;
import mas.util.ID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BDIAgent;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class LookUpAgentsMachineBehavior  extends CyclicBehaviour {

	private static final long serialVersionUID = 1L;
	private int step = 0;
	private DFAgentDescription dfd;
	private AID maintenance;
	private AID lsa;
	private Logger log;
	private Simulator machineSimulator;

	private SearchConstraints sc;

	private ServiceDescription sdLsa;
	private ServiceDescription sdMaintenance;

	public LookUpAgentsMachineBehavior(Simulator sim) {
		log = LogManager.getLogger();

		this.machineSimulator = sim;
		lsa = sim.getLsAgent();
		maintenance = sim.getMaintAgent();

		dfd = new DFAgentDescription();
		sdLsa  = new ServiceDescription();
		sdMaintenance = new ServiceDescription();

		sdLsa.setType(ID.LocalScheduler.Service);
		dfd.addServices(sdLsa);

		sdMaintenance.setType(ID.Maintenance.Service);
		dfd.addServices(sdMaintenance);

		sc = new SearchConstraints();
		sc.setMaxResults(new Long(1));

		log.info("Looking up ...");
	}

	@Override
	public void action() {
		switch(step) {
		case 0:

			DFAgentDescription[] result;
			try {
				result = DFService.search(myAgent, dfd);
				log.info("result length : " + result.length );
				if (result.length > 0) {

					for(int i = 0; i < result.length; i ++) {
						String service = ((ServiceDescription) result[i].getAllServices().next()).getType();

						if(ID.LocalScheduler.Service.equals(service)) {

							lsa = result[i].getName();
							log.info("Lsa found : " + lsa);
							machineSimulator.setLsAgent(lsa);
							myAgent.addBehaviour(new SubscribeToLsaMachineBehavior(machineSimulator));

						} else if(ID.Maintenance.Service.equals(service)) {

							maintenance = result[i].getName();
							log.info("maintenance found  : " + maintenance);
							machineSimulator.setMaintAgent(maintenance);
							myAgent.addBehaviour(new SubscribeToMaintMachineBehavior(machineSimulator));
						}
					}
				} 
			} catch (FIPAException e) {
				e.printStackTrace();
			}
			step = 1;
			break;
		case 1:
			log.info("step 1 ");

			myAgent.send(DFService.createSubscriptionMessage(myAgent, myAgent.getDefaultDF(), 
					dfd, sc));
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
							step  = 5;

						} else if(ID.Maintenance.Service.equals(service)) {
							maintenance = dfds[0].getName();
							step = 6;
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
}
