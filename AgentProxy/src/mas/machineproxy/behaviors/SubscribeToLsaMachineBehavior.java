package mas.machineproxy.behaviors;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import mas.machineproxy.Simulator;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.SubscriptionForm;

public class SubscribeToLsaMachineBehavior extends OneShotBehaviour {

	private static final long serialVersionUID = 1L;
	private AID lsa;
	private AID blackBoard;
	private Simulator machineSimulator;

	public SubscribeToLsaMachineBehavior(Simulator sim) {
		this.machineSimulator = sim;
		lsa =  machineSimulator.getLsAgent();
		blackBoard = Simulator.blackboardAgent;
	}

	@Override
	public void action() {
		SubscriptionForm subform = new SubscriptionForm();
		String[] lSchedulingParams = {ID.LocalScheduler.ZoneData.batchForMachine,
				ID.LocalScheduler.ZoneData.gui_machine, ID.LocalScheduler.ZoneData.maintenanceJobForMachine };
		subform.AddSubscriptionReq(lsa, lSchedulingParams);
		AgentUtil.subscribeToParam(myAgent, blackBoard, subform);

		// Add givemeJobBehavior to agent
		myAgent.addBehaviour(new GiveMeJobBehavior());
	}
}
