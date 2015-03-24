package mas.localSchedulingproxy.plan;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

import java.util.ArrayList;

import mas.job.job;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;
import mas.localSchedulingproxy.algorithm.ScheduleSequence;
import mas.util.ID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class JobSchedulingPlan extends TickerBehaviour implements PlanBody  {

	private ArrayList<job> jobQueue;
	private double regretThreshold;
	private Logger log;

	public JobSchedulingPlan(Agent a, long period) {
		super(a, period);
		reset(LocalSchedulingAgent.schedulingPeriod);
	}

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		bfBase = pInstance.getBeliefBase();
		log = LogManager.getLogger();
	}

	@Override
	protected void onTick() {
		jobQueue = (ArrayList<job>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue).
				getValue();

		regretThreshold = (double) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.regretThreshold).
				getValue();

		calculateRegret();
	}

	private void calculateRegret() {
		int max = jobQueue.size();
		double lateness;			

		double totalRegret = 0;

		for ( int i = 0; i < max ; i++) {
			lateness =	jobQueue.get(i).getCurrentOperationStartTime() +
					jobQueue.get(i).getCurrentOperationProcessTime() -
					jobQueue.get(i).getCurrentOperationDueDate();

			if(lateness < 0)
				lateness = 0;

			jobQueue.get(i).setRegret(lateness/jobQueue.get(i).getSlack());
			totalRegret += jobQueue.get(i).getRegret();
		}

		if(totalRegret > regretThreshold) {
			reset(100 * LocalSchedulingAgent.schedulingPeriod);

			ScheduleSequence scheduler = new ScheduleSequence(jobQueue);
			ArrayList<job> newQ = scheduler.getSolution();

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
