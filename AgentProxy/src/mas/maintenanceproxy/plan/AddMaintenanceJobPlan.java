package mas.maintenanceproxy.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;

import mas.job.job;
import mas.machineproxy.IMachine;
import mas.machineproxy.MachineStatus;
import mas.machineproxy.component.IComponent;
import mas.maintenance.behavior.SendMaintenanceJobBehavior;
import mas.util.ID;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * 
 * @author Anand Prajapati
 *
 */

public class AddMaintenanceJobPlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private int step = 0;
	private BeliefBase bfBase;
	private IMachine myMachine;
	private job maintenanceJob;
	private RepairKit solver;
	private AID bAgent;
	MessageTemplate mt = MessageTemplate.MatchConversationId("MJStart");

	@Override
	public void action() {

//		if (/*global.be==*/0) {

		if(true) { 
			long t = (long) (System.currentTimeMillis() / 1000L);

			int componentRZoneCount = 0;
			int componentYZoneCount = 0;

			MachineStatus mstatus = myMachine.getStatus();
			if (mstatus != MachineStatus.UNDER_MAINTENANCE &&
					mstatus != MachineStatus.FAILED) {

				double[] temprLife = solver.residualLife(t);
				double[] yellowZoneComponents = solver.yellowZone(t);
				double[] redZoneComponents = solver.redZone(t);	

				ArrayList<IComponent> components = myMachine.getComponents();
				for (int i = 0; i < components.size(); i++) {

					if (yellowZoneComponents[i] <= 0)
						componentYZoneCount++; 

					if (redZoneComponents[i] <= 0 )
						componentRZoneCount++;
				}


				if((componentYZoneCount >= 4 || componentRZoneCount > 0) &&
						//						global.m[m_id].flag==false &&
						mstatus != MachineStatus.FAILED &&
						mstatus != MachineStatus.UNDER_MAINTENANCE) {					 

					//					global.m[m_id].flag = true;

					long processingTime = (long) solver.totalMaintenanceTime(t);
					long duedate = (long) solver.maintenanceJobDueDate(t);
					
					this.maintenanceJob = new job.Builder("0").
							jobGenTime(System.currentTimeMillis()).
							jobPenaltyRate(1).
							jobDueDateTime(duedate).
							build();

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

					myAgent.addBehaviour(new SendMaintenanceJobBehavior
								(this.maintenanceJob,this.bAgent));			
				}
			}
		}
	}

	@Override
	public EndState getEndState() {
		return null;
	}
	
	@Override
	public void init(PlanInstance pInstance) {
		this.bfBase = pInstance.getBeliefBase();
		this.myMachine = (IMachine) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.machine).
				getValue();

		this.maintenanceJob = (job) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.maintenanceJob).
				getValue();
		
		this.bAgent = (AID) bfBase.
						getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgent).
						getValue();

		this.solver = new RepairKit();
	}
}