package mas.maintenanceproxy.plan;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;

import mas.job.job;
import mas.job.jobOperation;
import mas.machineproxy.IMachine;
import mas.maintenance.behavior.SendMaintenanceJobBehavior;
import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import mas.util.ID;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * @author Anand Prajapati
 */

public class PreventiveMaintenancePlan extends TickerBehaviour implements PlanBody {

	public PreventiveMaintenancePlan(Agent a, long period) {
		super(a, period);
		reset(LocalMaintenanceAgent.prevMaintPeriod);
	}

	private static final long serialVersionUID = 1L;
	private int step = 0;
	private BeliefBase bfBase;
	private IMachine myMachine;
	private job maintenanceJob;
	private RepairKit solver;
	private AID bbAgent;

	MessageTemplate mt = MessageTemplate.MatchConversationId("MJStart");

	@Override
	protected void onTick() {

		//		if (/*global.be==*/0) {
		if(myMachine != null) {

			long startTime = (long) (System.currentTimeMillis() / 1000L);

			long processingTime = (long) solver.totalMaintenanceTime(startTime);
			long duedate = (long) solver.maintenanceJobDueDate(startTime);

			this.maintenanceJob = new job.Builder("0").
					jobGenTime(System.currentTimeMillis()).
					jobPenalty(1).
					jobCPN(1).
					jobDueDateTime(duedate).
					build();

			ArrayList<jobOperation> mainOp = new ArrayList<jobOperation>();
			jobOperation op1 = new jobOperation();
			op1.setProcessingTime(processingTime);

			mainOp.add(op1);

			this.maintenanceJob.setOperations(mainOp);

			myAgent.addBehaviour(new SendMaintenanceJobBehavior
					(this.maintenanceJob,this.bbAgent));		

			bfBase.updateBelief(ID.Maintenance.BeliefBaseConst.maintenanceJob,
					maintenanceJob);
		}
	}

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {

		this.bfBase = pInstance.getBeliefBase();

		this.myMachine = (IMachine) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.machine).
				getValue();

		this.bbAgent = (AID) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgentAID).
				getValue();

		this.solver = new RepairKit();
	}

}