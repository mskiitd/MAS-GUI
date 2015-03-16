package mas.maintenance.behavior;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import mas.job.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

public class SendMaintenanceJobBehavior extends Behaviour{

	private int step = 0;
	private static final long serialVersionUID = 1L;
	private job maintJob;
	private AID bbAgent;
	
	public SendMaintenanceJobBehavior(job jobToSend, AID blackboard) {
		this.maintJob = jobToSend;
		this.bbAgent = blackboard;
	}
	
	@Override
	public void action() {

		ZoneDataUpdate maintenanceJob = new ZoneDataUpdate(
				ID.Maintenance.ZoneData.preventiveMaintJob,
				this.maintJob);

		AgentUtil.sendZoneDataUpdate(this.bbAgent ,maintenanceJob, myAgent);
		step = 1;
	}

	@Override
	public boolean done() {
		return step > 0;
	}

}
