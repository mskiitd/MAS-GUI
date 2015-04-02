package mas.machineproxy.behaviors;

import java.io.IOException;

import jade.core.behaviours.Behaviour;
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
		if(machineSimulator.getStatus() != MachineStatus.FAILED) {

			switch(step) {
			case 0:
				ACLMessage msg = myAgent.receive(batchMsgTemplate);
				
				if (msg != null) {
//					machineSimulator.setUnloadFlag(false);

					try {
						this.batchToProcess = (Batch) msg.getContentObject();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}

					log.info(" Batch No : '" + batchToProcess.getBatchNumber() +
							"'accepted with starting time : " +
							batchToProcess.getStartTimeMillis() + " due date: " +
							batchToProcess.getDueDateByCustomer() );

					machineSimulator.setCurrentBatch(batchToProcess);
//					step = 1;
					block(100);
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
