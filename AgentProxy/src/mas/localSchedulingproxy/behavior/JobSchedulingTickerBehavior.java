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

		if(jobQueue == null) {
			jobQueue = (ArrayList<Batch>) bfBase.
					getBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue).
					getValue();
		}

		if(regretThreshold == -1) {
			regretThreshold = (double) bfBase.
					getBelief(ID.LocalScheduler.BeliefBaseConst.regretThreshold).
					getValue();
		}

		if(jobQueue != null) {
			calculateRegretAndSchedule();
		}
	}

	/**
	 * Schedules the sequence of batches based on processing time of current operation
	 */
	
	private void calculateRegretAndSchedule() {
		int qSize = jobQueue.size();

		double totalRegret = 0;

		for ( int i = 0; i < qSize ; i++) {
			double batchLateness = 0;

			batchLateness =	jobQueue.get(i).getCurrentOperationStartTime()-
					jobQueue.get(i).getCurrentOperationDueDate() +
					jobQueue.get(i).getCurrentOperationProcessingTime();
			
			
			//variable name should be batchEarliness maybe??
			
			log.info("latesness : "+ batchLateness);
			log.info("start time " + new Date(jobQueue.get(i).getCurrentOperationStartTime()) );
			log.info("proc time " + jobQueue.get(i).getCurrentOperationProcessingTime() );
			log.info("finish time " + new Date(jobQueue.get(i).getCurrentOperationDueDate()) );

			if(batchLateness < 0)
				batchLateness = 0;
			if(jobQueue.get(i).getSlack()>0){
				jobQueue.get(i).setRegret(batchLateness/jobQueue.get(i).getSlack());
			}
			else{
				jobQueue.get(i).setRegret(Double.MAX_VALUE/100.0);
				// divide by 100 to avoid overflow in totallRegret
			}
			totalRegret += jobQueue.get(i).getRegret();
			log.info("regret for "+jobQueue.get(i).getBatchNumber()+" = "+jobQueue.get(i).getRegret());
			//			lateness += batchLateness;
		}
		log.info("total regret : " + totalRegret);
		if(totalRegret > regretThreshold) {

			if(gui != null) {
				gui.showNotification("Scheduling", "Scheduling of batches starting", MessageType.INFO);
			}

			reset(100 * LocalSchedulingAgent.schedulingPeriod);
			log.info("old queue : " + jobQueue);
			ScheduleSequence scheduler = new ScheduleSequence(jobQueue);
			ArrayList<Batch> newQ = scheduler.getSolution();

			log.info("updating belief base with the new schedule");
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
