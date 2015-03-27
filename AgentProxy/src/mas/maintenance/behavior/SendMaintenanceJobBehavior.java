package mas.maintenance.behavior;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import mas.jobproxy.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

/**
 * @author Anand Prajapati
 * 
 * Behavior to send the generated preventive maintenance job to blackboard
 *
 */

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

		ZoneDataUpdate maintenanceJob = new ZoneDataUpdate.Builder(ID.Maintenance.ZoneData.preventiveMaintJob)
			.value(this.maintJob).Build();
		
		AgentUtil.sendZoneDataUpdate(this.bbAgent ,maintenanceJob, myAgent);
		step = 1;
	}

	@Override
	public boolean done() {
		return step > 0;
	}

}
