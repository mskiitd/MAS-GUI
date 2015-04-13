package mas.globalSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import mas.globalSchedulingproxy.goal.SubscribeToCustomerGsaGoal;
import mas.globalSchedulingproxy.goal.SubscribeToLsaGoal;
import mas.util.ID;
import mas.util.SubscribeID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.core.BDIAgent;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class LookUpAgentsGsaPlan extends CyclicBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private int step = 0;
	private BeliefBase bfBase;
	private AID customer;
	private AID lsa;
	private DFAgentDescription dfd, dfd2;
	private ArrayList<SubscribeID> listCustomer;
	private ArrayList<SubscribeID> listLsa;
	private Logger log;
	private SearchConstraints sc;
	private ServiceDescription sdCustomer;
	private ServiceDescription sdLsa;

	@Override
	public EndState getEndState() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(PlanInstance planInstance) {

		bfBase = planInstance.getBeliefBase();
		log = LogManager.getLogger();

		listCustomer = (ArrayList<SubscribeID>) bfBase.
				getBelief(ID.GlobalScheduler.BeliefBaseConst.customerList).
				getValue();
		listLsa = (ArrayList<SubscribeID>) bfBase.
				getBelief(ID.GlobalScheduler.BeliefBaseConst.lsaList).
				getValue();

		dfd = new DFAgentDescription();
		sdCustomer  = new ServiceDescription();
		sdLsa = new ServiceDescription();

		sdCustomer.setType(ID.Customer.Service);
		dfd.addServices(sdCustomer);

		sdLsa.setType(ID.LocalScheduler.Service);
		dfd.addServices(sdLsa);

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
						if(ID.Customer.Service.equals(service)) {
							customer = result[i].getName();

							log.info("Customer found : " + customer);
							if(!listCustomer.contains(customer)) {
								SubscribeID customerId = new SubscribeID(customer,false);

								listCustomer.add(customerId);
								bfBase.updateBelief(ID.GlobalScheduler.BeliefBaseConst.customerList, listCustomer);
								((BDIAgent)myAgent).addGoal(new SubscribeToCustomerGsaGoal());
							} else {
								log.info("duplicate customer agent registering");
							}
						} else if(ID.LocalScheduler.Service.equals(service)) {
							lsa = result[i].getName();

							log.info("LSA found  : " + lsa);
							if(!listLsa.contains(lsa)) {
								SubscribeID lsaId = new SubscribeID(lsa,false);
								listLsa.add(lsaId);
								bfBase.updateBelief(ID.GlobalScheduler.BeliefBaseConst.lsaList, listLsa);

								((BDIAgent)myAgent).addGoal(new SubscribeToLsaGoal());
							}else {
								log.info("duplicate LSA agent registering");
							}
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
			
			DFAgentDescription dfd3 = new DFAgentDescription();
			ServiceDescription s1 = new ServiceDescription();
			s1.setType(ID.LocalScheduler.Service);
			dfd3.addServices(s1);
			myAgent.send(DFService.createSubscriptionMessage(myAgent, myAgent.getDefaultDF(), 
					dfd3, sc));
			step = 2;
			break;

		case 2:
			ACLMessage msg = myAgent.receive(
					MessageTemplate.MatchSender(myAgent.getDefaultDF()));
			log.info("step 2 : " + msg);
			if (msg != null) {
				try {
					DFAgentDescription[] dfds = DFService.decodeNotification(msg.getContent());

					log.info("dfd length : " + dfds.length);
					if (dfds.length > 0) {
						String service = ((ServiceDescription) dfds[0].getAllServices().next()).getType();
						if(ID.Customer.Service.equals(service)) {
							customer = dfds[0].getName();
							step  = 5;
						} else if(ID.LocalScheduler.Service.equals(service)) {
							lsa = dfds[0].getName();
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
			log.info("Customer found  : " + customer);
			if(!listCustomer.contains(customer)) {
				SubscribeID customerId = new SubscribeID(customer,false);

				listCustomer.add(customerId);
				bfBase.updateBelief(ID.GlobalScheduler.BeliefBaseConst.customerList, listCustomer);
				((BDIAgent)myAgent).addGoal(new SubscribeToCustomerGsaGoal());
			} else {
				log.info("duplicate customer agent registering");
			}
			step = 2;
			break;

		case 6:
			log.info("LSA found  : " + lsa);
			if(!listLsa.contains(lsa)) {
				SubscribeID lsaId = new SubscribeID(lsa,false);
				listLsa.add(lsaId);
				bfBase.updateBelief(ID.GlobalScheduler.BeliefBaseConst.lsaList, listLsa);

				((BDIAgent)myAgent).addGoal(new SubscribeToLsaGoal());
			}else {
				log.info("duplicate LSA agent registering");
			}
			step = 2;
			break;
		}
	}

}
