package mas.maintenanceproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
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

public class LookUpAgentsMaintPlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private int step = 0;
	private BeliefBase bfBase;
	private DFAgentDescription dfdLsa;
	private DFAgentDescription dfdMachine;
	private AID machine;
	private AID lsa;
	private Logger log;

	private SearchConstraints sc;

	private ServiceDescription sdLsa;
	private ServiceDescription sdMachine;

	private boolean machineFound = false;
	private boolean lsaFound = false;

	private String mySurName;

	@Override
	public EndState getEndState() {
		return (lsaFound && machineFound) ? EndState.SUCCESSFUL : null;
	}

	@Override
	public void init(PlanInstance planInstance) {

		bfBase = planInstance.getBeliefBase();
		log = LogManager.getLogger();

		dfdLsa = new DFAgentDescription();
		dfdMachine = new DFAgentDescription();

		sdLsa  = new ServiceDescription();
		sdMachine = new ServiceDescription();

		sdLsa.setType(ID.LocalScheduler.Service);
		dfdLsa.addServices(sdLsa);

		sdMachine.setType(ID.Machine.Service);
		dfdMachine.addServices(sdMachine);

		sc = new SearchConstraints();
		sc.setMaxResults(new Long(1));
	}

	@Override
	public void action() {
		switch(step) {
		case 0:

			String name = myAgent.getLocalName();
			mySurName = name.substring(name.lastIndexOf("#") + 1, name.length());

			DFAgentDescription[] resultLsa, resultMachine;
			try {
				resultLsa = DFService.search(myAgent, dfdLsa);
				if (resultLsa.length > 0) {

					for(int i = 0; i < resultLsa.length; i ++) {
						lsa = resultLsa[i].getName();
						String lsaName = lsa.getLocalName();
						int hashIdx = lsaName.lastIndexOf("#");
						String lsaSurName = lsaName.substring(hashIdx + 1, lsaName.length());

						if(mySurName.equals(lsaSurName)) {
							log.info("Lsa found : " + lsa);
							lsaFound = true;
							bfBase.updateBelief(ID.Maintenance.BeliefBaseConst.lsAgent, lsa);
							((BDIAgent)myAgent).addGoal(new SubscribeToLsaMaintGoal());
							break;
						}

					} 
				}
				resultMachine =  DFService.search(myAgent, dfdMachine);
				if (resultMachine.length > 0) {
					for(int i = 0; i < resultMachine.length; i ++) {
						machine = resultMachine[i].getName();

						String machineName = lsa.getLocalName();
						int hashIdx = machineName.lastIndexOf("#");
						String mSurName = machineName.substring(hashIdx + 1, machineName.length());

						if(mySurName.equals(mSurName)) {
							log.info("machine found  : " + machine);
							machineFound = true;
							bfBase.updateBelief(ID.Maintenance.BeliefBaseConst.machineAgent, machine);
							((BDIAgent)myAgent).addGoal(new SubscribeToMachineMaintGoal());
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
			log.info("step 1 ");

			if(! lsaFound) {
				myAgent.send(DFService.createSubscriptionMessage(myAgent, myAgent.getDefaultDF(), 
						dfdLsa, sc));
			}

			if(! machineFound) {
				myAgent.send(DFService.createSubscriptionMessage(myAgent, myAgent.getDefaultDF(), 
						dfdMachine, sc));
			}
			step = 2;
			break;

		case 2:
			ACLMessage msg = myAgent.receive(
					MessageTemplate.MatchSender(myAgent.getDefaultDF()));
			if (msg != null) {
				try {
					DFAgentDescription[] dfds = DFService.decodeNotification(msg.getContent());

					if (dfds.length > 0) {
						String service = ((ServiceDescription) dfds[0].getAllServices().next()).getType();

						if(ID.LocalScheduler.Service.equals(service)) {
							lsa = dfds[0].getName();

							String lsaName = lsa.getLocalName();
							int hashIdx = lsaName.lastIndexOf("#");
							String lsaSurName = lsaName.substring(hashIdx + 1, lsaName.length());

							if(mySurName.equals(lsaSurName)) {
								step  = 5;
							}

						} else if(ID.Machine.Service.equals(service)) {
							machine = dfds[0].getName();

							String machineName = lsa.getLocalName();
							int hashIdx = machineName.lastIndexOf("#");
							String mSurName = machineName.substring(hashIdx + 1, machineName.length());

							if(mySurName.equals(mSurName)) {
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

	@Override
	public boolean done() {
		return lsaFound && machineFound;
	}
}
