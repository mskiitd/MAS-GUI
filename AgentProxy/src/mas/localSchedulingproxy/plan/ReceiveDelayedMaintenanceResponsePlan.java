package mas.localSchedulingproxy.plan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import mas.machineproxy.gui.MachineGUI;
import mas.maintenanceproxy.classes.MaintenanceResponse;
import mas.util.ID;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class ReceiveDelayedMaintenanceResponsePlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private MaintenanceResponse response;
	private boolean done = false;
	private MachineGUI gui;
	private Logger log;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		
		log = LogManager.getLogger();

		ACLMessage msg = ((MessageGoal) pInstance.getGoal()).getMessage();

		try {
			response = (MaintenanceResponse) msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		bfBase = pInstance.getBeliefBase();

		gui = (MachineGUI) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.gui_machine).
				getValue();
		log.info("Maintenance respose received");
	}

	@Override
	public void action() {
		if(response != null) {
			
			if(gui != null) {
				gui.delayedMaintWarning(response);
			}
		}

		done = true;
	}

	@Override
	public boolean done() {
		return done;
	}
}
