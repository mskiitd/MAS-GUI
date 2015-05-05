package mas.localSchedulingproxy.behavior;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

import java.awt.TrayIcon.MessageType;
import java.util.ArrayList;
import java.util.Date;

import mas.jobproxy.Batch;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;
import mas.localSchedulingproxy.algorithm.ScheduleSequence;
import mas.machineproxy.gui.MachineGUI;
import mas.util.ID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;

/**
 * @author Anand Prajapati
 * Behavior to performing scheduling, if required, at regular intervals
 *
 */
public class JobSchedulingTickerBehavior extends TickerBehaviour {

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private ArrayList<Batch> jobQueue;
	private double regretThreshold = -1;
	private Logger log;
	private MachineGUI gui;

	public JobSchedulingTickerBehavior(Agent a, long period) {
		super(a, period);
		reset(LocalSchedulingAgent.schedulingPeriod);
	}

	public JobSchedulingTickerBehavior(Agent myAgent, long schedulingPeriod,
			BeliefBase bfBase) {

		super(myAgent, schedulingPeriod);
		this.bfBase = bfBase;
		gui = (MachineGUI) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.gui_machine).
				getValue();

		log = LogManager.getLogger();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onTick() {

		jobQueue = (ArrayList<Batch>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue).
				getValue();

		regretThreshold = (double) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.regretThreshold).
				getValue();

		if(jobQueue != null) {
			calculateRegretAndSchedule();
		}
	}

	/**
	 * Performs scheduling of the current sequence of batches if the total regret of the sequence
	 * has gone beyond some threshold.
	 * Schedules the sequence of batches based on processing time of only the current operation.
	 */
	@SuppressWarnings("unchecked")
	private void calculateRegretAndSchedule() {
		int qSize = jobQueue.size();

		double totalRegret = 0;

		for ( int i = 0; i < qSize ; i++) {
			double batchLateness = 0;

			batchLateness =	jobQueue.get(i).getCurrentOperationStartTime()-
					jobQueue.get(i).getCurrentOperationDueDate() +
					jobQueue.get(i).getCurrentOperationProcessingTime();

			// convert lateness into seconds
			batchLateness = batchLateness/1000;

			log.info("latesness : "+ batchLateness + " s, " + "No : " + jobQueue.get(i).getBatchNumber());
			log.info("start time " + new Date(jobQueue.get(i).getCurrentOperationStartTime()) );
			log.info("proc time " + jobQueue.get(i).getCurrentOperationProcessingTime()/1000 + " sec" );
			log.info("Due date " + new Date(jobQueue.get(i).getCurrentOperationDueDate()) );

			if(batchLateness < 0)
				batchLateness = 0;

			if(jobQueue.get(i).getSlack() > 0) {
				jobQueue.get(i).setRegret(batchLateness/jobQueue.get(i).getSlack());
			}
			else {
				//set to some high value as the job doesn't have any slack at all 
				jobQueue.get(i).setRegret(Double.MAX_VALUE/100.0);
				// divide by 100 to avoid overflow in totallRegret
			}
			totalRegret += jobQueue.get(i).getRegret();
			log.info("regret for batch no "+jobQueue.get(i).getBatchNumber()+" = "+jobQueue.get(i).getRegret());
			//			lateness += batchLateness;
		}
		log.info("total regret : " + totalRegret);
		if(totalRegret > regretThreshold) {

			reset(10 * LocalSchedulingAgent.schedulingPeriod);
			log.info("old queue : " + jobQueue);
			ScheduleSequence scheduler = new ScheduleSequence(jobQueue);
			ArrayList<Batch> newQ = scheduler.getSolution();

			ArrayList<Batch> currQ = (ArrayList<Batch>) bfBase.
					getBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue).
					getValue();

			log.info("current Q " + currQ);
			log.info("new Q "  + newQ);

			// merge the current queue with the queue obtained from rescheduling
			for(int i = 0; i < currQ.size(); i++ ) {
				// if batch from current Q isn't in the new Q that means that batch would have arrived while rescheduling
				if(! newQ.contains(currQ.get(i))) {
					newQ.add(currQ.get(i));
				}
			}

			// if an element of newQ isn't there in the current Queue that means that that
			// batch was processed while rescheduling was going on
			for(int i = 0 ; i < newQ.size(); i++) {
				if(! currQ.contains(newQ.get(i))) {
					newQ.remove(i);
				}
			}

			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue,
					newQ);

			log.info("update new queue in the machine gui " + newQ);
			if(gui != null) {
				gui.updateQueue(newQ);
				gui.showNotification("Scheduling", "Scheduling of batches complete", MessageType.INFO);
			}

			reset(LocalSchedulingAgent.schedulingPeriod);
		}
	}

}
