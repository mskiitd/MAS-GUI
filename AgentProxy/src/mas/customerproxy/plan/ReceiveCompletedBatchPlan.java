package mas.customerproxy.plan;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import mas.customerproxy.agent.CustomerAgent;
import mas.jobproxy.Batch;
import mas.jobproxy.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class ReceiveCompletedBatchPlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private Logger log;
	private Batch completedBatch;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		ACLMessage msg = ( (MessageGoal)pInstance.getGoal() ).getMessage();
		try {
			completedBatch = (Batch) msg.getContentObject();

		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() {
//		log.info(arg0);
		if(completedBatch != null && completedBatch.getCustomerId().equals(myAgent.getAID())) {
			log.info("Adding completed job to GUI ");
			CustomerAgent.mygui.addCompletedBatch(completedBatch);
		}
	}

	@Override
	public boolean done() {
		return true;
	}

}
