package mas.maintenanceproxy.plan;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.MessageTemplate;
import mas.maintenance.behavior.AutoMaintenanceTickerBehavior;
import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * @author Anand Prajapati
 */

public class PreventiveMaintenancePlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private AutoMaintenanceTickerBehavior autoMaintenance;

	MessageTemplate mt = MessageTemplate.MatchConversationId("MJStart");

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		
		this.bfBase = pInstance.getBeliefBase();
	}

	@Override
	public void action() {
		autoMaintenance = new AutoMaintenanceTickerBehavior(myAgent,
				LocalMaintenanceAgent.prevMaintPeriod, this.bfBase);
		
		myAgent.addBehaviour(autoMaintenance);
		
	}

	@Override
	public boolean done() {
		return true;
	}

}