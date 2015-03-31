package mas.localSchedulingproxy.behavior;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

import java.util.ArrayList;

import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;
import mas.localSchedulingproxy.algorithm.ScheduleSequence;
import mas.util.ID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;

public class JobSchedulingTickerBehavior extends TickerBehaviour {

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private ArrayList<Batch> jobQueue;
	private double regretThreshold = 0;
	private Logger log;

	public JobSchedulingTickerBehavior(Agent a, long period) {
		super(a, period);
		reset(LocalSchedulingAgent.schedulingPeriod);
	}

	public JobSchedulingTickerBehavior(Agent myAgent, long schedulingPeriod,
			BeliefBase bfBase) {
		super(myAgent, schedulingPeriod);
		this.bfBase = bfBase;

		log = LogManager.getLogger();
	}

	@Override
	protected void onTick() {

		if(jobQueue == null) {
			jobQueue = (ArrayList<Batch>) bfBase.
					getBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue).
					getValue();
		}

		if(regretThreshold == 0) {
			regretThreshold = (double) bfBase.
					getBelief(ID.LocalScheduler.BeliefBaseConst.regretThreshold).
					getValue();
		}

		if(jobQueue != null) {
			calculateRegretAndSchedule();
		}
	}

	private void calculateRegretAndSchedule() {
		int qSize = jobQueue.size();
		double lateness;			

		double totalRegret = 0;

		for ( int i = 0; i < qSize ; i++) {
			double batchLateness = 0;
			Batch currentBatch = jobQueue.get(i);
			for(int jobIndex = 0 ; jobIndex < currentBatch.getBatchCount() ; jobIndex ++ ) {
				
				job currentJobInBatch = currentBatch.getJobsInBatch().get(jobIndex);
				batchLateness =	currentJobInBatch.getCurrentOperationStartTime() +
						currentJobInBatch.getCurrentOperationProcessTime() -
						currentJobInBatch.getCurrentOperationDueDate();

				if(batchLateness < 0)
					batchLateness = 0;

				currentJobInBatch.setRegret(batchLateness/currentJobInBatch.getSlack());
				totalRegret += currentJobInBatch.getRegret();
			}
//			lateness += batchLateness;
		}

		if(totalRegret > regretThreshold) {

			reset(100 * LocalSchedulingAgent.schedulingPeriod);

			ScheduleSequence scheduler = new ScheduleSequence(jobQueue);
			ArrayList<Batch> newQ = scheduler.getSolution();

			log.info("updating belief base with the new schedule");
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue,
					newQ);

			log.info("update new queue in the machine gui ");
			if(LocalSchedulingAgent.mGUI != null) {
				LocalSchedulingAgent.mGUI.updateQueue(newQ);
			}

			reset(LocalSchedulingAgent.schedulingPeriod);
		}
	}

}
