package mas.maintenanceproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.machineproxy.SimulatorInternals;
import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import mas.maintenanceproxy.gui.MaintenanceGUI;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * 
 * @author Anand Prajapati
 *
 */
public class MaintenanceStartSendInfoPlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private Logger log;
	private BeliefBase bfBase;
	private MessageTemplate msgTemplate;
	private int step = 0;
	private ACLMessage msg;
	private SimulatorInternals machine;
	private AID bba;
	private MaintenanceGUI gui;

	@Override
	public EndState getEndState() {
		return (step >= 2 ? EndState.SUCCESSFUL : null);
	}

	@Override
	public void init(PlanInstance pInstance) {
		bfBase = pInstance.getBeliefBase();
		msgTemplate = MessageTemplate.MatchConversationId(
				MessageIds.msgmaintenanceStart);

		this.bba = (AID) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgentAID).
				getValue();

		gui = (MaintenanceGUI) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.gui_maintenance).
				getValue();
	}

	@Override
	public void action() {
		switch(step) {
		case 0:
			msg = myAgent.receive(msgTemplate);
			if(msg != null) {
				try {
					machine = (SimulatorInternals) msg.getContentObject();
					step++;
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			else{
				block();
			}
			break;
		case 1:

			gui.showMaintenanceStartNotification();
			//			String maintenanceData = "prev_maint_data";
			//			
			//			ZoneDataUpdate maintenanceStartData = new ZoneDataUpdate.Builder(ID.Maintenance.ZoneData.prevMaintData)
			//				.value(maintenanceData).Build();
			//
			//			AgentUtil.sendZoneDataUpdate(this.bba ,maintenanceStartData, myAgent);

			log.info("sending maintenance job data");
			// update the maintenance performed in the maintenance GUI
			break;
		}
	}

	@Override
	public boolean done() {
		return step >= 2;
	}
}
