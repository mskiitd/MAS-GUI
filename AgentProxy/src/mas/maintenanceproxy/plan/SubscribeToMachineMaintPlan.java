package mas.maintenanceproxy.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.SubscriptionForm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class SubscribeToMachineMaintPlan  extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private AID machine;
	private AID blackBoard;
	private boolean done = false;
	private Logger log;

	@Override
	public EndState getEndState() {
		return (done ? EndState.SUCCESSFUL : null );
	}

	@Override
	public void init(PlanInstance planInstance) {
		bfBase = planInstance.getBeliefBase();
		machine = (AID) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.machine).
				getValue();
		blackBoard = (AID) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgent).
				getValue();
		log = LogManager.getLogger();
	}

	@Override
	public void action() {
		
		SubscriptionForm subform = new SubscriptionForm();
		
		String[] machineParams = { ID.Machine.ZoneData.myHealth,
				ID.Machine.ZoneData.machineFailures, ID.Machine.ZoneData.maintenanceStart,
				ID.Machine.ZoneData.inspectionStart };
		
		subform.AddSubscriptionReq(machine, machineParams);
		AgentUtil.subscribeToParam(myAgent, blackBoard, subform);
		done = true;
	}

	@Override
	public boolean done() {
		return done;
	}

}
