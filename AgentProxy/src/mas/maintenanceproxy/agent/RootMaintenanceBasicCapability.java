package mas.maintenanceproxy.agent;

import jade.core.AID;

import java.util.HashSet;
import java.util.Set;

import mas.machineproxy.SimulatorInternals;
import mas.maintenanceproxy.classes.PMaintenance;
import mas.maintenanceproxy.goal.LookUpAgentMaintGoal;
import mas.maintenanceproxy.goal.ManualMachineRepairGoal;
import mas.maintenanceproxy.goal.MaintenanceStartSendInfoGoal;
import mas.maintenanceproxy.goal.PeriodicPreventiveMaintenanceGoal;
import mas.maintenanceproxy.goal.RegisterMaintenanceAgentServiceGoal;
import mas.maintenanceproxy.goal.RegisterMaintenanceAgentToBlackboardGoal;
import mas.maintenanceproxy.goal.SendCorrectiveRepairDataGoal;
import mas.maintenanceproxy.goal.SubscribeToLsaMaintGoal;
import mas.maintenanceproxy.goal.SubscribeToMachineMaintGoal;
import mas.maintenanceproxy.goal.machineHealthCheckGoal;
import mas.maintenanceproxy.gui.MaintenanceGUI;
import mas.maintenanceproxy.plan.LookUpAgentsMaintPlan;
import mas.maintenanceproxy.plan.MaintenanceStartSendInfoPlan;
import mas.maintenanceproxy.plan.ManualMachineRepairPlan;
import mas.maintenanceproxy.plan.PeriodicPreventiveMaintenancePlan;
import mas.maintenanceproxy.plan.RegisterMaintenanceAgentServicePlan;
import mas.maintenanceproxy.plan.RegisterMaintenanceAgentToBlackboardPlan;
import mas.maintenanceproxy.plan.SendCorrectiveRepairDataPlan;
import mas.maintenanceproxy.plan.SubscribeToLsaMaintPlan;
import mas.maintenanceproxy.plan.SubscribeToMachineMaintPlan;
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

		Belief<AID> bboard = new TransientBelief<AID>(ID.Maintenance.BeliefBaseConst.blackboardAgent);

		Belief<AID> gsAgent = new TransientBelief<AID>(ID.Maintenance.BeliefBaseConst.globalSchAgentAID);
		
		Belief<AID> lsaAgent = new TransientBelief<AID>(ID.Maintenance.BeliefBaseConst.lsAgent);
		
		Belief<AID> machine = new TransientBelief<AID>(ID.Maintenance.BeliefBaseConst.machineAgent);

		Belief<SimulatorInternals> myMachine = new TransientBelief<SimulatorInternals>(
				ID.Maintenance.BeliefBaseConst.machineHealth);
		
		Belief<PMaintenance> maintJob  = new TransientBelief<PMaintenance>(
				ID.Maintenance.BeliefBaseConst.preventiveMaintJob);

		Belief<Double> CorrectiveRepair = new TransientBelief<Double>(
				ID.Maintenance.BeliefBaseConst.correctiveRepairData);

		Belief<MaintenanceGUI> gui = new TransientBelief<MaintenanceGUI>(
				ID.Maintenance.BeliefBaseConst.gui_maintenance);
		
		Belief<PMaintenance> pmStatus = new TransientBelief<PMaintenance>(
				ID.Maintenance.BeliefBaseConst.prevMaintFromMachine);
		
		gui.setValue(null);
		CorrectiveRepair.setValue(null);
		lsaAgent.setValue(null);
		machine.setValue(null);
		
		myMachine.setValue(new SimulatorInternals());

		beliefs.add(bboard);
		beliefs.add(myMachine);
		beliefs.add(gsAgent);
		beliefs.add(maintJob);
		beliefs.add(CorrectiveRepair);
		beliefs.add(gui);
		beliefs.add(pmStatus);
		beliefs.add(machine);
		beliefs.add(lsaAgent);
			
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

		plans.add(new SimplePlan(ManualMachineRepairGoal.class,
				ManualMachineRepairPlan.class));

		plans.add(new SimplePlan(MaintenanceStartSendInfoGoal.class,
				MaintenanceStartSendInfoPlan.class));

		plans.add(new SimplePlan(SendCorrectiveRepairDataGoal.class,
				SendCorrectiveRepairDataPlan.class));

		plans.add(new SimplePlan(PeriodicPreventiveMaintenanceGoal.class,
				PeriodicPreventiveMaintenancePlan.class));
		
		plans.add(new SimplePlan(LookUpAgentMaintGoal.class, LookUpAgentsMaintPlan.class));
		
		plans.add(new SimplePlan(SubscribeToLsaMaintGoal.class, SubscribeToLsaMaintPlan.class));
		
		plans.add(new SimplePlan(SubscribeToMachineMaintGoal.class, SubscribeToMachineMaintPlan.class));
		
		return plans;
	}	

	@Override
	protected void setup() {
		myAgent.addGoal(new RegisterMaintenanceAgentServiceGoal());
		myAgent.addGoal(new RegisterMaintenanceAgentToBlackboardGoal());
		myAgent.addGoal(new LookUpAgentMaintGoal());
		myAgent.addGoal(new machineHealthCheckGoal());
		myAgent.addGoal(new ManualMachineRepairGoal());
		myAgent.addGoal(new PeriodicPreventiveMaintenanceGoal());
	}
}
