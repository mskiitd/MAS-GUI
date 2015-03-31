package mas.maintenanceproxy.agent;

import jade.core.AID;
import java.util.HashSet;
import java.util.Set;
import mas.maintenanceproxy.goal.CorrectiveMachineComponentsRepairGoal;
import mas.maintenanceproxy.goal.MaintenanceStartSendInfoGoal;
import mas.maintenanceproxy.goal.RegisterMaintenanceAgentServiceGoal;
import mas.maintenanceproxy.goal.RegisterMaintenanceAgentToBlackboardGoal;
import mas.maintenanceproxy.goal.SendCorrectiveRepairDataGoal;
import mas.maintenanceproxy.goal.machineHealthCheckGoal;
import mas.maintenanceproxy.plan.MaintenanceStartSendInfoPlan;
import mas.maintenanceproxy.plan.ManualMachineRepairPlan;
import mas.maintenanceproxy.plan.RegisterMaintenanceAgentServicePlan;
import mas.maintenanceproxy.plan.RegisterMaintenanceAgentToBlackboardPlan;
import mas.maintenanceproxy.plan.SendCorrectiveRepairDataPlan;
import mas.maintenanceproxy.plan.machineHealthCheckPlan;
import mas.util.ID;
import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
import bdi4jade.core.BeliefBase;
import bdi4jade.core.Capability;
import bdi4jade.core.PlanLibrary;
import bdi4jade.plan.Plan;
import bdi4jade.util.plan.SimplePlan;

public class RootMaintenanceBasicCapability extends Capability{

	private static final long serialVersionUID = 1L;

	public RootMaintenanceBasicCapability() {
		super(new BeliefBase(getBeliefs()), new PlanLibrary(getPlans()));
	}

	public static Set<Belief<?>> getBeliefs() {
		Set<Belief<?>> beliefs = new HashSet<Belief<?>>();

		Belief<AID> bboard = new TransientBelief<AID>(
				ID.Maintenance.BeliefBaseConst.blackboardAgentAID);

		Belief<AID> myMachine = new TransientBelief<AID>(
				ID.Maintenance.BeliefBaseConst.machine);

		Belief<AID> mygsAgent = new TransientBelief<AID>(
				ID.Maintenance.BeliefBaseConst.globalSchAgentAID);

		Belief<AID> maintJob  = new TransientBelief<AID>(
				ID.Maintenance.BeliefBaseConst.maintenanceJob);

		Belief<Double> CorrectiveRepair = new TransientBelief<Double>(
				ID.Maintenance.BeliefBaseConst.correctiveRepairData);

		CorrectiveRepair.setValue(null);
		
		beliefs.add(bboard);
		beliefs.add(myMachine);
		beliefs.add(mygsAgent);
		beliefs.add(maintJob);
		beliefs.add(CorrectiveRepair);

		return beliefs;
	}

	public static Set<Plan> getPlans() {
		Set<Plan> plans = new HashSet<Plan>();

		plans.add(new SimplePlan(RegisterMaintenanceAgentServiceGoal.class,
				RegisterMaintenanceAgentServicePlan.class));

		plans.add(new SimplePlan(RegisterMaintenanceAgentToBlackboardGoal.class,
				RegisterMaintenanceAgentToBlackboardPlan.class));

		plans.add(new SimplePlan(machineHealthCheckGoal.class,
				machineHealthCheckPlan.class));

		plans.add(new SimplePlan(CorrectiveMachineComponentsRepairGoal.class,
				ManualMachineRepairPlan.class));

		plans.add(new SimplePlan(MaintenanceStartSendInfoGoal.class,
				MaintenanceStartSendInfoPlan.class));

		plans.add(new SimplePlan(SendCorrectiveRepairDataGoal.class,
				SendCorrectiveRepairDataPlan.class));

		return plans;
	}	

	@Override
	protected void setup() {
		myAgent.addGoal(new RegisterMaintenanceAgentServiceGoal());
		myAgent.addGoal(new RegisterMaintenanceAgentToBlackboardGoal());
		myAgent.addGoal(new machineHealthCheckGoal());
		myAgent.addGoal(new CorrectiveMachineComponentsRepairGoal());
		myAgent.addGoal(new MaintenanceStartSendInfoGoal());
	}
}
