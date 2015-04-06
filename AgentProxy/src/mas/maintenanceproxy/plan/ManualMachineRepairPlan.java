package mas.maintenanceproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.machineproxy.SimulatorInternals;
import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import mas.maintenanceproxy.gui.MaintenanceGUI;
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
 */

public class ManualMachineRepairPlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private AID blackboard;
	private ACLMessage msg;
	private SimulatorInternals failedMachine;
	private BeliefBase bfBase;
	private Logger log;
	private MaintenanceGUI gui;

	public class correctiveBehavior extends Behaviour {

		private static final long serialVersionUID = 1L;
		private int step = 0;

		MessageTemplate machineFailureMSG = MessageTemplate.
				MatchConversationId(MessageIds.msgmachineFailures);

		@Override
		public void action() {
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
				if(gui != null) {
					gui.showRepairTimeInput();
					//					ZoneDataUpdate correctiveRepairUpdate = new ZoneDataUpdate.
					//							Builder(ID.Maintenance.ZoneData.correctiveMaintdata).
					//							value(correctiveMaintData).
					//							Build();
					//
					//					AgentUtil.sendZoneDataUpdate(blackboard ,correctiveRepairUpdate, myAgent);

					step = 2;
				}
			}
		}

		@Override
		public boolean done() {
			return step >= 2;
		}
	}

	@Override
	public void action() {
		myAgent.addBehaviour(new correctiveBehavior());
	}

	@Override
	public boolean done() {
		return true;
	}

	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	public void init(PlanInstance planInstance) {
		log = LogManager.getLogger();
		bfBase = planInstance.getBeliefBase();

		this.blackboard = (AID) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgentAID).
				getValue();

		gui = (MaintenanceGUI) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.gui_maintenance).
				getValue();
	}
}
