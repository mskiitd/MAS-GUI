package mas.maintenanceproxy.plan;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.machineproxy.SimulatorInternals;
import mas.util.ID;
import mas.util.MessageIds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * @author Anand Prajapati
 * <p>
 * Plan to check health of machine.
 * Health check implies checking age and life of components of machine.
 * </p>
 */
public class machineHealthCheckPlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private SimulatorInternals myMachine;
	private ACLMessage msg;
	private int step = 0;
	private Logger log;
	private MessageTemplate machineHealth;

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();
		myMachine = null;
		machineHealth = MessageTemplate.MatchConversationId(MessageIds.msgmyHealth);
	}

	@Override
	public void action() {

		switch(step) {
		case 0:
			msg = myAgent.receive(machineHealth);
			if(msg != null) {
				try {
					myMachine = (SimulatorInternals) msg.getContentObject();
					bfBase.updateBelief(ID.Maintenance.BeliefBaseConst.machineHealth, myMachine);
					log.info("updating belief base of machine's health : " + myMachine );
					step = 1;
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			else {
				block();
			}
			break;
		}
	}

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public boolean done() {
		return false;
	}
}
