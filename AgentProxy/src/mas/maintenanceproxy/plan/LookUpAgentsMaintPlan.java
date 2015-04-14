package mas.maintenanceproxy.plan;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import mas.localSchedulingproxy.goal.SubscribeToGsaLsaGoal;
import mas.localSchedulingproxy.goal.SubscribeToMachineLsaGoal;
import mas.localSchedulingproxy.goal.SubscribeToMaintenanceLsaGoal;
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

public class LookUpAgentsMaintPlan extends CyclicBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private int step = 0;
	private BeliefBase bfBase;
	private DFAgentDescription dfd;
	private AID machine;
	private AID lsa;
	private Logger log;

	private SearchConstraints sc;

	private ServiceDescription sdLsa;
	private ServiceDescription sdMachine;

	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance planInstance) {

		bfBase = planInstance.getBeliefBase();
		log = LogManager.getLogger();

		dfd = new DFAgentDescription();
		sdLsa  = new ServiceDescription();
		sdMachine = new ServiceDescription();

		sdLsa.setType(ID.LocalScheduler.Service);
		dfd.addServices(sdLsa);

		sdMachine.setType(ID.Machine.Service);
		dfd.addServices(sdMachine);

		sc = new SearchConstraints();
		sc.setMaxResults(new Long(1));
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
							bfBase.updateBelief(ID.Maintenance.BeliefBaseConst.lsAgent, lsa);
							((BDIAgent)myAgent).addGoal(new SubscribeToLsaMaintGoal());

						} else if(ID.Machine.Service.equals(service)) {

							machine = result[i].getName();
							log.info("machine found  : " + machine);
							bfBase.updateBelief(ID.Maintenance.BeliefBaseConst.machineAgent, machine);
							((BDIAgent)myAgent).addGoal(new SubscribeToMachineMaintGoal());
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

						} else if(ID.Machine.Service.equals(service)) {
							machine = dfds[0].getName();
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
			bfBase.updateBelief(ID.Maintenance.BeliefBaseConst.lsAgent, lsa);
			((BDIAgent)myAgent).addGoal(new SubscribeToLsaMaintGoal());
			step = 2;
			break;

		case 6:
			log.info("Machine found  : " + machine);
			bfBase.updateBelief(ID.Maintenance.BeliefBaseConst.machineAgent, machine);
			((BDIAgent)myAgent).addGoal(new SubscribeToMachineMaintGoal());
			step = 2;
			break;

		}
	}
}
