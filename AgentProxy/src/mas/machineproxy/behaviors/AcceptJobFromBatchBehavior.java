package mas.machineproxy.behaviors;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.MessageTemplate;
import mas.jobproxy.Batch;
import mas.jobproxy.job;
import mas.machineproxy.MachineStatus;
import mas.machineproxy.Simulator;
import mas.util.MessageIds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AcceptJobFromBatchBehavior extends Behaviour {

	private static final long serialVersionUID = 1L;
	private transient Logger log;
	private Batch currBatch;
	private transient job jobFromBatch;
	private transient Simulator machineSimulator;
	private int step = 0;

	public AcceptJobFromBatchBehavior(Simulator simulator) {
		log = LogManager.getLogger();

		machineSimulator = simulator;
		getDataStore().put(Simulator.simulatorStoreName, simulator);

	}

	@Override
	public void action() {
		if(machineSimulator.getStatus() != MachineStatus.FAILED) {

			switch(step) {
			case 0:
				currBatch = machineSimulator.getCurrentBatch();
				if (currBatch != null) {
					machineSimulator.setUnloadFlag(false);

					this.jobFromBatch = currBatch.getCurrentJob();
					currBatch.incrementCurrentJob();

					AddJobBehavior addjob = new AddJobBehavior(this.jobFromBatch);
					addjob.setDataStore(getDataStore());
					myAgent.addBehaviour(addjob);

					step = 1;
				} 
				else {
					block();
				}

				break;
			}
		}
	}

	@Override
	public boolean done() {
		return step >= 1;
	}

}
