package mas.machineproxy.behaviors;

import jade.core.behaviours.Behaviour;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import mas.jobproxy.job;
import mas.localSchedulingproxy.agent.LocalSchedulingAgent;
import mas.machineproxy.MachineStatus;
import mas.machineproxy.Methods;
import mas.machineproxy.Simulator;
import mas.machineproxy.gui.MachineGUI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadJobBehavior extends Behaviour {

	private static final long serialVersionUID = 1L;
	private job comingJob;
	boolean IsJobComplete;
	private String inspectionJobId = "0";
	private Logger log;
	private int step = 0;
	private long processingTime;
	private long passedTime;
	private Simulator machineSimulator = null;
	private MachineGUI gui;
	private ScheduledThreadPoolExecutor executor;

	public LoadJobBehavior(job comingJob, Simulator machineSim) {
		this.comingJob = comingJob;
		this.IsJobComplete = false;
		log = LogManager.getLogger();
		this.machineSimulator = machineSim;
		getDataStore().put(Simulator.simulatorStoreName, machineSimulator);
		gui = machineSimulator.getGui();
	}

	public void action() {

		switch(step) {
		// in step 0 generate processing times
		case 0:

			machineSimulator.setStatus(MachineStatus.PROCESSING);
			
			gui.setBatchNo(String.valueOf(machineSimulator.getCurrentBatch().getBatchNumber()));
			gui.setCustomerId(machineSimulator.getCurrentBatch().getCustomerId());
			gui.setBatch(machineSimulator.getCurrentBatch().getBatchId());
			gui.setJobNumber(String.valueOf(machineSimulator.getCurrentBatch().getCurrentJobNo()));
			
			gui.machineProcessing(comingJob.getJobID(), comingJob.getCurrentOperation().getJobOperationType());

			double ProcessingTimeInSeconds = comingJob.getCurrentOperationProcessingTime()/1000.0;
			comingJob.setCurrentOperationStartTime(System.currentTimeMillis());
			/*	log.info("Job No : '" + comingJob.getJobNo() + "' loading with" +
						"processing time before loading/unloading: " + comingJob.getCurrentOperationProcessTime());*/

			processingTime = comingJob.getCurrentOperationProcessingTime();
			passedTime = 0;

			if(processingTime <= 0) {
				processingTime = 1;// 1 milliseconds
				log.info("ATTENTION : -ve processing time");
			}

			log.info("Job No : " + comingJob.getJobNo() + " Batch No : "  +
					machineSimulator.getCurrentBatch().getBatchNumber() + " loading with" +
					"processing time : " + comingJob.getCurrentOperationProcessingTime());

			if( processingTime > 0 ) {
				executor = new ScheduledThreadPoolExecutor(1);
				executor.scheduleAtFixedRate(new timeProcessing(), 0,
						Simulator.TIME_STEP, TimeUnit.MILLISECONDS);
				step = 1;
			} else {
				log.info("Severe : negative processing time " );
			}
			//			}
			//			else if(comingJob.getJobID().equals(inspectionJobId)) { 
			//				log.info("Inspection Job loading");
			//				IsJobComplete = true;
			//				HandleInspectionJobBehavior inspector = 
			//						new HandleInspectionJobBehavior(comingJob);
			//				inspector.setDataStore(this.getDataStore());
			//				myAgent.addBehaviour(inspector);
			//			}
			break;

		case 1:
			// block for some time in order to avoid too much CPU usage
			// this won't affect working of the behavior however
			block(100);
			break;

		case 2:
			IsJobComplete = true;

			log.info("Job No:" + comingJob.getJobNo() + " operation " + 
					(comingJob.getCurrentOperationNumber() + 1 ) + "/" +
					comingJob.getOperations().size() + " completed");

			ProcessJobBehavior process = new ProcessJobBehavior(comingJob);
			process.setDataStore(getDataStore());
			myAgent.addBehaviour(process);
			machineSimulator.setStatus(MachineStatus.IDLE);
			gui.machineIdle();
			break;
		}
	}

	@Override
	public boolean done() {
		return (IsJobComplete);
	}

	/**
	 * If machine is failed it won't do anything.
	 * Executor will just keep scheduling this task
	 * 
	 * It is stuck in calling the executor task again and again until machine is failed
	 * or the user presses unload button
	 */
	class timeProcessing implements Runnable {

		@Override
		public void run() {
			if(machineSimulator.getStatus() != MachineStatus.FAILED &&
					machineSimulator.getStatus() != MachineStatus.PAUSED &&
					! machineSimulator.isUnloadFlag()) {

				processingTime = processingTime - Simulator.TIME_STEP; 
				passedTime += Simulator.TIME_STEP;

			} else if( machineSimulator.isUnloadFlag() )
				//					&& machineSimulator.getStatus() != MachineStatus.FAILED &&
				//					machineSimulator.getStatus() != MachineStatus.PAUSED)
			{
				step = 2;
				executor.shutdown();
			} 
		}
	}
}
