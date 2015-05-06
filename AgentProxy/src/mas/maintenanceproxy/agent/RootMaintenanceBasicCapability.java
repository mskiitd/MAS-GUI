package mas.maintenanceproxy.agent;

import jade.core.AID;

import java.util.HashSet;
import java.util.Set;

import mas.machineproxy.SimulatorInternals;
import mas.maintenanceproxy.classes.PMaintenance;
import mas.maintenanceproxy.goal.LoadConfigMaintGoal;
import mas.maintenanceproxy.goal.ManualMachineRepairGoal;
import mas.maintenanceproxy.goal.MaintenanceStartSendInfoGoal;
import mas.maintenanceproxy.goal.PeriodicPreventiveMaintenanceGoal;
import mas.maintenanceproxy.goal.RegisterMaintenanceAgentServiceGoal;
import mas.maintenanceproxy.goal.RegisterMaintenanceAgentToBlackboardGoal;
import mas.maintenanceproxy.goal.SendCorrectiveRepairDataGoal;
import mas.maintenanceproxy.goal.machineHealthCheckGoal;
import mas.maintenanceproxy.gui.MaintenanceGUI;
import mas.maintenanceproxy.plan.LoadConfigMaintPlan;
import mas.maintenanceproxy.plan.MaintenanceStartSendInfoPlan;
import mas.maintenanceproxy.plan.ManualMachineRepairPlan;
import mas.maintenanceproxy.plan.PeriodicPreventiveMaintenancePlan;
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

/**
 * @author Anand Prajapati
 * <p>
 * Capability of local maintenance agent. Capability consists of beliefs and plans associated with goals of the agent.
 * Each capability has its own belief base.
 * </p>
 *
 */
public class RootMaintenanceBasicCapability extends Capability{

	private static final long serialVersionUID = 1L;

	public RootMaintenanceBasicCapability() {
		super(new BeliefBase(getBeliefs()), new PlanLibrary(getPlans()));
	}

	/** <b> Add belief for </b> <i>
	 * </br> AID of blackboard Agent
	 * </br> Health of machine
	 * </br> AID of GSA
	 * </br> Preventive maintenance job
	 * </br> corrective maintenance repair data for machine
	 * </br> gui of maintenance agent
	 * </br> Status of maintenance activity on machine
	 * </br> Preventive maintenance activity inter-arrival time
	 * </br> Maximum time by which maintenance can be delayed without machine being received any warning
	 * </i>
	 * @return Set of beliefs of capability
	 */
	public static Set<Belief<?>> getBeliefs() {
		Set<Belief<?>> beliefs = new HashSet<Belief<?>>();

		// belief for AID of blackboard Agent
		Belief<AID> bboard = new TransientBelief<AID>(
				ID.Maintenance.BeliefBaseConst.blackboardAgentAID);

		// belief for storing health of machine
		Belief<SimulatorInternals> myMachine = new TransientBelief<SimulatorInternals>(
				ID.Maintenance.BeliefBaseConst.machineHealth);

		// belief for AID of GSA
		Belief<AID> mygsAgent = new TransientBelief<AID>(
				ID.Maintenance.BeliefBaseConst.globalSchAgentAID);

		// belief for Preventive maintenance job
		Belief<PMaintenance> maintJob  = new TransientBelief<PMaintenance>(
				ID.Maintenance.BeliefBaseConst.preventiveMaintJob);

		// belief for corrective maintenance repair data for machine
		Belief<Double> CorrectiveRepair = new TransientBelief<Double>(
				ID.Maintenance.BeliefBaseConst.correctiveRepairData);

		// belief for gui of maintenance agent
		Belief<MaintenanceGUI> gui = new TransientBelief<MaintenanceGUI>(
				ID.Maintenance.BeliefBaseConst.gui_maintenance);
		
		// belief for storing status of maintenance activity on machine
		Belief<PMaintenance> pmStatus = new TransientBelief<PMaintenance>(
				ID.Maintenance.BeliefBaseConst.prevMaintFromMachine);
		
		// belief for preventive maintenance activity inter-arrival time
		Belief<Long> maintPeriod = new TransientBelief<Long>(
				ID.Maintenance.BeliefBaseConst.maintenancePeriod);
		
		// belief for storing maximum time by which maintenance can be delayed without machine being received any warning
		Belief<Long> warningPeriod = new TransientBelief<Long>(
				ID.Maintenance.BeliefBaseConst.maintWarningPeriod);
		
		gui.setValue(null);
		warningPeriod.setValue(null);
		CorrectiveRepair.setValue(null);
		maintPeriod.setValue(null);
		myMachine.setValue(new SimulatorInternals());

		beliefs.add(bboard);
		beliefs.add(myMachine);
		beliefs.add(mygsAgent);
		beliefs.add(maintJob);
		beliefs.add(CorrectiveRepair);
		beliefs.add(gui);
		beliefs.add(pmStatus);
		beliefs.add(maintPeriod);
		beliefs.add(warningPeriod);

		return beliefs;
	}

	/**<b> Add plans for </b> <i>
	 * </br> register agent to DF (JADE)
	 * </br> registering agent to blackboard
	 * </br> checking current health status of machine
	 * </br> performing manual repair of machine when failed
	 * </br> sending information about maintenance activity when it starts
	 * </br> sending corrective repair data to machine
	 * </br> sending periodic preventive maintenance activities to machine
	 * </br> loading configuration for the agent
	 * </i>
	 * @return Set of plans associated with goals 
	 */
	public static Set<Plan> getPlans() {
		Set<Plan> plans = new HashSet<Plan>();

		// plan for registering maintenance agent service on DF (JADE)
		plans.add(new SimplePlan(RegisterMaintenanceAgentServiceGoal.class,
				RegisterMaintenanceAgentServicePlan.class));

		// plan for registering agent on blackboard
		plans.add(new SimplePlan(RegisterMaintenanceAgentToBlackboardGoal.class,
				RegisterMaintenanceAgentToBlackboardPlan.class));

		// plan for checking current health status of machine
		plans.add(new SimplePlan(machineHealthCheckGoal.class,
				machineHealthCheckPlan.class));

		// plan for performing manual repair of machine when failed
		plans.add(new SimplePlan(ManualMachineRepairGoal.class,
				ManualMachineRepairPlan.class));

		// plan for sending information about maintenance activity when it starts
		plans.add(new SimplePlan(MaintenanceStartSendInfoGoal.class,
				MaintenanceStartSendInfoPlan.class));

		// plan for sending corrective repair data to machine
		plans.add(new SimplePlan(SendCorrectiveRepairDataGoal.class,
				SendCorrectiveRepairDataPlan.class));

		// plan for sending periodic preventive maintenance activities to machine
		plans.add(new SimplePlan(PeriodicPreventiveMaintenanceGoal.class,
				PeriodicPreventiveMaintenancePlan.class));
		
		// plan for loading configuration for the agent
		plans.add(new SimplePlan(LoadConfigMaintGoal.class, LoadConfigMaintPlan.class));
		
		return plans;
	}	

	@Override
	protected void setup() {
		// add goals to the agent
		myAgent.addGoal(new RegisterMaintenanceAgentServiceGoal());
		myAgent.addGoal(new RegisterMaintenanceAgentToBlackboardGoal());
		myAgent.addGoal(new LoadConfigMaintGoal());
		myAgent.addGoal(new machineHealthCheckGoal());
		myAgent.addGoal(new ManualMachineRepairGoal());
	}
}
