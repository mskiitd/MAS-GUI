package mas.maintenanceproxy.plan;

import jade.core.behaviours.Behaviour;
import mas.maintenanceproxy.behavior.PeriodicMaintenanceTickerBehavior;
import mas.maintenanceproxy.gui.MaintenanceGUI;
import mas.util.ID;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * 
 * @author Anand Prajapati
 * <p>
 * Plan to generate and send periodic preventive maintenance activity to the machine.
 * This add a ticker behavior which periodically generates and sends the maintenance activity for machine.
 * </p>
 */

public class PeriodicPreventiveMaintenancePlan extends Behaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private boolean done = false;
	private PeriodicMaintenanceTickerBehavior maintenance;
	private MaintenanceGUI gui;
	private long maintPeriod;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {

		this.bfBase = pInstance.getBeliefBase();
		gui = (MaintenanceGUI) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.gui_maintenance).
				getValue();
		maintPeriod = (long) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.maintenancePeriod).
				getValue();

		maintenance = new PeriodicMaintenanceTickerBehavior(myAgent, maintPeriod, bfBase);
	}

	@Override
	public void action() {
		if(gui != null) {
			myAgent.addBehaviour(maintenance);
			gui.setNextMaintTime(maintPeriod);
			done = true;
		} else {
			block(100);
			gui = (MaintenanceGUI) bfBase.
					getBelief(ID.Maintenance.BeliefBaseConst.gui_maintenance).
					getValue();
		}

	}

	@Override
	public boolean done() {
		return done;
	}

}
