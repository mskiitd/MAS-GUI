package mas.machineproxy.behaviors;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.jobproxy.job;
import mas.machineproxy.MachineStatus;
import mas.machineproxy.Simulator;
import mas.util.MessageIds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AcceptJobBehavior extends CyclicBehaviour {

	private static final long serialVersionUID = 1L;
	private transient MessageTemplate jobMsgTemplate;
	private transient Logger log;
	private transient job jobToProcess;
	private transient Simulator machineSimulator;

	public AcceptJobBehavior(Simulator sim) {
		log = LogManager.getLogger();

		machineSimulator = sim;
		getDataStore().put(Simulator.simulatorStoreName, sim);

		jobMsgTemplate = MessageTemplate.
				MatchConversationId(MessageIds.msgjobForMachine);
	}

	@Override
	public void action() {
		if(machineSimulator.getStatus() != MachineStatus.FAILED) {
			try {

				ACLMessage msg = myAgent.receive(jobMsgTemplate);
				if (msg != null) {
					this.jobToProcess = (job) msg.getContentObject();

					log.info(" Job No : '" + jobToProcess.getJobNo() +
							"'accepted with starting time : " +
							jobToProcess.getStartTimeByCust()+" due date: "+jobToProcess.getJobDuedatebyCust());

					AddJobBehavior addjob = new AddJobBehavior(this.jobToProcess);
					addjob.setDataStore(getDataStore());
					myAgent.addBehaviour(addjob);
				} 
				else {
					block();
				}
			} catch (UnreadableException e3) {
				log.debug("Error in parsing job");
			}
		}
	}
}
