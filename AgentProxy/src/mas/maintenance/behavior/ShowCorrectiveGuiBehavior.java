package mas.maintenance.behavior;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.machineproxy.SimulatorInternals;
import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import mas.maintenanceproxy.gui.CorrectiveMaintenancePanel;
import mas.maintenanceproxy.gui.MaintenanceGUI;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;

public class ShowCorrectiveGuiBehavior extends Behaviour {

	private static final long serialVersionUID = 1L;

	ACLMessage msg;
	private SimulatorInternals failedMachine;
	private AID blackboard;
	private Logger log;
	private int step = 0;

	MessageTemplate machineFailureMSG = MessageTemplate.
			MatchConversationId(MessageIds.msgmachineFailures);

	public ShowCorrectiveGuiBehavior(AID blackboard) {
		this.blackboard = blackboard;
	}

	@Override
	public void action() {
		
		if(log == null) {
			log = LogManager.getLogger();
		}
		
		switch(step) {
		case 0:
			msg = myAgent.receive(machineFailureMSG);
			if(msg != null) {
				try {
					log.info("recieved machine's failure msg");
					failedMachine = (SimulatorInternals) msg.getContentObject();
					step ++;
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			else {
				block();
			}
			break;

		case 1:
			LocalMaintenanceAgent.mgui.showRepairTimeInput();
			step = 2;
		}
	}

	@Override
	public boolean done() {
		return step >= 2;
	}
}
