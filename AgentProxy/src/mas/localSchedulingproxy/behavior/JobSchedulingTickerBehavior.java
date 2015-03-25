package mas.localSchedulingproxy.behavior;

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

public class JobSchedulingTickerBehavior extends TickerBehaviour {

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private ArrayList<job> jobQueue;
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
	}

	@Override
	protected void onTick() {

		if(jobQueue == null) {
			jobQueue = (ArrayList<job>) bfBase.
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
