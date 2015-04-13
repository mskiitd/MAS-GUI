package mas.machineproxy.behaviors;

import java.io.IOException;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.jobproxy.Batch;
import mas.machineproxy.MachineStatus;
import mas.machineproxy.Simulator;
import mas.util.MessageIds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** 
 * @author Anand Prajapati
 * 
 * Machine simulator accepts the whole batch without user intervention and when user presses
 * load button job from batch is loaded.
 * behavior to accept first batch in the queue from the local scheduling agent
 * This behavior is paused when the machine is either paused or failed.
 */
public class AcceptBatchBehavior extends CyclicBehaviour {

	private static final long serialVersionUID = 1L;
	private transient MessageTemplate batchMsgTemplate;
	private transient Logger log;
	private transient Batch batchToProcess;
	private transient Simulator machineSimulator;

	private int step = 0;

	public AcceptBatchBehavior(Simulator sim) {
		log = LogManager.getLogger();

		machineSimulator = sim;
		getDataStore().put(Simulator.simulatorStoreName, sim);

		batchMsgTemplate = MessageTemplate.
				MatchConversationId(MessageIds.msgbatchForMachine);
	}

	@Override
	public void action() {
		if(machineSimulator.getStatus() != MachineStatus.FAILED &&
				machineSimulator.getStatus() != MachineStatus.PAUSED) {

			switch(step) {
			case 0:
				ACLMessage msg = myAgent.receive(batchMsgTemplate);

				if (msg != null) {
					//					machineSimulator.setUnloadFlag(false);

					try {
						this.batchToProcess = (Batch) msg.getContentObject();
						machineSimulator.setCurrentBatch(batchToProcess);
						log.info(" Batch No : '" + batchToProcess.getBatchNumber() +
								"'accepted with processing time : " + batchToProcess.getBatchProcessingTime());
						
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				} 
				else {
					block();
				}

				break;

			case 1:

				if(! batchToProcess.isAllJobsComplete()) {

					ACLMessage jobMsg = new ACLMessage(ACLMessage.INFORM);

					try {
						jobMsg.setContentObject(this.batchToProcess.getCurrentJob() );
					} catch (IOException e) {
						e.printStackTrace();
					}
					jobMsg.setConversationId(MessageIds.msgJobFromBatchForMachine);
					jobMsg.addReceiver(myAgent.getAID());
					myAgent.send(jobMsg);

					block(100);

					this.batchToProcess.incrementCurrentJob();
				} else {
					step = 2;
				}

				break;

			case 2:
				break;
			}
		}
	}

}
