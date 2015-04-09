package mas.maintenanceproxy.plan;

import com.alee.log.Log;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.maintenanceproxy.classes.MaintStatus;
import mas.maintenanceproxy.classes.PMaintenance;
import mas.util.ID;
import mas.util.MessageIds;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class RecievePreventiveMaintenanceConfirmationPlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private AID blackboard;
	private ACLMessage msg;
	private BeliefBase bfBase;
	private int step = 0;
	private MessageTemplate pmConfirmaton;
	private PMaintenance prevMaint;

	public void init(PlanInstance planInstance) {
		bfBase = planInstance.getBeliefBase();

		this.blackboard = (AID) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgentAID).
				getValue();
		this.pmConfirmaton = MessageTemplate.MatchConversationId(MessageIds.msgPrevMaintConfirmation);
	}

	@Override
	public void action() {

		switch(step) {
		case 0:
			msg = myAgent.receive(pmConfirmaton);
			if(msg != null) {
				try {
					prevMaint = (PMaintenance) msg.getContentObject();
					//					log.info("updating belief base of machine's health : " + myMachine );
					step = 1;
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			else {
				block();
			}

		case 1:
			if(prevMaint.getMaintStatus() == MaintStatus.COMPLETE) { 
				Log.info("done");
				step = 2;
			}
			break;
		}
	}

	@Override
	public boolean done() {
		return step >= 2;
	}

	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

}
