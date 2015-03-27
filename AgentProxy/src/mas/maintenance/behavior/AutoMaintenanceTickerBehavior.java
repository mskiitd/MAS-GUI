package mas.maintenance.behavior;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

import java.util.ArrayList;

import mas.jobproxy.job;
import mas.jobproxy.jobOperation;
import mas.machineproxy.MachineStatus;
import mas.machineproxy.SimulatorInternals;
import mas.machineproxy.component.IComponent;
import mas.maintenanceproxy.plan.RepairKit;
import mas.util.ID;
import bdi4jade.core.BeliefBase;

public class AutoMaintenanceTickerBehavior extends TickerBehaviour{

	private static final long serialVersionUID = 1L;
	private RepairKit solver;
	private SimulatorInternals myMachine;
	private job maintenanceJob;
	private AID bbAgent;
	private BeliefBase bfBase;

	public AutoMaintenanceTickerBehavior(Agent a, long period) {
		super(a, period);
	}

	public AutoMaintenanceTickerBehavior(Agent a, long period,
			BeliefBase bfBase) {

		super(a, period);

		this.solver = new RepairKit();
		this.bfBase = bfBase;

		this.myMachine = (SimulatorInternals) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.machine).
				getValue();

		this.bbAgent = (AID) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgentAID).
				getValue();

	}

	@Override
	protected void onTick() {

		if(myMachine == null) {
			this.myMachine = (SimulatorInternals) bfBase.
					getBelief(ID.Maintenance.BeliefBaseConst.machine).
					getValue();
		}

		if(myMachine != null) {
			long startTime = (long) (System.currentTimeMillis() / 1000L);

			int componentRZoneCount = 0;
			int componentYZoneCount = 0;

			MachineStatus mstatus = myMachine.getStatus();
			if (mstatus != MachineStatus.UNDER_MAINTENANCE &&
					mstatus != MachineStatus.FAILED) {

				solver.setMachine(myMachine);
				double[] temprLife = solver.residualLife(startTime);
				double[] yellowZoneComponents = solver.yellowZone(startTime);
				double[] redZoneComponents = solver.redZone(startTime);	

				ArrayList<IComponent> components = myMachine.getComponents();
				for (int i = 0; i < components.size(); i++) {

					if (yellowZoneComponents[i] <= 0)
						componentYZoneCount++; 

					if (redZoneComponents[i] <= 0 )
						componentRZoneCount++;
				}


				if((componentYZoneCount >= 4 || componentRZoneCount > 0) &&
						mstatus != MachineStatus.FAILED &&
						mstatus != MachineStatus.UNDER_MAINTENANCE) {					 

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

					//					global.m[m_id].Ddate = solver.maintenanceJobDueDate(t);
					//					global.m[m_id].maint_job.dDate = (double) (System.currentTimeMillis() / 1000L)+global.m[m_id].Ddate - customer.Time;
					//					for(int i=0; i<3; i++){
					//						global.m[m_id].maint_job.dd[i]=global.m[m_id].maint_job.dDate;
					//						global.m[m_id].maint_job.l_dd[i]=global.m[m_id].maint_job.dDate;
					//						global.m[m_id].maint_job.g_dd[i]=global.m[m_id].maint_job.dDate;
					//					}
					//					global.m[m_id].maint_job.procTime = global.m[m_id].Maint_time(global.m[m_id].t);

					//					global.m[m_id].Penalty = 0;					
					//					global.m[m_id].t = (double) (System.currentTimeMillis() / 1000L);
					//					global.m[m_id].maint_job.Penalty = (int) global.m[m_id].Penalty;
					//
					//					global.m[m_id].maint_job.jobID = 0;
					//					global.m[m_id].maint_job.genTime=global.m[m_id].t-customer.Time;

					if(bbAgent == null) {
						this.bbAgent = (AID) bfBase.
								getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgentAID).
								getValue();
					}

					if(bbAgent != null) {
						myAgent.addBehaviour(new SendMaintenanceJobBehavior
								(this.maintenanceJob,this.bbAgent));
					}

					bfBase.updateBelief(ID.Maintenance.BeliefBaseConst.maintenanceJob,
							maintenanceJob);
				}
			}
		}

	}
}
