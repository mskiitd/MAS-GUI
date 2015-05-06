package mas.localSchedulingproxy.plan;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

import mas.machineproxy.gui.MachineGUI;
import mas.maintenanceproxy.classes.PMaintenance;
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
 * 
 * Plan to receive preventive maintenance job from the maintenance agent.
 *
 */
public class ReceiveMaintenanceJobPlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private PMaintenance comingMaintJob;
	private Logger log;
	private int step = 0;
	private MessageTemplate pmMsgTemplate;
	private ACLMessage msg;
	private BeliefBase bfBase;
	private ArrayList<PMaintenance> maintJobList;
	private MachineGUI gui;

	@SuppressWarnings("unchecked")
	@Override
	public void init(PlanInstance pInstance) {

		log = LogManager.getLogger();
		pmMsgTemplate = MessageTemplate.MatchConversationId(
				MessageIds.msgpreventiveMaintJob);

		this.bfBase = pInstance.getBeliefBase();
		
		maintJobList = (ArrayList<PMaintenance>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.preventiveJobsQueue).
				getValue();

		this.gui = (MachineGUI) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.gui_machine).
				getValue();
	}

	@Override
	public void action() {
		switch(step) {
		case 0 :
			msg = myAgent.receive(pmMsgTemplate);
			if(msg != null) {

				try {
					comingMaintJob = (PMaintenance) msg.getContentObject();
					maintJobList.add(comingMaintJob);

					bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.preventiveJobsQueue, maintJobList);
//					log.info("Maintenance Job Received");

					if(gui != null) {
						gui.maintJobArrived();
//						if(maintJobList.size() > 1) {
//							gui.pendingMaintPopUp();
//						}
					}

				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			else 
				block();
			break;
		}
	}

	@Override
	public boolean done() {
		return false;
	}

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}
}
