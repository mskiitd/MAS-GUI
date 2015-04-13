package mas.customerproxy.plan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.customerproxy.goal.SubscribeToZonesGoal;
import mas.util.ID;
import bdi4jade.core.BDIAgent;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class LookUpAgentsCustomerPlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private int step = 0;
	private BeliefBase bfBase;
	private boolean gsAgentFound = false;
	private AID gsa;
	private DFAgentDescription dfd;
	private ServiceDescription sd;
	private Logger log;

	@Override
	public EndState getEndState() {
		return gsAgentFound ? EndState.SUCCESSFUL : null;
	}

	@Override
	public void init(PlanInstance planInstance) {
		bfBase = planInstance.getBeliefBase();
		dfd = new DFAgentDescription();
		sd  = new ServiceDescription();
		sd.setType(ID.GlobalScheduler.Service);
		dfd.addServices(sd);
		log = LogManager.getLogger();
	}

	@Override
	public void action() {
		switch(step) {
		case 0:
			DFAgentDescription[] result;
			try {
				result = DFService.search(myAgent, dfd);
				if (result.length > 0) {
					gsa = result[0].getName();
					step = 3;
				} else {
					step = 1;
				}
			} catch (FIPAException e) {
				e.printStackTrace();
			}
			break;
		case 1:
			SearchConstraints sc = new SearchConstraints();
			sc.setMaxResults(new Long(1));

			myAgent.send(DFService.createSubscriptionMessage(myAgent, myAgent.getDefaultDF(), 
					dfd, sc));
			step ++;
			break;
		case 2:
			ACLMessage msg = myAgent.receive(
					MessageTemplate.MatchSender(myAgent.getDefaultDF()));

			if (msg != null) {
				try {
					DFAgentDescription[] dfds = DFService.decodeNotification(msg.getContent());

					if (dfds.length > 0) {
						gsa = dfds[0].getName();
						step++;
					}
				}
				catch (Exception ex) {}
			} else {
				block();
			}

			break;
		case 3:
			log.info("GSA found  : " + gsa);
			bfBase.updateBelief(ID.Customer.BeliefBaseConst.gsAgent, gsa);
			gsAgentFound = true;
			((BDIAgent)myAgent).addGoal(new SubscribeToZonesGoal());
			break;
		}
	}

	@Override
	public boolean done() {
		return gsAgentFound;
	}

}
