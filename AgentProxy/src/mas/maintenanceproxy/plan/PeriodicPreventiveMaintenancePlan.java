package mas.maintenanceproxy.plan;

import jade.core.behaviours.Behaviour;
import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import mas.maintenanceproxy.behavior.PeriodicMaintenanceTickerBehavior;
import mas.maintenanceproxy.gui.MaintenanceGUI;
import mas.util.ID;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class PeriodicPreventiveMaintenancePlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private boolean done = false;
	private PeriodicMaintenanceTickerBehavior maintenance;
	private MaintenanceGUI gui;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {

		this.bfBase = pInstance.getBeliefBase();
		gui = (MaintenanceGUI) bfBase.getBelief(ID.Maintenance.BeliefBaseConst.gui_maintenance).getValue();

		maintenance = new PeriodicMaintenanceTickerBehavior(myAgent,
				LocalMaintenanceAgent.prevMaintPeriod, bfBase);
	}

	@Override
	public void action() {
		if(gui != null) {
			myAgent.addBehaviour(maintenance);
			gui.setNextMaintTime(LocalMaintenanceAgent.prevMaintPeriod);
			done = true;
		} else {
			block(100);
			gui = (MaintenanceGUI) bfBase.getBelief(ID.Maintenance.BeliefBaseConst.gui_maintenance).getValue();
		}
		
	}

	@Override
	public boolean done() {
		return done;
	}

}
