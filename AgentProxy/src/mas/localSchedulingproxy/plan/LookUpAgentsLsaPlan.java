package mas.localSchedulingproxy.plan;

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
import mas.util.ID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.core.BDIAgent;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class LookUpAgentsLsaPlan extends CyclicBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private int step = 0;
	private BeliefBase bfBase;
	private DFAgentDescription dfd;
	private AID machine;
	private AID gsa;
	private AID maintenance;
	private Logger log;

	private SearchConstraints sc;

	private ServiceDescription sdGsa;
	private ServiceDescription sdMaintenance;
	private ServiceDescription sdMachine;

	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance planInstance) {

		bfBase = planInstance.getBeliefBase();
		log = LogManager.getLogger();

		gsa = (AID) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.globalSchAgent).
				getValue();

		machine = (AID) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.machine).
				getValue();

		maintenance = (AID) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.maintAgent).
				getValue();

		dfd = new DFAgentDescription();
		sdGsa  = new ServiceDescription();
		sdMaintenance = new ServiceDescription();
		sdMachine = new ServiceDescription();

		sdGsa.setType(ID.GlobalScheduler.Service);
		dfd.addServices(sdGsa);

		sdMaintenance.setType(ID.Maintenance.Service);
		dfd.addServices(sdMaintenance);

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

						if(ID.GlobalScheduler.Service.equals(service)) {

							gsa = result[i].getName();
							log.info("Gsa found : " + gsa);
							bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.globalSchAgent, gsa);
							((BDIAgent)myAgent).addGoal(new SubscribeToGsaLsaGoal());
						}
						else if(ID.Maintenance.Service.equals(service)) {

							maintenance = result[i].getName();
							log.info("Maintenance found  : " + maintenance);
							bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.maintAgent, maintenance);
							((BDIAgent)myAgent).addGoal(new SubscribeToMaintenanceLsaGoal());

						} else if(ID.Machine.Service.equals(service)) {

							machine = result[i].getName();
							log.info("machine found  : " + machine);
							bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.machine, machine);
							((BDIAgent)myAgent).addGoal(new SubscribeToMachineLsaGoal());
						}
					}
				} else {
					step = 1;
				}
			} catch (FIPAException e) {
				e.printStackTrace();
			}
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

						if(ID.GlobalScheduler.Service.equals(service)) {
							gsa = dfds[0].getName();
							step  = 5;

						} else if(ID.Maintenance.Service.equals(service)) {
							maintenance = dfds[0].getName();
							step = 6;

						} else if(ID.Machine.Service.equals(service)) {
							machine = dfds[0].getName();
							step = 7;
						}
					}
				}
				catch (Exception ex) {}
			} else {
				block();
			}
			break;

		case 5:
			log.info("GSA found  : " + gsa);
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.globalSchAgent, gsa);
			((BDIAgent)myAgent).addGoal(new SubscribeToGsaLsaGoal());
			step = 2;
			break;

		case 6:
			log.info("Maintenance found  : " + maintenance);
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.globalSchAgent, maintenance);
			((BDIAgent)myAgent).addGoal(new SubscribeToMaintenanceLsaGoal());
			step = 2;
			break;

		case 7:
			log.info("Machine found  : " + machine);
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.globalSchAgent, machine);
			((BDIAgent)myAgent).addGoal(new SubscribeToMachineLsaGoal());
			step = 2;
			break;
		}
	}

}