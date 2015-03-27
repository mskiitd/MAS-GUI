package mas.maintenanceproxy.plan;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.MessageTemplate;
import mas.maintenance.behavior.PeriodicMaintenanceTickerBehavior;
import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class PeriodicPreventiveMaintenancePlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;

	private PeriodicMaintenanceTickerBehavior maintenance;

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
		maintenance = new PeriodicMaintenanceTickerBehavior(myAgent,
													LocalMaintenanceAgent.prevMaintPeriod,
													bfBase);
		myAgent.addBehaviour(maintenance);
	}

	@Override
	public boolean done() {
		return true;
	}

}
