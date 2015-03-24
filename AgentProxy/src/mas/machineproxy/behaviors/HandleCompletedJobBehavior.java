package mas.machineproxy.behaviors;

import jade.core.behaviours.Behaviour;
import mas.job.job;
import mas.machineproxy.Simulator;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HandleCompletedJobBehavior extends Behaviour{

	private static final long serialVersionUID = 1L;
	private job completedJob;
	private Logger log;
	private int step = 0;

	public HandleCompletedJobBehavior(job comingJob) {

		this.completedJob = comingJob;
		this.log = LogManager.getLogger();
	}

	@Override
	public void action() {
		switch(step) {
		case 0:
			myAgent.addBehaviour(new GiveMeJobBehavior());
			/**
			 * update zone-data for completed jobs from machine
			 */
			ZoneDataUpdate completedJobUpdate = new ZoneDataUpdate.Builder(ID.Machine.ZoneData.finishedJob)
				.value(completedJob).Build();
			
			/*ZoneDataUpdate completedJobUpdate = new ZoneDataUpdate(
					ID.Machine.ZoneData.finishedJob,
					completedJob);*/

			AgentUtil.sendZoneDataUpdate(Simulator.blackboardAgent ,
					completedJobUpdate, myAgent);

			log.info("Job no: '"+ completedJob.getJobNo() + 
					"Job ID : " + completedJob.getJobID() + 
					"'\n--> completion : " + completedJob.getCompletionTime() + 
					"\nStarting time : " + completedJob.getStartTimeByCust() + 
					"\nDue date : " + completedJob.getJobDuedatebyCust());
			log.info("sending completed job to blackboard");

			step = 1;

			break;

		case 1:
			break;
		}
	}

	@Override
	public boolean done() {
		return step >= 1;
	}
}
