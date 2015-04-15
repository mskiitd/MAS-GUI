package mas.machineproxy.behaviors;

import java.util.Date;

import jade.core.behaviours.Behaviour;
import mas.jobproxy.Batch;
import mas.jobproxy.job;
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
	private transient Simulator machineSimulator;

	public HandleCompletedJobBehavior(job comingJob,Simulator sim) {

		this.completedJob = comingJob;
		this.log = LogManager.getLogger();

		machineSimulator = sim;
		getDataStore().put(Simulator.simulatorStoreName, sim);
	}

	@Override
	public void action() {

		switch(step) {
		case 0:

			if(machineSimulator.getCurrentBatch().isAllJobsComplete()) {
				
				Batch cBatch = machineSimulator.getCurrentBatch();
				cBatch.updateJob(completedJob);
				machineSimulator.setCurrentJob(null);
				machineSimulator.setCurrentBatch(cBatch);
				/**
				 * update zone-data for completed jobs from machine
				 */
				ZoneDataUpdate completedJobUpdate = new ZoneDataUpdate.Builder(ID.Machine.ZoneData.finishedBatch)
				.value(cBatch).Build();

				AgentUtil.sendZoneDataUpdate(Simulator.blackboardAgent ,
						completedJobUpdate, myAgent);
				
				myAgent.addBehaviour(new GiveMeJobBehavior());
				machineSimulator.setCurrentBatch(null);

				log.info("batch no: '"+ cBatch.getBatchNumber() + 
						"'\tbatch ID : " + cBatch.getBatchId() + 
						"\n\tcompletion : " + new Date(cBatch.getCompletionTime()) + 
						"\n\tStarting time : " + new Date(cBatch.getStartTimeMillis()) + 
						"\n\tDue date : " + cBatch.getDueDateByCustomer());
				
				log.info("sending completed batch to blackboard");
			} else {
				Batch cBatch = machineSimulator.getCurrentBatch();
				cBatch.updateJob(completedJob);
				machineSimulator.setCurrentBatch(cBatch);
			}

			step = 1;

			break;
		}
	}

	@Override
	public boolean done() {
		return step == 1;
	}
}
