package mas.machineproxy.behaviors;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import mas.machineproxy.Simulator;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.SubscriptionForm;

public class SubscribeToMaintMachineBehavior extends OneShotBehaviour {

	private static final long serialVersionUID = 1L;
	private AID maintenance;
	private AID blackBoard;
	private Simulator machineSimulator;

	public SubscribeToMaintMachineBehavior(Simulator sim) {
		this.machineSimulator = sim;
		maintenance = machineSimulator.getMaintAgent();
		blackBoard = Simulator.blackboardAgent;
	}

	@Override
	public void action() {
		SubscriptionForm subform = new SubscriptionForm();
		String[] lMaintenanceParams = {ID.Maintenance.ZoneData.correctiveMaintdata,
				ID.Maintenance.ZoneData.prevMaintData };
		subform.AddSubscriptionReq(maintenance, lMaintenanceParams);
		AgentUtil.subscribeToParam(myAgent, blackBoard, subform);
	}

}
