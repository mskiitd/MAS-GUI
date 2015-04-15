package mas.machineproxy.behaviors;

import jade.core.behaviours.OneShotBehaviour;
import java.util.Date;
import mas.jobproxy.job;
import mas.jobproxy.jobOperation;
import mas.machineproxy.Simulator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProcessJobBehavior extends OneShotBehaviour{

	private static final long serialVersionUID = 1L;
	private job comingJob;
	private Logger log;
	private Simulator machineSimulator;

	public ProcessJobBehavior(job processJob) {
		this.comingJob = processJob;
		machineSimulator = null;
	}

	@Override
	public void action() {

		log = LogManager.getLogger();

		if(machineSimulator == null) {
			machineSimulator = (Simulator) getDataStore().
					get(Simulator.simulatorStoreName);
		}

		jobOperation ops = comingJob.getCurrentOperation();
		// Assign dimensions to the job
//		ArrayList<jobDimension> jDimensions = ops.getjDims();
//		int numDims = jDimensions.size();
//		int dIndex;
//
//		BinomialDistribution bernoulli =
//				new BinomialDistribution(1, machineSimulator.getFractionDefective());
//
//		boolean conforming;
//
//		for(dIndex = 0; dIndex < numDims; dIndex++) {
//
//			jDimensions.get(dIndex).setTargetDimension(
//					jDimensions.get(dIndex).getTargetDimension() +
//					Methods.normalRandom(machineSimulator.getMean_shift(),
//							machineSimulator.getSd_shift()));
//
//			conforming = (bernoulli.sample()==1)? Boolean.TRUE :Boolean.FALSE;
//			jDimensions.get(dIndex).setConforming(conforming);
//		}
//		comingJob.setCurrentOperationDimension(jDimensions);
		comingJob.setCurrentOperationCompletionTime(System.currentTimeMillis());
		
		log.info("start time was " + comingJob.getCurrentOperationStartTime() );
		log.info("Completion time : " + comingJob.getCurrentOperationFinishTime());
//		log.info("processed for "+ (comingJob.getCompletionTime().getTime() - comingJob.getStartTimeByCust().getTime()));

		// send completed job to blackboard in handleCompletedJobBehavior
		myAgent.addBehaviour(new HandleCompletedJobBehavior(comingJob,machineSimulator));
	}
}
