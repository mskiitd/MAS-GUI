package mas.maintenanceproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.machineproxy.SimulatorInternals;
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
	private RepairKit solver;
	private AID bba;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		bfBase = pInstance.getBeliefBase();
		msgTemplate = MessageTemplate.MatchConversationId(
				MessageIds.msgmaintenanceStart);
		solver = new RepairKit();
		
		this.bba = (AID) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgentAID).
				getValue();
	}

	@Override
	public void action() {
		switch(step){
		case 0:
			msg = myAgent.receive(msgTemplate);
			if(msg != null) {
				try {
					machine = (SimulatorInternals) msg.getContentObject();
					solver.setMachine(machine);
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
			
			String maintenanceData = solver.getPreventiveMaintenanceData();
			
			ZoneDataUpdate maintenanceStartData = new ZoneDataUpdate.Builder(ID.Maintenance.ZoneData.prevMaintData)
				.value(maintenanceData).Build();

			AgentUtil.sendZoneDataUpdate(this.bba ,maintenanceStartData, myAgent);
			
			log.info("sending maintenance job data");
			break;
		}
	}

	@Override
	public boolean done() {
		return step >= 2;
	}
}
