package mas.customerproxy.plan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import mas.customerproxy.agent.CustomerAgent;
import mas.job.job;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class ReceiveCompletedJobPlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private Logger log;
	private job completedJob;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		ACLMessage msg = ( (MessageGoal)pInstance.getGoal() ).getMessage();
		try {
			completedJob = (job) msg.getContentObject();

		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() {
		if(completedJob != null) {
			log.info("Adding completed job to GUI ");
			CustomerAgent.mygui.addCompletedJob(completedJob);
		}
	}

	@Override
	public boolean done() {
		return true;
	}

}
