package mas.localSchedulingproxy.behavior;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

import java.awt.TrayIcon.MessageType;
import java.util.ArrayList;

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
		double lateness;			

		double totalRegret = 0;

		for ( int i = 0; i < qSize ; i++) {
			double batchLateness = 0;

			batchLateness =	jobQueue.get(i).getCurrentOperationStartTime() +
					jobQueue.get(i).getCurrentOperationProcessingTime() -
					jobQueue.get(i).getCurrentOperationDueDate();

			if(batchLateness < 0)
				batchLateness = 0;

			jobQueue.get(i).setRegret(batchLateness/jobQueue.get(i).getSlack());
			totalRegret += jobQueue.get(i).getRegret();
			//			lateness += batchLateness;
		}

		if(totalRegret > regretThreshold) {

			MachineGUI.showNotification("Scheduling", "Scheduling of batches starting", MessageType.INFO);
			
			reset(100 * LocalSchedulingAgent.schedulingPeriod);

			ScheduleSequence scheduler = new ScheduleSequence(jobQueue);
			ArrayList<Batch> newQ = scheduler.getSolution();

			log.info("updating belief base with the new schedule");
			bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.batchQueue,
					newQ);

			log.info("update new queue in the machine gui ");
			if(gui != null) {
				gui.updateQueue(newQ);
				MachineGUI.showNotification("Scheduling", "Scheduling of batches complete", MessageType.INFO);
			}

			reset(LocalSchedulingAgent.schedulingPeriod);
		}
	}

}
