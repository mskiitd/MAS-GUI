package mas.localSchedulingproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
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

public class LookUpAgentsLsaPlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private int step = 0;
	private BeliefBase bfBase;
	private DFAgentDescription dfdGsa;
	private DFAgentDescription dfdMaint;
	private DFAgentDescription dfdMachine;
	private AID machine;
	private AID gsa;
	private AID maintenance;
	private Logger log;

	private SearchConstraints sc;

	private ServiceDescription sdGsa;
	private ServiceDescription sdMaintenance;
	private ServiceDescription sdMachine;

	private boolean gsaFound = false;
	private boolean maintFound = false;
	private boolean machineFound = false;

	private String mySurname;

	@Override
	public EndState getEndState() {
		return (gsaFound && maintFound && machineFound) ? EndState.SUCCESSFUL : null;
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

		dfdMaint = new DFAgentDescription();
		dfdGsa = new DFAgentDescription();
		dfdMachine = new DFAgentDescription();

		sdGsa  = new ServiceDescription();
		sdMaintenance = new ServiceDescription();
		sdMachine = new ServiceDescription();

		sdGsa.setType(ID.GlobalScheduler.Service);
		dfdGsa.addServices(sdGsa);

		sdMaintenance.setType(ID.Maintenance.Service);
		dfdMaint.addServices(sdMaintenance);

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
			mySurname = name.substring(name.lastIndexOf("#") + 1, name.length());

			DFAgentDescription[] resultGsa, resultMaint, resultMachine;
			try {
				resultGsa = DFService.search(myAgent, dfdGsa);
				if (resultGsa.length > 0) {
					gsa = resultGsa[0].getName();
					step  = 5;
				}

				resultMaint = DFService.search(myAgent, dfdMaint);
				if (resultMaint.length > 0) {
					for(int i = 0; i < resultMaint.length; i ++) {

						maintenance = resultMaint[i].getName();

						String maintName = maintenance.getLocalName();
						int hashIdx = maintName.lastIndexOf("#");
						String maintSurName = maintName.substring(hashIdx + 1, maintName.length());
						if(mySurname.equals(maintSurName)) {
							log.info("Maintenance found  : " + maintenance);
							maintFound = true;
							bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.maintAgent, maintenance);
							((BDIAgent)myAgent).addGoal(new SubscribeToMaintenanceLsaGoal());
							break;
						}
					}
				}

				resultMachine = DFService.search(myAgent, dfdMachine);
				if (resultMachine.length > 0) {
					for(int i = 0; i < resultMachine.length; i ++) {

						machine = resultMachine[i].getName();
						String machineName = machine.getLocalName();
						int hashIdx = machineName.lastIndexOf("#");
						String machineSurName = machineName.substring(hashIdx + 1, machineName.length());
						if(mySurname.equals(machineSurName)) {
							machineFound = true;
							log.info("machine found  : " + machine);
							bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.machine, machine);
							((BDIAgent)myAgent).addGoal(new SubscribeToMachineLsaGoal());
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
			if(!gsaFound) {
				myAgent.send(DFService.createSubscriptionMessage(myAgent, myAgent.getDefaultDF(), 
						dfdGsa, sc));
			}

			if(!maintFound) {
				myAgent.send(DFService.createSubscriptionMessage(myAgent, myAgent.getDefaultDF(), 
						dfdMaint, sc));
			}

			if(!machineFound) {
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

						if(ID.GlobalScheduler.Service.equals(service)) {
							gsa = dfds[0].getName();
							step  = 5;

						} else if(ID.Maintenance.Service.equals(service)) {
							maintenance = dfds[0].getName();

							String maintName = maintenance.getLocalName();
							int hashIdx = maintName.lastIndexOf("#");
							String maintSurName = maintName.substring(hashIdx + 1, maintName.length());

							if(mySurname.equals(maintSurName)) {
								step = 6;
							}

						} else if(ID.Machine.Service.equals(service)) {
							machine = dfds[0].getName();
							String machineName = machine.getLocalName();
							int hashIdx = machineName.lastIndexOf("#");

							String machineSurName = machineName.substring(hashIdx + 1, machineName.length());
							if(mySurname.equals(machineSurName)) {
								step = 7;
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
			log.info("GSA found  : " + gsa);
			gsaFound = true;
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.globalSchAgent, gsa);
			((BDIAgent)myAgent).addGoal(new SubscribeToGsaLsaGoal());
			step = 2;
			break;

		case 6:
			log.info("Maintenance found  : " + maintenance);
			maintFound = true;
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.globalSchAgent, maintenance);
			((BDIAgent)myAgent).addGoal(new SubscribeToMaintenanceLsaGoal());
			step = 2;
			break;

		case 7:
			log.info("Machine found  : " + machine);
			gsaFound = true;
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.machine, machine);
			((BDIAgent)myAgent).addGoal(new SubscribeToMachineLsaGoal());
			step = 2;
			break;
		}
	}

	@Override
	public boolean done() {
		return gsaFound && maintFound && machineFound;
	}

}