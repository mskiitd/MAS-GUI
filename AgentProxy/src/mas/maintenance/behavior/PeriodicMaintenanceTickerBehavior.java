package mas.maintenance.behavior;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import java.util.ArrayList;
import mas.jobproxy.job;
import mas.jobproxy.jobOperation;
import mas.machineproxy.SimulatorInternals;
import mas.maintenanceproxy.agent.LocalMaintenanceAgent;
import mas.util.ID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.core.BeliefBase;

public class PeriodicMaintenanceTickerBehavior extends TickerBehaviour{

	private static final long serialVersionUID = 1L;
	private SimulatorInternals myMachine;
	private job maintenanceJob;
	private AID bbAgent;
	private BeliefBase bfBase;
	private Logger log;
	private long maintDownTime = 10000;

	public PeriodicMaintenanceTickerBehavior(Agent a, long period) {
		super(a, period);
	}

	public PeriodicMaintenanceTickerBehavior(Agent a, long period,
			BeliefBase bfBase) {
		super(a, period);
		reset(LocalMaintenanceAgent.prevMaintPeriod);
		this.bfBase = bfBase;

		this.myMachine = (SimulatorInternals) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.machine).
				getValue();

		this.bbAgent = (AID) bfBase.
				getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgentAID).
				getValue();

		log = LogManager.getLogger();

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

			// set correct processing time and due date for maintenance job
			long processingTime =  maintDownTime;
			long duedate = (long) (startTime);

			this.maintenanceJob = new job.Builder("0").
					jobDueDateTime(duedate).
					build();

			ArrayList<jobOperation> mainOp = new ArrayList<jobOperation>();
			jobOperation op1 = new jobOperation();
			op1.setProcessingTime(processingTime);

			mainOp.add(op1);

			this.maintenanceJob.setOperations(mainOp);

			if(bbAgent == null) {
				this.bbAgent = (AID) bfBase.
						getBelief(ID.Maintenance.BeliefBaseConst.blackboardAgentAID).
						getValue();
			}

			if(bbAgent != null) {
				myAgent.addBehaviour(new SendMaintenanceJobBehavior
						(this.maintenanceJob,this.bbAgent));
				log.info("Sending Periodic maintenance job ");
			}

			bfBase.updateBelief(ID.Maintenance.BeliefBaseConst.maintenanceJob,
					maintenanceJob);
		}
	}

}
