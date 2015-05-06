package mas.maintenanceproxy.plan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.maintenanceproxy.classes.MaintStatus;
import mas.maintenanceproxy.classes.PMaintenance;
import mas.maintenanceproxy.gui.MaintenanceGUI;
import mas.util.ID;
import mas.util.MessageIds;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * @author Anand Prajapati
 * <p>
 *  Plan to receive preventive maintenance confirmation from machine.
 * </p>
 */

public class RecievePreventiveMaintenanceConfirmationPlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private AID blackboard;
	private ACLMessage msg;
	private BeliefBase bfBase;
	private MaintenanceGUI gui;
	private int step = 0;
	private MessageTemplate pmConfirmaton;
	private PMaintenance prevMaint;
	private Logger log;

	public void init(PlanInstance planInstance) {
		this.log = LogManager.getLogger();
		bfBase = planInstance.getBeliefBase();

		this.blackboard = (AID) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgentAID).
				getValue();
		this.pmConfirmaton = MessageTemplate.MatchConversationId(MessageIds.msgMaintConfirmationLSA);
		this.gui = (MaintenanceGUI) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.gui_maintenance).
				getValue();
	}

	@Override
	public void action() {

		switch(step) {
		case 0:
			msg = myAgent.receive(pmConfirmaton);
			if(msg != null) {
				try {
					prevMaint = (PMaintenance) msg.getContentObject();
//					log.info("Received pm confirmation");
					step = 1;
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			else {
				block();
			}
			break;

		case 1:
			if(prevMaint.getMaintStatus() == MaintStatus.COMPLETE) { 
				if(gui != null) {
					gui.addMaintJobToDisplay(prevMaint);
				}
				step = 0;
			}
			break;
		}
	}

	@Override
	public boolean done() {
		return step > 2;
	}

	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

}
